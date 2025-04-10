<template>
  <div class="my-bids-view">
    <h2>{{ $t('MY_BIDS_TITLE') }}</h2>

    <div v-if="isLoading" class="loading-indicator">
      <p>Loading your bids...</p>
    </div>

    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
    </div>

    <div v-else-if="myBids.length > 0" class="bids-list">
      <div
        v-for="bid in myBids"
        :key="bid.id"
        class="bid-card-my"
        :class="`bid-status-${bid.status.toLowerCase()}`"
      >
        <div class="bid-details">
          <p>
            <span>{{ $t('ON_ITEM') }}: </span>
            <router-link
              :to="{ name: 'item-detail', params: { id: bid.itemId } }"
              class="item-link"
            >
              {{ getItemTitle(bid.itemId) || `Item #${bid.itemId}` }}
            </router-link>
          </p>
          <p><strong>{{ $t('YOUR_BID_AMOUNT') }}:</strong> {{ formatPrice(bid.amount) }}</p>
          <p v-if="bid.comment">
            <strong>{{ $t('YOUR_COMMENT') }}:</strong> {{ bid.comment }}
          </p>
          <p>
            <strong>{{ $t('STATUS') }}:</strong>
            <span class="bid-status-badge">{{ bid.status }}</span>
          </p>
          <p class="bid-timestamp">{{ $t('PLACED') }}: {{ formatDateTime(bid.createdAt) }}</p>
          <p v-if="bid.createdAt !== bid.updatedAt" class="bid-timestamp">
            Updated: {{ formatDateTime(bid.updatedAt) }}
          </p>
        </div>
        <div class="bid-actions-my">
          <template v-if="bid.status === 'PENDING'">
            <button
              @click="openUpdateBidModal(bid)"
              class="edit-button"
              :disabled="actionLoading === bid.id"
            >
              {{ $t('UPDATE_BID') }}
            </button>
            <button
              @click="promptDeleteBid(bid)"
              class="delete-button"
              :disabled="actionLoading === bid.id && bid.id === bidToDelete?.id"
            >
              <span v-if="actionLoading === bid.id && bid.id === bidToDelete?.id">...</span>
              <span v-else>{{ $t('DELETE') }}</span>
            </button>
          </template>
          <span v-else-if="bid.status === 'ACCEPTED'" class="status-indicator accepted">{{
            $t('ACCEPTED')
          }}</span>
          <span v-else-if="bid.status === 'REJECTED'" class="status-indicator rejected">{{
            $t('REJECTED')
          }}</span>
        </div>
      </div>

      <!-- Pagination controls -->
      <div v-if="totalPages > 1" class="pagination-controls">
        <button
          :disabled="currentPage <= 1"
          @click="changePage(currentPage - 1)"
          class="pagination-button"
        >
          Previous
        </button>
        <span class="page-info">Page {{ currentPage }} of {{ totalPages }}</span>
        <button
          :disabled="currentPage >= totalPages"
          @click="changePage(currentPage + 1)"
          class="pagination-button"
        >
          Next
        </button>
      </div>
    </div>

    <div v-else class="no-items-message">
      <p>{{ $t('NO_BIDS_YET') }}</p>
    </div>

    <div v-if="actionError" class="error-message action-error">
      {{ actionError }}
    </div>

    <BidModal
      :is-open="isBidModalOpen"
      :item-id="bidToEdit?.itemId ?? ''"
      :item-title="bidToEdit ? getItemTitle(bidToEdit.itemId) : ''"
      :initial-bid="bidToEdit"
      @close="closeBidModal"
      @bid-placed="handleBidResult"
      @bid-updated="handleBidResult"
    />

    <ConfirmDeleteModal
      :is-open="showDeleteConfirmModal"
      :message="deleteConfirmationMessage"
      title="Confirm Bid Deletion"
      confirm-text="Delete Bid"
      @confirm="confirmDeleteBid"
      @cancel="cancelDeleteBid"
    />
  </div>
</template>

<script setup lang="ts">
/**
 * Profile My Bids View component.
 *
 * This component displays a list of bids placed by the current user.
 * It provides functionality to view, update, and delete pending bids,
 * as well as see the status of all bids.
 *
 * Features:
 * - Paginated list of user's bids with status indicators
 * - Visual differentiation between pending, accepted, and rejected bids
 * - Ability to update pending bids through a modal interface
 * - Ability to delete pending bids with confirmation dialog
 * - Links to the related item details
 * - Formatted price and timestamp display
 * - Loading states and error handling
 *
 * @component ProfileMyBidsView
 * @requires vue
 * @requires vue-router
 * @requires vue-i18n
 * @requires @/services/BidService
 * @requires @/services/ItemService
 * @requires @/components/modals/BidModal.vue
 * @requires @/components/modals/ConfirmDeleteModal.vue
 * @requires @/models/Bid
 * @displayName ProfileMyBidsView
 */

