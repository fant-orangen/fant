<template>
  <div class="item-details-page">
    <!-- Loading state -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>Loading item details...</p>
    </div>

    <!-- Error state -->
    <div v-else-if="error" class="error-state">
      <h2>Unable to load item</h2>
      <p>{{ errorMessage }}</p>
      <button @click="retryLoading" class="retry-button">Try Again</button>
    </div>

    <!-- Content when item is loaded successfully -->
    <div v-else-if="item" class="item-detail-container">
      <!-- Left column: Image gallery -->
      <div class="gallery-column">
        <ImageGallery
          v-if="item.imageUrls && item.imageUrls.length > 0"
          :imageUrls="item.imageUrls"
          :alt-text="item.title"
        />
        <div v-else class="no-image-placeholder">
          <span class="placeholder-icon">📷</span>
          <p>No Images Available</p>
        </div>
      </div>

      <!-- Right column: Item details -->
      <div class="details-column">
        <!-- Header section with title and favorite button -->
        <div class="header">
          <h1 class="item-title">{{ item.title }}</h1>
          <HeartIcon
            class="heart-icon-details"
            :item-id="itemId"
            @toggle-favorite="handleFavoriteToggle"
          />
        </div>

        <!-- Price display -->
        <p class="item-price">{{ formatPrice(item.price) }}</p>

        <!-- Item description section -->
        <div class="item-description">
          <h3>Description</h3>
          <p>{{ item.description || 'No description provided.' }}</p>
        </div>

        <!-- Seller/contact information section -->
        <div class="seller-info">
          <h3>Contact Information</h3>
          <p v-if="item.contact"><strong>Seller:</strong> {{ item.contact }}</p>
          <p v-else class="no-info">Contact information unavailable</p>
        </div>

        <!-- Action buttons -->
        <div class="action-buttons">
          <button
            @click="startConversation"
            class="contact-button"
            :disabled="!canContactSeller"
          >
            Contact Seller
          </button>

          <button
            @click="openBidModal"
            class="bid-button"
            :disabled="!isUserLoggedIn || isUserSeller"
          >
            Place Bid
          </button>
        </div>

        <!-- Bid status message -->
        <div v-if="isUserSeller" class="seller-notice">
          You cannot bid on your own item.
        </div>
        <div v-else-if="!isUserLoggedIn" class="login-notice">
          Please log in to place a bid.
        </div>
      </div>
    </div>

    <!-- Fallback for when there's no loading, no error, but also no item -->
    <div v-else class="not-found-state">
      <h2>Item Not Found</h2>
      <p>The requested item could not be found or may have been removed.</p>
      <router-link to="/" class="home-link">Browse Other Items</router-link>
    </div>

    <!-- Bid Modal Component -->
    <BidModal
      :is-open="showBidModal"
      :item-id="itemId"
      :item-title="item?.title || ''"
      :current-price="item?.price || 0"
      @close="closeBidModal"
      @bid-placed="handleBidPlaced"
    />
  </div>
</template>

<script setup lang="ts">
/**
 * ItemDetails.vue
 *
 * A component that displays detailed information about a specific item.
 * Features include:
 * - Image gallery for item photos
 * - Detailed item information (title, price, description)
 * - Location information
 * - Seller contact details
 * - Ability to favorite/save the item
 * - Contact seller functionality
 * - Bid placement functionality
 *
 * The component handles various states:
 * - Loading state while fetching data
 * - Error state if data cannot be retrieved
 * - Empty state if no images or data are available
 * - Not found state if the item doesn't exist
 */

import { ref, onMounted, computed } from 'vue';
import ImageGallery from '@/components/show/ImageGallery.vue';
import HeartIcon from '@/components/toggle/HeartIcon.vue';
import BidModal from '@/components/modals/BidModal.vue';
import type { ItemDetailsType } from '@/models/Item';
import { fetchItem, recordItemView } from '@/services/ItemService.ts';
import { useUserStore } from '@/stores/UserStore';

/**
 * Component props
 * @property {string|number} itemId - The ID of the item to display
 */
const props = defineProps<{
  itemId: string | number
}>();

