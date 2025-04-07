<template>
  <div class="manage-item-view">
    <div v-if="itemLoading" class="loading-indicator">
      <p>Loading item details...</p>
    </div>
    <div v-else-if="itemError" class="error-message">
      <p>Error loading item: {{ itemError }}</p>
      <router-link to="/profile/listings">Back to My Listings</router-link>
    </div>
    <div v-else-if="item" class="item-section">
      <h2>Manage Your Item: {{ item.title }}</h2>
      <div class="item-details-display">
        <img
          :src="item.imageUrls && item.imageUrls.length > 0 ? item.imageUrls[0] : '/placeholder-image.png'"
          :alt="item.title"
          class="item-main-image"
          @error="handleImageError"
        />
        <div class="item-info">
          <p><strong>Description:</strong> {{ item.description || 'No description provided.' }}</p>
          <p><strong>Category:</strong> {{ item.category }}</p>
          <p><strong>Price:</strong> {{ formatPrice(item.price) }}</p>
          <button @click="goToEditItem">Edit Item</button> </div>
      </div>

      <div class="bids-section">
        <h3>Received Bids</h3>
        <div v-if="bidsLoading" class="loading-indicator">
          <p>Loading bids...</p>
        </div>
        <div v-else-if="bidsError" class="error-message">
          <p>Error loading bids: {{ bidsError }}</p>
        </div>
        <div v-else-if="bids.length > 0" class="bids-list">
          <div v-for="bid in sortedBids" :key="bid.id" class="bid-card" :class="`bid-status-${bid.status.toLowerCase()}`">
            <div class="bid-info">
              <p><strong>Bidder:</strong> {{ bid.bidderUsername }}</p>
              <p><strong>Amount:</strong> {{ formatPrice(bid.amount) }}</p>
              <p v-if="bid.comment"><strong>Comment:</strong> {{ bid.comment }}</p>
              <p><strong>Status:</strong> <span class="bid-status-badge">{{ bid.status }}</span></p>
              <p class="bid-timestamp">Placed: {{ formatDateTime(bid.createdAt) }}</p>
              <p v-if="bid.createdAt !== bid.updatedAt" class="bid-timestamp">Updated: {{ formatDateTime(bid.updatedAt) }}</p>
            </div>
            <div v-if="bid.status === 'PENDING'" class="bid-actions">
              <button @click="handleAcceptBid(bid)" class="accept-button" :disabled="actionLoading === bid.id">
                <span v-if="actionLoading === bid.id">...</span><span v-else>Accept</span>
              </button>
              <button @click="handleRejectBid(bid)" class="reject-button" :disabled="actionLoading === bid.id">
                <span v-if="actionLoading === bid.id">...</span><span v-else>Reject</span>
              </button>
            </div>
            <div v-else-if="bid.status === 'ACCEPTED'" class="accepted-message">
              Accepted
            </div>
          </div>
        </div>
        <div v-else class="no-bids-message">
          <p>No bids have been placed on this item yet.</p>
        </div>
      </div>
      <div v-if="actionError" class="error-message action-error">
        {{ actionError }}
      </div>

    </div>
    <div v-else class="error-message">
      <p>Item data could not be loaded.</p>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { fetchItem } from '@/services/ItemService';
import { fetchBidsForItem, acceptBid, rejectBid } from '@/services/BidService'; // Import bid services
import type { ItemDetailsType } from '@/models/Item';
import type { BidResponseType } from '@/models/Bid';

const route = useRoute();
const router = useRouter();

const itemId = ref<string | number | null>(null);
const item = ref<ItemDetailsType | null>(null);
const bids = ref<BidResponseType[]>([]);

const itemLoading = ref(false);
const bidsLoading = ref(false);
const actionLoading = ref<string | number | null>(null); // Track which bid action is loading

const itemError = ref<string | null>(null);
const bidsError = ref<string | null>(null);
const actionError = ref<string | null>(null); // For accept/reject errors


// Fetch Item Details
async function loadItemDetails(id: string | number) {
  itemLoading.value = true;
  itemError.value = null;
  try {
    item.value = await fetchItem(id);
  } catch (error: any) {
    console.error("Error loading item details:", error);
    itemError.value = error.message || "Could not load item details.";
    item.value = null; // Clear item on error
  } finally {
    itemLoading.value = false;
  }
}

// Fetch Bids for the Item
async function loadBids(id: string | number) {
  bidsLoading.value = true;
  bidsError.value = null;
  actionError.value = null; // Clear previous action errors
  try {
    bids.value = await fetchBidsForItem(id);
  } catch (error: any) {
    console.error("Error loading bids:", error);
    bidsError.value = error.message || "Could not load bids for this item.";
    bids.value = []; // Clear bids on error
  } finally {
    bidsLoading.value = false;
  }
}

// Handle Accept Bid Action
async function handleAcceptBid(bidToAccept: BidResponseType) {
  if (!itemId.value || actionLoading.value) return;
  actionLoading.value = bidToAccept.id; // Set loading for this specific bid
  actionError.value = null;
  try {
    await acceptBid(bidToAccept.bidderId, itemId.value);
    // Update bid status locally or reload bids
    // Reloading is simpler for now
    await loadBids(itemId.value);
  } catch (error: any) {
    console.error("Error accepting bid:", error);
    actionError.value = error.message || "Failed to accept bid.";
  } finally {
    actionLoading.value = null; // Clear loading state
  }
}

