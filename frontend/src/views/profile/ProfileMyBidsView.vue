<template>
  <div class="my-bids-view">
    <h2>{{ $t('MY_BIDS_TITLE') }}</h2>

    <div v-if="isLoading" class="loading-indicator">
      <p>{{ $t('LOADING_BIDS') }}</p>
    </div>

    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
    </div>

    <div v-else-if="myBids.length > 0" class="bids-list">
      <div v-for="bid in sortedBids" :key="bid.id" class="bid-card-my" :class="`bid-status-${bid.status.toLowerCase()}`">
        <div class="bid-details">
          <p>
            <span>{{ $t('ON_ITEM') }}: </span>
            <router-link :to="{ name: 'item-detail', params: { id: bid.itemId } }" class="item-link">
              {{ getItemTitle(bid.itemId) || `${$t('ITEM')} #${bid.itemId}` }}
            </router-link>
          </p>
          <p><strong>{{ $t('YOUR_BID_AMOUNT') }}:</strong> {{ formatPrice(bid.amount) }}</p>
          <p v-if="bid.comment"><strong>{{ $t('YOUR_COMMENT') }}:</strong> {{ bid.comment }}</p>
          <p><strong>{{ $t('STATUS') }}:</strong> <span class="bid-status-badge">{{ $t(bid.status) }}</span></p>
          <p class="bid-timestamp">{{ $t('PLACED') }}: {{ formatDateTime(bid.createdAt) }}</p>
          <p v-if="bid.createdAt !== bid.updatedAt" class="bid-timestamp">{{ $t('UPDATED') }}: {{ formatDateTime(bid.updatedAt) }}</p>
        </div>
        <div class="bid-actions-my">
          <template v-if="bid.status === 'PENDING'">
            <button @click="openUpdateBidModal(bid)" class="edit-button" :disabled="actionLoading === bid.id">{{ $t('UPDATE_BID') }}</button>
            <button @click="promptDeleteBid(bid)" class="delete-button" :disabled="actionLoading === bid.id && bid.id === bidToDelete?.id">
              <span v-if="actionLoading === bid.id && bid.id === bidToDelete?.id">...</span><span v-else>{{ $t('DELETE') }}</span>
            </button>
          </template>
          <span v-else-if="bid.status === 'ACCEPTED'" class="status-indicator accepted">{{ $t('ACCEPTED') }}</span>
          <span v-else-if="bid.status === 'REJECTED'" class="status-indicator rejected">{{ $t('REJECTED') }}</span>
        </div>
      </div>
    </div>

    <div v-else class="no-items-message">
      <p>{{ $t('NO_BIDS_PLACED') }}</p>
    </div>

    <div v-if="actionError" class="error-message action-error">
      {{ actionError }}
    </div>

    <BidModal
      :is-open="isBidModalOpen"
      :item-id="bidToEdit?.itemId ?? ''"
      :item-title="bidToEdit ? getItemTitle(bidToEdit.itemId) : ''"
      :current-price="bidToEdit ? null : null"
      :initial-bid="bidToEdit"
      @close="closeBidModal"
      @bid-placed="handleBidResult"
      @bid-updated="handleBidResult"
    />

    <ConfirmDeleteModal
      :is-open="showDeleteConfirmModal"
      :message="deleteConfirmationMessage"
      :title="$t('CONFIRM_BID_DELETION')"
      :confirm-text="$t('DELETE_BID')"
      @confirm="confirmDeleteBid"
      @cancel="cancelDeleteBid"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
// --- Import services and types ---
import { fetchUserBids, deleteMyBid } from '@/services/BidService';
import { fetchItem } from '@/services/ItemService';
import type { BidResponseType } from '@/models/Bid';
// --- Import Modals ---
import BidModal from '@/components/modals/BidModal.vue';
import ConfirmDeleteModal from '@/components/modals/ConfirmDeleteModal.vue'; // <-- Import the new modal

// --- Component State ---
const myBids = ref<BidResponseType[]>([]);
const itemDetailsCache = ref<Record<string | number, string>>({});
const isLoading = ref(false);
const actionLoading = ref<string | number | null>(null);
const error = ref<string | null>(null);
const actionError = ref<string | null>(null);

// State for Bid Modal (Update)
const isBidModalOpen = ref(false);
const bidToEdit = ref<BidResponseType | null>(null);

// --- State for Delete Confirmation Modal ---
const showDeleteConfirmModal = ref(false);
const bidToDelete = ref<BidResponseType | null>(null);
const deleteConfirmationMessage = ref('');