// Component state
const item = ref<ItemDetailsType | null>(null);
const loading = ref(true);
const error = ref(false);
const errorMessage = ref("Failed to load item details. Please try again later.");
const showBidModal = ref(false);

// Get user store for auth status
const userStore = useUserStore();

/**
 * Computed property to determine if the user can contact the seller
 */
const canContactSeller = computed(() => {
  return item.value && item.value.contact;
});

/**
 * Computed property to check if a user is logged in
 */
const isUserLoggedIn = computed(() => {
  return userStore.loggedIn;
});

/**
 * Computed property to check if the current user is the seller
 * Note: Since your ItemDetailsType doesn't include sellerId, we'll use the contact field
 * to determine if the current user is the seller. This is a simplification - in a real
 * implementation, you'd want a proper sellerId field or a way to determine ownership.
 */
const isUserSeller = computed(() => {
  if (!item.value || !userStore.username) return false;
  // Simple check - if username matches the contact field
  // You may need to adjust this based on your actual data structure
  return item.value.contact === userStore.username;
});

/**
 * Formats the price with currency symbol and proper formatting
 * @param {number|null} price - The price to format
 * @returns {string} The formatted price string
 */
function formatPrice(price: number | null): string {
  if (price === null || price === undefined) return 'Price not specified';
  return `${price.toLocaleString('no-NO')} kr`;
}

/**
 * Opens the modals modal
 */
function openBidModal() {
  if (!isUserLoggedIn.value || isUserSeller.value) return;
  showBidModal.value = true;
}

/**
 * Closes the modals modal
 */
function closeBidModal() {
  showBidModal.value = false;
}

/**
 * Handles successful modals placement
 */
function handleBidPlaced() {
  // You could add additional handling here, such as showing a toast notification
  console.log("Bid placed successfully");
  // Close modal after some time
  setTimeout(() => {
    closeBidModal();
  }, 2000);
}

/**
 * Loads the item data from the API
 */
async function loadItemData() {
  loading.value = true;
  error.value = false;

  if (!props.itemId) {
    loading.value = false;
    error.value = true;
    errorMessage.value = "No item ID provided.";
    return;
  }

  try {
    // Fetch item and record view concurrently
    const [fetchedItem, viewResponse] = await Promise.all([
      fetchItem(props.itemId),
      recordItemView(props.itemId)
    ]);

    // Handle potential array response from fetchItem (if API might return array)
    item.value = Array.isArray(fetchedItem) ? fetchedItem[0] : fetchedItem;

    if (!item.value) {
      throw new Error("Item not found");
    }

    console.log("Item details loaded:", item.value);
    console.log(`View recorded status: ${viewResponse?.status}`);
  } catch (err) {
    console.error('Error fetching item details:', err);
    error.value = true;
    errorMessage.value = err instanceof Error
      ? err.message
      : "Failed to load item details. Please try again later.";
    item.value = null;
  } finally {
    loading.value = false;
  }
}

/**
 * Retries loading the item data if there was an error
 */
function retryLoading() {
  loadItemData();
}

/**
 * Handles toggling the favorite status of an item
 * @param {boolean} isFavorite - Whether the item is now favorited
 */
function handleFavoriteToggle(isFavorite: boolean) {
  console.log(`Item ${props.itemId} favorite status: ${isFavorite}`);
  // Additional logic can be added here if needed
}

/**
 * Initiates a conversation with the seller
 */
function startConversation() {
  if (!item.value || !canContactSeller.value) return;

  console.log("Contacting seller for item:", item.value.id);

  // Temporary placeholder until messaging is implemented
  alert('Contact Seller functionality will be implemented in a future update.');
}

// Load item data when component mounts
onMounted(() => {
  loadItemData();
});
</script>

<style scoped>
/**
 * Item details page layout and responsive container
 */
.item-details-page {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 1rem;
}

/**
 * Main layout - two column grid for desktop
 */
.item-detail-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  margin-bottom: 2rem;
}

/**
 * Loading state styling
 */
.loading-state {
  text-align: center;
  padding: 4rem;
  color: #666;
}