import { ref, onMounted } from 'vue'
import { fetchPagedUserBids, deleteMyBid } from '@/services/BidService'
import { fetchItem } from '@/services/ItemService'
import type { BidResponseType } from '@/models/Bid'
import BidModal from '@/components/modals/BidModal.vue'
import ConfirmDeleteModal from '@/components/modals/ConfirmDeleteModal.vue'

const myBids = ref<BidResponseType[]>([])
const itemDetailsCache = ref<Record<string | number, string>>({})
const isLoading = ref(false)
const actionLoading = ref<string | number | null>(null)
const error = ref<string | null>(null)
const actionError = ref<string | null>(null)

const currentPage = ref(1)
const totalPages = ref(0)
const pageSize = ref(5)
const sortOption = ref('createdAt,desc')

const bidToEdit = ref<BidResponseType | null>(null)
const showDeleteConfirmModal = ref(false)
const bidToDelete = ref<BidResponseType | null>(null)
const deleteConfirmationMessage = ref('')
const isBidModalOpen = ref(false)

/**
 * Fetches and loads the user's bids from the API.
 * Updates component state with paginated results or error information.
 * Also fetches item titles for each bid.
 *
 * @returns {Promise<void>}
 */
async function loadMyBids() {
  isLoading.value = true
  error.value = null
  try {
    const paginated = await fetchPagedUserBids(
      currentPage.value - 1,
      pageSize.value,
      sortOption.value,
    )
    myBids.value = paginated.content
    totalPages.value = paginated.totalPages
    await fetchItemTitlesForBids(myBids.value)
  } catch (err: any) {
    console.error('Failed to load user bids:', err)
    error.value = err.message || 'Could not load your bids.'
    myBids.value = []
  } finally {
    isLoading.value = false
  }
}

/**
 * Changes the current page of bids and reloads data.
 *
 * @param {number} page - The page number to navigate to
 * @returns {void}
 */
function changePage(page: number) {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    loadMyBids()
  }
}

/**
 * Fetches item titles for a collection of bids and caches the results.
 * Only fetches titles for items not already in the cache.
 *
 * @param {BidResponseType[]} bidsToFetch - The bids to fetch item titles for
 * @returns {Promise<void>}
 */
async function fetchItemTitlesForBids(bidsToFetch: BidResponseType[]) {
  const itemIdsToFetch = bidsToFetch
    .map((b) => b.itemId)
    .filter((id) => !itemDetailsCache.value[id])
  const results = await Promise.all(
    [...new Set(itemIdsToFetch)].map((id) =>
      fetchItem(id)
        .then((data) => ({ id, title: data.title }))
        .catch(() => ({
          id,
          title: `Item #${id}`,
        })),
    ),
  )
  results.forEach((res) => (itemDetailsCache.value[res.id] = res.title))
}

/**
 * Retrieves the cached title for an item.
 *
 * @param {string|number|null} itemId - The ID of the item
 * @returns {string} The item title or a fallback string
 */
function getItemTitle(itemId: string | number | null): string {
  return itemId != null ? itemDetailsCache.value[itemId] || `Item #${itemId}` : 'Unknown Item'
}

/**
 * Opens the bid update modal with pre-populated data.
 *
 * @param {BidResponseType} bid - The bid to be edited
 * @returns {void}
 */
function openUpdateBidModal(bid: BidResponseType) {
  bidToEdit.value = bid
  isBidModalOpen.value = true
}

/**
 * Closes the bid update modal and resets the edit state.
 *
 * @returns {void}
 */
function closeBidModal() {
  isBidModalOpen.value = false
  bidToEdit.value = null
}

/**
 * Handles the result of a successful bid update or placement.
 * Closes the modal and refreshes the bids list.
 *
 * @returns {void}
 */
function handleBidResult() {
  closeBidModal()
  loadMyBids()
}

/**
 * Opens the delete confirmation modal for a specific bid.
 * Sets up the confirmation message with bid details.
 *
 * @param {BidResponseType} bid - The bid to be deleted
 * @returns {void}
 */