// --- Load Bids ---
async function loadMyBids() {
  isLoading.value = true;
  error.value = null;
  actionError.value = null;
  try {
    myBids.value = await fetchUserBids(); // Assumes fetchUserBids handles potential string response
    await fetchItemTitlesForBids(myBids.value);
  } catch (err: any) {
    console.error("Failed to load user bids:", err);
    error.value = err.message || 'Could not load your bids. Please try again later.';
    myBids.value = [];
  } finally {
    isLoading.value = false;
  }
}

// --- Fetch Item Titles ---
async function fetchItemTitlesForBids(bidsToFetch: BidResponseType[]) {
  const itemIdsToFetch = bidsToFetch
  .map(bid => bid.itemId)
  .filter((itemId): itemId is string | number => itemId != null && !itemDetailsCache.value[itemId]); // Type guard

  const uniqueItemIds = [...new Set(itemIdsToFetch)];

  if (uniqueItemIds.length === 0) return;

  console.log(`Workspaceing titles for item IDs: ${uniqueItemIds.join(', ')}`);

  const titlePromises = uniqueItemIds.map(id =>
    fetchItem(id)
    .then(itemData => ({ id, title: itemData.title }))
    .catch(err => {
      console.warn(`Could not fetch details for item ${id}:`, err);
      return { id, title: `Item #${id}` }; // Fallback title
    })
  );

  const results = await Promise.all(titlePromises);

  results.forEach(result => {
    if (result) {
      itemDetailsCache.value[result.id] = result.title;
    }
  });
}

// --- Get Item Title ---
function getItemTitle(itemId: string | number | null): string {
  if (itemId === null || itemId === undefined) return 'Unknown Item';
  return itemDetailsCache.value[itemId] || `Item #${itemId}`;
}


// --- Delete Bid Logic (Using Custom Modal) ---
function promptDeleteBid(bid: BidResponseType) {
  if (!bid.itemId) return;
  bidToDelete.value = bid;
  deleteConfirmationMessage.value = `Are you sure you want to delete your bid of ${formatPrice(bid.amount)} on "${getItemTitle(bid.itemId)}"? This action cannot be undone.`;
  // Set state to show the modal
  showDeleteConfirmModal.value = true;
}

// This function is now called when the ConfirmDeleteModal emits @confirm
async function confirmDeleteBid() {
  if (!bidToDelete.value || !bidToDelete.value.itemId || actionLoading.value) return;

  const bidId = bidToDelete.value.id; // Store id for loading indicator
  actionLoading.value = bidId;
  actionError.value = null;
  showDeleteConfirmModal.value = false; // Close the modal

  try {
    await deleteMyBid(bidToDelete.value.itemId);
    bidToDelete.value = null; // Clear the bid to delete
    await loadMyBids(); // Refresh list
  } catch (err: any) {
    console.error("Failed to delete bid:", err);
    actionError.value = err.message || "Could not delete the bid.";
  } finally {
    // Ensure loading state is cleared only if it matches the current action
    if (actionLoading.value === bidId) {
      actionLoading.value = null;
    }
  }
}

// This function is now called when the ConfirmDeleteModal emits @cancel
function cancelDeleteBid() {
  showDeleteConfirmModal.value = false;
  bidToDelete.value = null;
}
// --- End Delete Bid Logic ---


// --- Update Bid Logic (Keep as is) ---
function openUpdateBidModal(bid: BidResponseType) {
  if (!bid.itemId) {
    console.error("Cannot edit bid without item ID:", bid);
    actionError.value = "Cannot edit this bid: missing item information.";
    return;
  }
  bidToEdit.value = bid;
  isBidModalOpen.value = true;
}

function closeBidModal() {
  isBidModalOpen.value = false;
  bidToEdit.value = null;
}

function handleBidResult() {
  closeBidModal();
  loadMyBids();
}
// --- End Update Bid Logic ---


// --- Computed: Sorted Bids (Keep as is) ---
const sortedBids = computed(() => {
  return [...myBids.value].sort((a, b) => {
    const dateA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
    const dateB = b.createdAt ? new Date(b.createdAt).getTime() : 0;
    return dateB - dateA;
  });
});

// --- Formatting Helpers (Keep as is) ---
function formatPrice(price: number | null | undefined): string {
  if (price === null || price === undefined) return 'N/A';
  return price.toLocaleString('no-NO', { style: 'currency', currency: 'NOK' });
}

function formatDateTime(dateTimeString: string | undefined): string {
  if (!dateTimeString) return 'N/A';
  try {
    const date = new Date(dateTimeString);
    return date.toLocaleString('no-NO', { dateStyle: 'short', timeStyle: 'short' });
  } catch (e) {
    return dateTimeString;
  }
}

