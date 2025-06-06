<template>
  <div class="profile-ads-view">
    <h2>{{ $t('MY_LISTINGS') }}</h2>

    <div v-if="isLoading" class="loading-indicator">
      <p>Loading your items...</p>
    </div>

    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
    </div>

    <div v-else-if="userItems.length > 0" class="items-grid">
      <div
        v-for="item in userItems"
        :key="item.id"
        class="item-preview-card"
        @click="navigateToManageItem(item.id)"
      >
        <img
          :src="item.imageUrl || placeholderImage"
          :alt="item.title"
          class="item-image"
          @error="handleImageError"
          loading="lazy"
        />
        <div class="item-details">
          <h3>{{ item.title }}</h3>
          <p class="item-price">{{ formatPrice(item.price) }}</p>
        </div>
      </div>
    </div>

    <div v-else class="no-items-message">
      <p>You haven't listed any items yet.</p>
      <router-link to="/create-listing/start" class="create-listing-link"
      >List an Item</router-link
      >
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * Profile Ads View component.
 *
 * This component displays a grid of items listed by the current user.
 * It provides a way for users to view and manage their marketplace listings.
 *
 * Features:
 * - Grid display of user's listed items with images
 * - Navigation to detailed management view for each item
 * - Empty state with prompt to create a new listing
 * - Sorted by newest listings first
 * - Error handling for API failures
 * - Loading state indicators
 * - Responsive grid layout
 *
 * @component ProfileAdsView
 * @requires vue
 * @requires vue-router
 * @requires vue-i18n
 * @requires @/services/ItemService
 * @requires @/models/Item
 * @displayName ProfileAdsView
 */
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { fetchMyPagedItems } from '@/services/ItemService'
import type { ItemPreviewType, PaginatedItemPreviewResponse } from '@/models/Item'
import placeholderImage from '@/assets/images/placeholderItem.jpg'

const router = useRouter()
const userItems = ref<ItemPreviewType[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)

const page = ref(0)
const size = 20 // Adjust as needed
const sort = 'publishedAt,desc'

/**
 * Handles image loading errors by replacing with placeholder.
 *
 * @param {Event} event - The error event from the image element
 * @returns {void}
 */
async function loadUserItems() {
  isLoading.value = true
  error.value = null
  try {
    const response: PaginatedItemPreviewResponse = await fetchMyPagedItems(
      page.value,
      size,
      sort
    )
    userItems.value = response.content
  } catch (err: any) {
    if (err.response?.status === 401 || err.response?.status === 403) {
      error.value = 'Authentication error. Please log in again.'
    } else {
      error.value = 'Could not load your items. Please try again later.'
    }
    userItems.value = []
  } finally {
    isLoading.value = false
  }
}

/**
 * Handles image loading errors by replacing with placeholder.
 *
 * @param {Event} event - The error event from the image element
 * @returns {void}
 */
function handleImageError(event: Event) {
  const imgElement = event.target as HTMLImageElement
  imgElement.src = placeholderImage
}

/**
 * Formats a price value to the Norwegian currency format.
 *
 * @param {number|null|undefined} price - The price to format
 * @returns {string} The formatted price string
 */
function formatPrice(price: number | null | undefined): string {
  if (price === null || price === undefined) return 'N/A'
  return price.toLocaleString('no-NO', { style: 'currency', currency: 'NOK' })
}

/**
 * Navigates to the management view for a specific item.
 *
 * @param {string|number} itemId - The ID of the item to manage
 * @returns {void}
 */
function navigateToManageItem(itemId: string | number) {
  router.push({ name: 'manage-my-item', params: { id: itemId } })
}

onMounted(() => {
  loadUserItems()
})
</script>

<style scoped>
.profile-ads-view {
  padding: 1rem;
  max-width: 1200px;
  margin: 0 auto;
}

h2 {
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
  color: var(--color-text);
}

.error-message p {
  color: var(--vt-c-red-dark);
  font-weight: bold;
}

.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 1.5rem;
  margin-top: 1.5rem;
}

.item-preview-card {
  border: 1px solid var(--color-border);
  border-radius: 8px;
  overflow: hidden;
  background-color: var(--color-background-soft);
  transition: box-shadow 0.3s ease, transform 0.2s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
}

.item-preview-card:hover {
  box-shadow: 0 6px 16px var(--vt-c-teal-soft);
  transform: translateY(-3px);
}

.item-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
  display: block;
  background-color: var(--color-background-mute);
}

.item-details {
  padding: 1rem;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.item-details h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
  color: var(--color-heading);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-price {
  font-weight: 600;
  color: var(--vt-c-teal-dark);
  margin-top: 0.5rem;
  margin-bottom: 0;
  font-size: 1rem;
}

.no-items-message .create-listing-link {
  display: inline-block;
  margin-top: 1rem;
  padding: 0.6rem 1.2rem;
  background-color: var(--vt-c-teal-soft);
  color: var(--vt-c-white);
  text-decoration: none;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.no-items-message .create-listing-link:hover {
  background-color: var(--vt-c-teal-dark);
}
</style>