function promptDeleteBid(bid: BidResponseType) {
  bidToDelete.value = bid
  deleteConfirmationMessage.value = `Are you sure you want to delete your bid of ${formatPrice(bid.amount)} on \"${getItemTitle(bid.itemId)}\"? This action cannot be undone.`
  showDeleteConfirmModal.value = true
}

/**
 * Handles the confirmation action for bid deletion.
 * Sends the delete request to the API and refreshes the bids list.
 *
 * @returns {Promise<void>}
 */
async function confirmDeleteBid() {
  if (!bidToDelete.value) return
  actionLoading.value = bidToDelete.value.id
  showDeleteConfirmModal.value = false
  try {
    await deleteMyBid(bidToDelete.value.itemId)
    bidToDelete.value = null
    await loadMyBids()
  } catch (err: any) {
    actionError.value = err.message || 'Failed to delete bid.'
  } finally {
    actionLoading.value = null
  }
}

/**
 * Cancels the bid deletion process and closes the confirmation modal.
 *
 * @returns {void}
 */
function cancelDeleteBid() {
  showDeleteConfirmModal.value = false
  bidToDelete.value = null
}

/**
 * Formats a price value according to Norwegian currency format.
 *
 * @param {number|null|undefined} price - The price to format
 * @returns {string} The formatted price string
 */
function formatPrice(price: number | null | undefined): string {
  return price != null
    ? price.toLocaleString('no-NO', {
        style: 'currency',
        currency: 'NOK',
      })
    : 'N/A'
}

/**
 * Formats a date string according to Norwegian locale.
 *
 * @param {string|undefined} dateStr - The date string to format
 * @returns {string} The formatted date and time string
 */
function formatDateTime(dateStr: string | undefined): string {
  return dateStr
    ? new Date(dateStr).toLocaleString('no-NO', {
        dateStyle: 'short',
        timeStyle: 'short',
      })
    : 'N/A'
}

onMounted(() => loadMyBids())
</script>

<style scoped>
.my-bids-view {
  padding: 1rem;
  max-width: 1000px;
  margin: 0 auto;
}

.my-bids-view h2 {
  margin-bottom: 1.5rem;
  border-bottom: 1px solid var(--color-border);
  padding-bottom: 0.5rem;
  color: var(--vt-c-text-dark-1);
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
  color: var(--vt-c-red-dark);
  font-weight: bold;
  background-color: var(--vt-c-red-light);
  border: 1px solid var(--vt-c-red-soft);
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
  align-items: center;
  gap: 1rem;
  transition: background-color 0.3s ease;
}

.bid-status-pending {
  border-left: 4px solid var(--vt-c-teal-soft);
}

.bid-status-accepted {
  border-left: 4px solid var(--vt-c-green-dark);
  background-color: var(--vt-c-green-light);
}

.bid-status-rejected {
  border-left: 4px solid var(--vt-c-red-dark);
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
  color: var(--vt-c-text-dark-2);
  text-decoration: none;
  font-weight: 500;
  background: none;
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
  gap: 0.75rem;
  align-items: center;
  flex-shrink: 0;
}

.bid-actions-my button {
  padding: 0.4rem 0.8rem;
  font-size: 0.9rem;
  cursor: pointer;
  min-width: 60px;
  text-align: center;
  border: 1px solid transparent;
  border-radius: 4px;
  transition:
    background-color 0.2s ease,
    opacity 0.2s ease,
    border-color 0.2s ease;
}

.bid-actions-my button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.status-indicator {
  font-weight: bold;
  padding: 0.3em 0.6em;
  border-radius: 4px;
  font-size: 0.9em;
}

.status-indicator.accepted {
  color: var(--vt-c-white);
  background-color: var(--vt-c-teal-dark);
  border: 1px solid var(--vt-c-teal-soft);
}

.status-indicator.rejected {
  color: var(--vt-c-red-dark);
  background-color: var(--vt-c-red-light);
  border: 1px solid var(--vt-c-red-soft);
}

.bid-status-badge {
  display: inline-block;
  padding: 0.2em 0.5em;
  font-size: 0.8em;
  font-weight: bold;
  border-radius: 4px;
  color: var(--vt-c-white);
  vertical-align: middle;
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

@media (max-width: 650px) {
  .bid-card-my {
    flex-direction: column;
    align-items: stretch;
  }

  .bid-actions-my {
    justify-content: flex-end;
    margin-top: 0.75rem;
  }
}
</style>