// Handle Reject Bid Action
async function handleRejectBid(bidToReject: BidResponseType) {
  if (!itemId.value || actionLoading.value) return;
  actionLoading.value = bidToReject.id; // Set loading
  actionError.value = null;
  try {
    await rejectBid(bidToReject.bidderId, itemId.value);
    // Update bid status locally or reload bids
    await loadBids(itemId.value);
  } catch (error: any) {
    console.error("Error rejecting bid:", error);
    actionError.value = error.message || "Failed to reject bid.";
  } finally {
    actionLoading.value = null; // Clear loading state
  }
}


// Computed property to sort bids (e.g., by amount descending)
const sortedBids = computed(() => {
  return [...bids.value].sort((a, b) => {
    // Prioritize PENDING bids, then sort by amount descending
    if (a.status === 'PENDING' && b.status !== 'PENDING') return -1;
    if (a.status !== 'PENDING' && b.status === 'PENDING') return 1;
    return b.amount - a.amount; // Higher amount first
  });
});

// Function to navigate to edit page (implement later)
function goToEditItem() {
  if(itemId.value) {
    // Assuming you have an edit route like 'edit-item'
    // router.push({ name: 'edit-item', params: { id: itemId.value }});
    alert(`Maps to edit page for item ${itemId.value}`);
  }
}


// Lifecycle hook
onMounted(() => {
  const idFromRoute = route.params.id;
  if (idFromRoute) {
    itemId.value = Array.isArray(idFromRoute) ? idFromRoute[0] : idFromRoute;
    loadItemDetails(itemId.value);
    loadBids(itemId.value);
  } else {
    itemError.value = "Item ID not found in route.";
    console.error("Item ID missing from route parameters.");
  }
});

// --- Formatting Helpers ---
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
    return dateTimeString; // Fallback to original string if parsing fails
  }
}

function handleImageError(event: Event) {
  const imgElement = event.target as HTMLImageElement;
  imgElement.src = '/placeholder-image.png';
}
</script>

<style scoped>
.manage-item-view {
  padding: 1.5rem;
  max-width: 900px;
  margin: 1rem auto;
  background-color: var(--color-background-soft);
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.loading-indicator,
.error-message,
.no-bids-message {
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

.item-section h2 {
  margin-bottom: 1.5rem;
  border-bottom: 1px solid var(--color-border);
  padding-bottom: 0.5rem;
}

.item-details-display {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 2rem;
  flex-wrap: wrap; /* Allow wrapping on smaller screens */
}

.item-main-image {
  width: 250px;
  height: 250px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid var(--color-border);
  background-color: #f0f0f0;
  flex-shrink: 0; /* Prevent image from shrinking */
}

.item-info {
  flex-grow: 1;
}
.item-info p {
  margin-bottom: 0.75rem;
  line-height: 1.6;
}
.item-info button {
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  cursor: pointer;
}


.bids-section h3 {
  margin-top: 2rem;
  margin-bottom: 1rem;
  border-bottom: 1px solid var(--color-border);
  padding-bottom: 0.5rem;
}

.bids-list {
  display: grid;
  gap: 1rem;
}

.bid-card {
  border: 1px solid var(--color-border);
  border-radius: 6px;
  padding: 1rem;
  background-color: var(--color-background);
  display: flex;
  justify-content: space-between;
  align-items: flex-start; /* Align items top */
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
  opacity: 0.7;
}


.bid-info p {
  margin: 0 0 0.4rem 0;
}
.bid-info strong {
  color: var(--color-heading);
}

.bid-timestamp {
  font-size: 0.85em;
  color: var(--vt-c-text-light-2);
  margin-top: 0.5rem;
}

.bid-actions {
  display: flex;
  flex-direction: column; /* Stack buttons vertically */
  gap: 0.5rem; /* Space between buttons */
  align-items: flex-end; /* Align buttons to the right */
}

.bid-actions button {
  padding: 0.4rem 0.8rem;
  font-size: 0.9rem;
  cursor: pointer;
  min-width: 70px; /* Ensure buttons have minimum width */
  text-align: center;
  border: none;
  border-radius: 4px;
  transition: background-color 0.2s ease, opacity 0.2s ease;
}
.bid-actions button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.accept-button {
  background-color: #28a745; /* Green */
  color: white;
}
.accept-button:hover:not(:disabled) {
  background-color: #218838;
}

.reject-button {
  background-color: #dc3545; /* Red */
  color: white;
}
.reject-button:hover:not(:disabled) {
  background-color: #c82333;
}

.accepted-message {
  font-weight: bold;
  color: #28a745; /* Green */
  align-self: center; /* Center vertically */
}

.bid-status-badge {
  display: inline-block;
  padding: 0.2em 0.5em;
  font-size: 0.8em;
  font-weight: bold;
  border-radius: 4px;
  color: white;
}
/* Color coding for status badges */
.bid-status-pending .bid-status-badge { background-color: #ffc107; color: #333;}
.bid-status-accepted .bid-status-badge { background-color: #28a745; }
.bid-status-rejected .bid-status-badge { background-color: #dc3545; }


@media (max-width: 600px) {
  .item-details-display {
    flex-direction: column; /* Stack image and info vertically */
  }
  .item-main-image {
    width: 100%; /* Full width on small screens */
    height: 200px; /* Adjust height */
  }
  .bid-card {
    flex-direction: column; /* Stack bid info and actions vertically */
    align-items: stretch; /* Stretch items full width */
  }
  .bid-actions {
    flex-direction: row; /* Put buttons side-by-side */
    justify-content: flex-end; /* Push buttons to the right */
    margin-top: 0.5rem;
  }
}

</style>
