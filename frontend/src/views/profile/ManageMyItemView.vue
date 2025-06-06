<template>
  <div class="manage-item-view">
    <div v-if="itemLoading" class="loading-indicator">
      <p>{{ $t('LOADING_ITEM_DETAILS') }}</p>
    </div>
    <div v-else-if="itemError" class="error-message">
      <p>{{ $t('ERROR_LOADING_ITEM') }}: {{ itemError }}</p>
      <router-link to="/profile/listings">{{ $t('BACK_TO_MY_LISTINGS') }}</router-link>
    </div>
    <div v-else-if="item" class="item-section">
      <h2>{{ $t('MANAGE_YOUR_ITEM') }}: {{ item.title }}</h2>

      <div class="item-details-display">
        <img
          :src="item.imageUrls && item.imageUrls.length > 0 ? item.imageUrls[0] : placeholderImage"
          :alt="item.title"
          class="item-main-image"
          @error="handleImageError"
        />
        <div class="item-info">
          <p><strong>{{ $t('DESCRIPTION') }}:</strong> {{ item.description || $t('NO_DESCRIPTION_PROVIDED') }}</p>
          <p><strong>{{ $t('CATEGORY') }}:</strong> {{ item.category }}</p>
          <p><strong>{{ $t('PRICE') }}:</strong> {{ formatPrice(item.price) }}</p>

          <div class="item-actions">
            <button class="edit-button" @click="goToEditItem">{{ $t('EDIT') }} {{ $t('ITEM') }}</button>
            <button class="edit-button" @click="goToItemDetail">{{ $t('VIEW') }} {{ $t('ITEM') }}</button>
            <button class="delete-button" @click="openDeleteModal">{{ $t('DELETE') }} {{ $t('ITEM') }}</button>
            <ConfirmDeleteModal
              :isOpen="isDeleteModalOpen"
              :title="$t('DELETE_ITEM')"
              :message="$t('CONFIRM_ITEM_DELETE_MESSAGE')"
              :confirmText="$t('DELETE')"
              :cancelText="$t('CANCEL')"
              @confirm="handleDeleteConfirm"
              @cancel="handleDeleteCancel"
            />
          </div>
        </div>
      </div>

      <!-- BIDS SECTION -->
      <div class="bids-section">
        <h3>{{ $t('RECEIVED_BIDS') }}</h3>
        <div v-if="bidsLoading" class="loading-indicator">
          <p>{{ $t('LOADING_BIDS') }}</p>
        </div>
        <div v-else-if="bidsError" class="error-message">
          <p>{{ $t('ERROR_LOADING_BIDS') }}: {{ bidsError }}</p>
        </div>
        <div v-else-if="sortedBids.length > 0" class="bids-list">
          <div
            v-for="bid in sortedBids"
            :key="bid.id"
            class="bid-card"
            :class="`bid-status-${bid.status.toLowerCase()}`"
          >
            <div class="bid-info">
              <p><strong>{{ $t('BIDDER') }}:</strong> {{ bid.bidderUsername }}</p>
              <p><strong>{{ $t('AMOUNT') }}:</strong> {{ formatPrice(bid.amount) }}</p>
              <p v-if="bid.comment"><strong>{{ $t('YOUR_COMMENT') }}:</strong> {{ bid.comment }}</p>
              <p><strong>{{ $t('STATUS') }}:</strong> <span class="bid-status-badge">{{ bid.status }}</span></p>
              <p class="bid-timestamp">{{ $t('PLACED') }}: {{ formatDateTime(bid.createdAt) }}</p>
              <p v-if="bid.createdAt !== bid.updatedAt" class="bid-timestamp">{{ $t('UPDATED') }}: {{ formatDateTime(bid.updatedAt) }}</p>
            </div>
            <div v-if="bid.status === 'PENDING'" class="bid-actions">
              <button @click="handleAcceptBid(bid)" class="accept-button" :disabled="actionLoading === bid.id">
                <span v-if="actionLoading === bid.id">...</span><span v-else>{{ $t('ACCEPT') }}</span>
              </button>
              <button @click="handleRejectBid(bid)" class="reject-button" :disabled="actionLoading === bid.id">
                <span v-if="actionLoading === bid.id">...</span><span v-else>{{ $t('REJECT') }}</span>
              </button>
            </div>
            <div v-else-if="bid.status === 'ACCEPTED'" class="accepted-message">
              {{ $t('ACCEPTED') }}
            </div>
          </div>
        </div>
        <div v-else class="no-bids-message">
          <p>{{ $t('NO_BIDS_ON_ITEM_YET') }}</p>
        </div>
      </div>

      <div v-if="actionError" class="error-message action-error">
        {{ actionError }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { fetchItem, deleteItem } from '@/services/ItemService';
import { fetchBidsForItem, acceptBid, rejectBid } from '@/services/BidService';
import type { ItemDetailsType } from '@/models/Item';
import type { BidResponseType } from '@/models/Bid';
import ConfirmDeleteModal from '@/components/modals/ConfirmDeleteModal.vue';
import placeholderImage from '@/assets/images/placeholderItem.jpg';

const route = useRoute();
const router = useRouter();
const isDeleteModalOpen = ref(false);

const itemId = ref<string | number | null>(null);
const item = ref<ItemDetailsType | null>(null);
const bids = ref<BidResponseType[]>([]);

const itemLoading = ref(false);
const bidsLoading = ref(false);
const actionLoading = ref<string | number | null>(null);

const itemError = ref<string | null>(null);
const bidsError = ref<string | null>(null);
const actionError = ref<string | null>(null);

async function loadItemDetails(id: string | number) {
  itemLoading.value = true;
  itemError.value = null;
  try {
    item.value = await fetchItem(id);
  } catch (error: any) {
    itemError.value = error.message || "Could not load item details.";
    item.value = null;
  } finally {
    itemLoading.value = false;
  }
}

async function loadBids(id: string | number) {
  bidsLoading.value = true;
  bidsError.value = null;
  actionError.value = null;
  try {
    bids.value = (await fetchBidsForItem(id)).content;
  } catch (error: any) {
    bidsError.value = error.message || "Could not load bids for this item.";
    bids.value = [];
  } finally {
    bidsLoading.value = false;
  }
}

async function handleAcceptBid(bidToAccept: BidResponseType) {
  if (!itemId.value || actionLoading.value) return;
  actionLoading.value = bidToAccept.id;
  actionError.value = null;
  try {
    await acceptBid(bidToAccept.bidderId, itemId.value);
    await loadBids(itemId.value);
  } catch (error: any) {
    actionError.value = error.message || "Failed to accept bid.";
  } finally {
    actionLoading.value = null;
  }
}

async function handleRejectBid(bidToReject: BidResponseType) {
  if (!itemId.value || actionLoading.value) return;
  actionLoading.value = bidToReject.id;
  actionError.value = null;
  try {
    await rejectBid(bidToReject.bidderId, itemId.value);
    await loadBids(itemId.value);
  } catch (error: any) {
    actionError.value = error.message || "Failed to reject bid.";
  } finally {
    actionLoading.value = null;
  }
}

const sortedBids = computed(() => {
  const bidArray = Array.isArray(bids.value) ? bids.value : [];
  return [...bidArray].sort((a, b) => {
    if (a.status === 'PENDING' && b.status !== 'PENDING') return -1;
    if (a.status !== 'PENDING' && b.status === 'PENDING') return 1;
    return b.amount - a.amount;
  });
});

function goToEditItem() {
  if(itemId.value) {
    router.push({ name: 'edit-listing', params: { itemId: itemId.value }});
  }
}

function goToItemDetail() {
  if (itemId.value) {
    router.push({ name: 'item-detail', params: { id: itemId.value }});
  }
}

function openDeleteModal() {
  isDeleteModalOpen.value = true;
}

async function handleDeleteConfirm() {
  isDeleteModalOpen.value = false;
  if (itemId.value !== null) {
    await deleteItem(Number(itemId.value));
  }
  router.push('/profile/listings');
}

function handleDeleteCancel() {
  isDeleteModalOpen.value = false;
}

onMounted(() => {
  const idFromRoute = route.params.id;
  if (idFromRoute) {
    itemId.value = Array.isArray(idFromRoute) ? idFromRoute[0] : idFromRoute;
    loadItemDetails(itemId.value);
    loadBids(itemId.value);
  } else {
    itemError.value = "Item ID not found in route.";
  }
});

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

function handleImageError(event: Event) {
  const imgElement = event.target as HTMLImageElement;
  imgElement.src = 'placeholderImage';
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
  color: var(--vt-c-red-dark);
  font-weight: bold;
  background-color: var(--vt-c-red-light);
  border: 1px solid var(--vt-c-red-soft);
  border-radius: 4px;
}

.action-error {
  margin-top: 1rem;
}

.item-section h2 {
  margin-bottom: 1.5rem;
  border-bottom: 1px solid var(--color-border);
  padding-bottom: 0.5rem;
  color: var(--vt-c-text-dark-2);
}

.item-details-display {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.item-main-image {
  width: 250px;
  height: 250px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid var(--color-border);
  background-color: var(--color-background-mute);
  flex-shrink: 0;
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
  color: var(--vt-c-text-dark-2);
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
  align-items: flex-start;
  gap: 1rem;
  transition: background-color 0.3s ease;
}

.bid-status-pending {
  border-left: 4px solid var(--vt-c-teal-soft);
}

.bid-status-accepted {
  border-left: 4px solid var(--vt-c-teal-dark);
  background-color: var(--vt-c-teal-light);
}

.bid-status-rejected {
  border-left: 4px solid var(--vt-c-red-dark);
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
  flex-direction: column;
  gap: 0.5rem;
  align-items: flex-end;
}

.bid-actions button {
  padding: 0.4rem 0.8rem;
  font-size: 0.9rem;
  cursor: pointer;
  min-width: 70px;
  text-align: center;
  border: none;
  border-radius: 4px;
  transition: background-color 0.2s ease, opacity 0.2s ease;
}

.bid-actions button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.accepted-message {
  font-weight: bold;
  color: var(--vt-c-teal-dark);
  align-self: center;
}

.bid-status-badge {
  display: inline-block;
  padding: 0.2em 0.5em;
  font-size: 0.8em;
  font-weight: bold;
  border-radius: 4px;
  color: var(--vt-c-white);
}

.bid-status-pending .bid-status-badge {
  background-color: var(--vt-c-teal-soft);
  color: var(--vt-c-white);
}

.bid-status-accepted .bid-status-badge {
  background-color: var(--vt-c-teal-dark);
}

.bid-status-rejected .bid-status-badge {
  background-color: var(--vt-c-red-dark);
}

@media (max-width: 600px) {
  .item-details-display {
    flex-direction: column;
  }
  .item-main-image {
    width: 100%;
    height: 200px;
  }
  .bid-card {
    flex-direction: column;
    align-items: stretch;
  }
  .bid-actions {
    flex-direction: row;
    justify-content: flex-end;
    margin-top: 0.5rem;
  }
}

</style>