.loading-spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top: 4px solid #007bff;
  width: 40px;
  height: 40px;
  margin: 0 auto 1rem;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/**
 * Error and not-found states styling
 */
.error-state, .not-found-state {
  text-align: center;
  padding: 3rem 1rem;
  background-color: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 2rem;
}

.retry-button, .home-link {
  display: inline-block;
  background-color: #007bff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  text-decoration: none;
  margin-top: 1rem;
}

.retry-button:hover, .home-link:hover {
  background-color: #0056b3;
}

/**
 * Gallery column styling with strict size constraints
 */
.gallery-column {
  border-radius: 8px;
  overflow: hidden;
  background-color: #f8f9fa;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  max-height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Target images within the gallery using deep selector */
.gallery-column :deep(img) {
  max-width: 100%;
  height: auto;
  max-height: 500px;
  object-fit: contain;
  display: block;
}

/* Ensure gallery component doesn't overflow */
.gallery-column :deep(.image-gallery) {
  width: 100%;
  max-height: 500px;
  overflow: hidden;
}

/* Style navigation arrows in gallery */
.gallery-column :deep(.nav-button) {
  background-color: rgba(255, 255, 255, 0.7);
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.no-image-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: #888;
  font-size: 1.1em;
  background-color: #f0f0f0;
}

.placeholder-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

/**
 * Details column styling
 */
.details-column {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  padding: 1.5rem;
  background-color: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

/**
 * Header section with title and favorite button
 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
  border-bottom: 1px solid #eee;
  padding-bottom: 1rem;
}

.item-title {
  font-size: 1.8em;
  font-weight: 600;
  margin: 0;
  color: #333;
  line-height: 1.3;
}

.heart-icon-details {
  font-size: 1.5rem;
  cursor: pointer;
  margin-top: 0.2rem;
  transition: color 0.2s ease;
}

/**
 * Price display styling
 */
.item-price {
  font-size: 1.6em;
  font-weight: bold;
  color: #007bff;
  margin: 0;
}

/**
 * Section headings and content styling
 */
.item-description h3,
.item-location h3,
.seller-info h3 {
  font-size: 1.1em;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #555;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 0.5rem;
}

.item-description p,
.item-location p,
.seller-info p {
  margin: 0;
  color: #444;
  line-height: 1.6;
}

.no-info {
  color: #999;
  font-style: italic;
}

/**
 * Action buttons styling
 */
.action-buttons {
  display: flex;
  gap: 1rem;
  margin-top: 0.5rem;
}

.contact-button, .bid-button {
  flex: 1;
  padding: 0.8rem 1rem;
  border-radius: 5px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.contact-button {
  background-color: #007bff;
  color: white;
  border: none;
}

.bid-button {
  background-color: #28a745;
  color: white;
  border: none;
}

.contact-button:hover:not(:disabled), .bid-button:hover:not(:disabled) {
  box-shadow: 0 4px 8px rgba(0,0,0,0.15);
}

.contact-button:hover:not(:disabled) {
  background-color: #0056b3;
}

.bid-button:hover:not(:disabled) {
  background-color: #218838;
}

.contact-button:disabled, .bid-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
  box-shadow: none;
}

/**
 * Login and seller notices
 */
.login-notice, .seller-notice {
  font-size: 0.9rem;
  color: #6c757d;
  text-align: center;
  margin-top: 0.5rem;
  font-style: italic;
}

/**
 * Responsive adjustments for smaller screens
 */
@media (max-width: 768px) {
  .item-detail-container {
    grid-template-columns: 1fr;
  }

  .gallery-column {
    max-height: 400px;
  }

  .gallery-column :deep(img) {
    max-height: 400px;
  }

  .item-title {
    font-size: 1.5em;
  }

  .item-price {
    font-size: 1.4em;
  }

  .details-column {
    padding: 1rem;
    gap: 1.2rem;
  }

  .action-buttons {
    flex-direction: column;
  }
}

/* Extra small devices */
@media (max-width: 480px) {
  .item-details-page {
    margin: 1rem auto;
  }

  .gallery-column {
    max-height: 300px;
  }

  .gallery-column :deep(img) {
    max-height: 300px;
  }
}
</style>