// --- Lifecycle Hook ---
onMounted(() => {
  loadMyBids();
});
</script>

<style scoped>
/* Styles remain the same as the previous version */
/* ...(keep existing styles from previous ProfileMyBidsView.vue response)... */
.my-bids-view {
  padding: 1rem;
  max-width: 1000px;
  margin: 0 auto;
}

.my-bids-view h2 {
  margin-bottom: 1.5rem;
  border-bottom: 1px solid var(--color-border);
  padding-bottom: 0.5rem;
}


.loading-indicator,
.error-message,
.no-items-message {
  text-align: center;
  margin-top: 2rem;
  padding: 1rem;
  color: var(--color-text);
}

.error-message {
  color: red;
  font-weight: bold;
  background-color: #ffebee;
  border: 1px solid red;
  border-radius: 4px;
}
.action-error {
  margin-top: 1rem;
}

.bids-list {
  display: grid;
  gap: 1rem;
}

.bid-card-my {
  border: 1px solid var(--color-border);
  border-radius: 6px;
  padding: 1rem 1.5rem;
  background-color: var(--color-background);
  display: flex;
  justify-content: space-between;
  align-items: center; /* Center items vertically */
  gap: 1rem;
  transition: background-color 0.3s ease;
}

/* Style based on bid status */
.bid-status-pending {
  border-left: 4px solid #ffc107; /* Yellow for pending */
}
.bid-status-accepted {
  border-left: 4px solid #28a745; /* Green for accepted */
  background-color: #e9f5e9;
}
.bid-status-rejected {
  border-left: 4px solid #dc3545; /* Red for rejected */
  opacity: 0.8;
}

.bid-details {
  flex-grow: 1;
}

.bid-details p {
  margin: 0 0 0.5rem 0;
  line-height: 1.5;
}

.bid-details strong {
  color: var(--color-heading);
}

.item-link {
  color: hsla(160, 100%, 37%, 1);
  text-decoration: none;
  font-weight: 500;
}
.item-link:hover {
  text-decoration: underline;
}


.bid-timestamp {
  font-size: 0.85em;
  color: var(--vt-c-text-light-2);
  margin-top: 0.3rem;
}

.bid-actions-my {
  display: flex;
  gap: 0.75rem; /* Space between buttons */
  align-items: center;
  flex-shrink: 0; /* Prevent actions area from shrinking */
}

.bid-actions-my button {
  padding: 0.4rem 0.8rem;
  font-size: 0.9rem;
  cursor: pointer;
  min-width: 60px;
  text-align: center;
  border: 1px solid transparent;
  border-radius: 4px;
  transition: background-color 0.2s ease, opacity 0.2s ease, border-color 0.2s ease;
}
.bid-actions-my button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.edit-button { /* Renamed from edit-button, styles updated */
  background-color: #007bff; /* Blue */
  color: white;
  border-color: #007bff;
  min-width: 90px; /* Slightly wider for "Update Bid" */
}
.edit-button:hover:not(:disabled) {
  background-color: #0056b3;
  border-color: #0056b3;
}

.delete-button {
  background-color: transparent;
  color: #dc3545; /* Red */
  border-color: #dc3545;
}
.delete-button:hover:not(:disabled) {
  background-color: #dc3545;
  color: white;
}

.status-indicator {
  font-weight: bold;
  padding: 0.3em 0.6em;
  border-radius: 4px;
  font-size: 0.9em;
}
.status-indicator.accepted {
  color: #155724;
  background-color: #d4edda;
  border: 1px solid #c3e6cb;
}
.status-indicator.rejected {
  color: #721c24;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
}


.bid-status-badge {
  display: inline-block;
  padding: 0.2em 0.5em;
  font-size: 0.8em;
  font-weight: bold;
  border-radius: 4px;
  color: white;
  vertical-align: middle; /* Align better with text */
}
/* Color coding */
.bid-status-pending .bid-status-badge { background-color: #ffc107; color: #333;}
.bid-status-accepted .bid-status-badge { background-color: #28a745; }
.bid-status-rejected .bid-status-badge { background-color: #dc3545; }


@media (max-width: 650px) {
  .bid-card-my {
    flex-direction: column; /* Stack details and actions vertically */
    align-items: stretch; /* Stretch full width */
  }
  .bid-actions-my {
    justify-content: flex-end; /* Align buttons to the right */
    margin-top: 0.75rem;
  }
}
</style>
