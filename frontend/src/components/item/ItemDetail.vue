<template>
  <div class="item-details-page">
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>{{ $t('APP_LISTING_LOADING_ITEM_DETAILS') }}</p>
    </div>

    <div v-else-if="error" class="error-state">
      <h2>{{ $t('APP_LISTING_UNABLE_TO_LOAD_ITEM_DETAILS') }}</h2>
      <p>{{ errorMessage }}</p>
      <button @click="retryLoading" class="retry-button">{{ $t('RETRY') }}</button>
    </div>

    <div v-else-if="item" class="item-detail-container">
      <div class="gallery-column">
        <ImageGallery
          v-if="item.imageUrls && item.imageUrls.length > 0"
          :imageUrls="item.imageUrls"
          :alt-text="item.title"
        />
        <div v-else class="no-image-placeholder">
          <span class="placeholder-icon">📷</span>
          <p>{{ $t('APP_LISTING_NO_IMAGES_AVAILABLE') }}</p>
        </div>
      </div>

      <div class="details-column">
        <div class="header">
          <h1 class="item-title">{{ item.title }}</h1>
          <HeartIcon
            class="heart-icon-details"
            :itemId="item.id.toString()"
          />
        </div>

        <p class="item-price">{{ formatPrice(item.price) }}</p>

        <div class="item-description">
          <h3>{{ $t('APP_LISTING_CREATE_NEW_DESCRIPTION_LABEL' )}}</h3>
          <p>{{ item.description || 'No description provided.' }}</p>
        </div>

        <div class="seller-info">
          <h3>{{ $t('APP_CONTACT_INFORMATION' )}}</h3>
          <p v-if="item.contact && item.sellerId">
            <strong>Seller:</strong>
            <router-link :to="{ name: 'public-profile', params: { userId: item.sellerId.toString() } }" class="seller-link">
              {{ item.contact }} </router-link>
          </p>
          <p v-else-if="item.contact">
            <strong>Seller:</strong> {{ item.contact }}
          </p>
          <p v-else class="no-info">Contact information unavailable</p>
        </div>
        <div class="action-buttons">
          <router-link
            :to="{ name: 'map', query: { highlightItem: item.id } }"
            custom
            v-slot="{ navigate }"
          >
            <button @click="navigate" class="edit-button">
              {{$t('GO_TO_LOCATION')}}
            </button>
          </router-link>
          <button
            @click="startConversation"
            class="edit-button"
            :disabled="!canContactSeller || startingConversation"
          >
            {{ startingConversation ? $t('APP_CONVERSATION_STARTING') : $t('APP_ITEM_CONTACT_SELLER') }}
          </button>
          <button
            @click="openBidModal"
            class="edit-button"
            :disabled="!isUserLoggedIn || isUserSeller"
          >
            {{ $t('APP_LISTING_PLACE_BID') }}
          </button>
          <div v-if="isAdmin" class="admin-actions">
            <button @click="confirmAdminDeleteItem" class="delete-button">
              {{ $t('APP_ADMIN_DELETE_ITEM') }}
            </button>
          </div>
        </div>

        <div v-if="isUserSeller" class="seller-notice">
          {{ $t('ERROR_BID_OWN_ITEM') }}
        </div>
        <div v-else-if="!isUserLoggedIn" class="login-notice">
          {{ $t('ERROR_LOG_IN_TO_BID') }}
        </div>
      </div>
    </div>

    <div v-else class="not-found-state">
      <h2>{{ $t('NOT_FOUND') }}</h2>
      <p>{{ $t('NOT_FOUND_ITEM_EXTENSION')}}</p>
      <router-link to="/" class="home-link">Browse Other Items</router-link>
    </div>

    <BidModal
      :is-open="showBidModal"
      :item-id="itemId"
      :item-title="item ? item.title : ''"
      :current-price="item ? item.price : 0"
      :initial-bid="null"
      @close="closeBidModal"
      @bid-placed="handleBidPlaced"
    />
  </div>
</template>

<script setup lang="ts">
/**
 * @fileoverview ItemDetail component for displaying detailed information about an item.
 * <p>This component provides functionality for:</p>
 * <ul>
 * <li>Detailed item display with images and information</li>
 * <li>Link to Seller's public profile page</li>
 * <li>Favoriting/unfavoriting items</li>
 * <li>Initiating conversations with sellers</li>
 * <li>Placing bids through a modal interface</li>
 * <li>Administrative actions for item deletion</li>
 * <li>View tracking for analytics</li>
 * </ul>
 */

import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import ImageGallery from '@/components/image/ImageGallery.vue';
import HeartIcon from '@/components/item/HeartIcon.vue';
import BidModal from '@/components/modals/BidModal.vue';
import type { ItemDetailsType } from '@/models/Item'; // Ensure ItemDetailsType includes sellerId
import { adminDeleteItem, fetchItem, recordItemView } from '@/services/ItemService.ts';
import { initiateConversation } from '@/services/MessageService';
import { useUserStore } from '@/stores/UserStore';

// Component props
const props = defineProps<{
  itemId: string | number
}>();

// Reactive state variables
const item = ref<ItemDetailsType | null>(null);
const loading = ref(true);
const error = ref(false);
const errorMessage = ref("Failed to load item details. Please try again later.");
const showBidModal = ref(false);
const startingConversation = ref(false); // State for disabling button during API call

// Dependencies
const userStore = useUserStore();
const router = useRouter();

// Computed properties
const isUserLoggedIn = computed(() => userStore.loggedIn);

const isUserSeller = computed(() => {
  if (!item.value || !userStore.userId) return false;
  // Check using sellerId (most reliable)
  if (item.value.sellerId) {
    return item.value.sellerId.toString() === userStore.userId.toString();
  }
  // Fallback check using contact name (less reliable)
  if (!userStore.username) return false;
  // Ensure contact name matches the *display name* or *username* from the store
  return item.value.contact === userStore.username; // Assuming username is the display name here
});

const canContactSeller = computed(() => {
  return isUserLoggedIn.value && !isUserSeller.value && !!item.value?.sellerId;
});

const isAdmin = computed(() => userStore.getUserRole === 'ADMIN');

// --- Methods ---

/**
 * Formats the price with currency symbol (kr) and proper locale formatting.
 */
function formatPrice(price: number | null): string {
  if (price === null || price === undefined) return 'Price not specified';
  return `${price.toLocaleString('no-NO')} kr`;
}

/**
 * Opens the bidding modal if user is eligible.
 */
function openBidModal() {
  if (!isUserLoggedIn.value || isUserSeller.value) return;
  showBidModal.value = true;
}

/**
 * Closes the bidding modal.
 */
function closeBidModal() {
  showBidModal.value = false;
}

/**
 * Handles bid placement success events.
 */
function handleBidPlaced() {
  setTimeout(() => {
    closeBidModal();
    // Potentially refresh item data if bid affects price display
    // loadItemData();
  }, 1500); // Close modal after 1.5 seconds
}

/**
 * Loads item data and records a view.
 */
async function loadItemData() {
  loading.value = true;
  error.value = false;
  item.value = null;

  if (!props.itemId) {
    loading.value = false;
    error.value = true;
    errorMessage.value = "No item ID provided.";
    return;
  }

  try {
    const [fetchedItem] = await Promise.all([
      fetchItem(props.itemId),
      recordItemView(props.itemId) // Record view might fail silently if needed
    ]);

    const itemData = Array.isArray(fetchedItem) ? fetchedItem[0] : fetchedItem;

    if (!itemData) {
      throw new Error("Item not found");
    }
    // Make sure sellerId is included in the fetched data
    item.value = itemData;
  } catch (err) {
    error.value = true;
    if (err instanceof Error && err.message.includes("404")) {
      errorMessage.value = "The requested item could not be found.";
    } else {
      errorMessage.value = err instanceof Error
        ? err.message
        : "Failed to load item details. Please try again later.";
    }
    item.value = null;
  } finally {
    loading.value = false;
  }
}

/**
 * Retries loading the item data.
 */
function retryLoading() {
  loadItemData();
}

/**
 * Initiates or navigates to a conversation with the seller.
 */
async function startConversation() {
  if (!item.value || !canContactSeller.value || startingConversation.value) return;
  if (!item.value.sellerId) {
    alert("Cannot contact seller: Seller information is unavailable.");
    return;
  }

  startingConversation.value = true;
  try {
    const conversationId = await initiateConversation(props.itemId);
    router.push({
      name: 'messages-conversation', // Ensure this matches router/index.ts
      params: { conversationId: conversationId.toString() }
    });
  } catch (err) {
    const message = err instanceof Error ? err.message : 'An unknown error occurred.';
    alert(`Could not start conversation: ${message}. Please try again later.`);
  } finally {
    startingConversation.value = false;
  }
}

/**
 * Confirms and processes administrative item deletion.
 */
async function confirmAdminDeleteItem() {
  if (!item.value || !isAdmin.value) return;

  const confirmDelete = confirm(
    `ADMIN ACTION: Are you sure you want to permanently delete item "${item.value.title}" (ID: ${props.itemId})?`
  );

  if (confirmDelete) {
    try {
      await adminDeleteItem(props.itemId); // Assumes this function exists
      alert('Item deleted successfully by admin.');
      router.push({ name: 'home' }); // Redirect after deletion
    } catch (err) {
      alert(`Failed to delete item: ${err instanceof Error ? err.message : 'Unknown error'}`);
    }
  }
}

// --- Lifecycle Hook ---
onMounted(() => {
  loadItemData();
});
</script>

<style scoped>
/* Base styles for the page */
.item-details-page {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 1rem;
}

/* Container for item details (grid layout) */
.item-detail-container {
  display: grid;
  grid-template-columns: 1fr 1fr; /* Two columns on larger screens */
  gap: 2rem;
  margin-bottom: 2rem;
}

/* Loading state styles */
.loading-state {
  text-align: center;
  padding: 4rem;
  color: var(--vt-c-text-light-1);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}

.loading-spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top: 4px solid #007bff; /* Use a primary color */
  width: 40px;
  height: 40px;
  margin-bottom: 1rem;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Error and Not Found states */
.error-state, .not-found-state {
  text-align: center;
  padding: 3rem 1rem;
  background-color: var(--color-background-soft);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  margin-bottom: 2rem;
  color: var(--vt-c-red-dark); /* Error color */
}
.not-found-state {
  color: var(--vt-c-text-light-2); /* Neutral color for not found */
}
.error-state h2, .not-found-state h2 {
  margin-bottom: 0.5rem;
  color: var(--color-heading);
}

/* Buttons for retry and linking home */
.retry-button, .home-link {
  display: inline-block;
  background-color: #007bff;
  color: white;
  border: none;
  padding: 0.6rem 1.2rem;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  text-decoration: none;
  margin-top: 1rem;
  transition: background-color 0.2s ease;
}
.retry-button:hover, .home-link:hover {
  background-color: #0056b3;
}

/* Gallery column styles */
.gallery-column {
  border-radius: 8px;
  overflow: hidden;
  background-color: var(--vt-c-white);
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  max-height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
}
/* Deep selector for images inside ImageGallery */
.gallery-column :deep(img) {
  max-width: 100%;
  height: auto;
  max-height: 500px; /* Match column max-height */
  object-fit: contain;
  display: block;
}
.gallery-column :deep(.image-gallery) {
  width: 100%;
  height: 100%;
  max-height: 500px;
  overflow: hidden;
  display: flex;
  align-items: center;
}
.no-image-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 300px;
  width: 100%;
  color: #888;
  font-size: 1.1em;
  background-color: #e9ecef; /* Light gray background */
}
.placeholder-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

/* Details column styles */
.details-column {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  padding: 1.5rem;
  background-color: var(--vt-c-white);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

/* Header section within details */
.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start; /* Align title and icon */
  gap: 1rem;
  border-bottom: 1px solid #eee;
  padding-bottom: 1rem;
  margin-bottom: 0.5rem;
}
.item-title {
  font-size: 1.8em;
  font-weight: 600;
  margin: 0;
  color: #333;
  line-height: 1.3;
  flex-grow: 1; /* Allow title to take available space */
}
.heart-icon-details {
  font-size: 1.5rem; /* Adjust size as needed */
  color: #adb5bd; /* Default gray color */
  cursor: pointer;
  margin-top: 0.2rem; /* Align vertically */
  transition: color 0.2s ease;
  flex-shrink: 0; /* Prevent icon from shrinking */
}

/* Price styling */
.item-price {
  font-size: 1.6em;
  font-weight: bold;
  color: var(--vt-c-teal-dark); /* Use theme color */
  margin: 0;
}

/* Description and Seller Info sections */
.item-description,
.seller-info {
  padding-top: 0.5rem;
}
.item-description h3,
.seller-info h3 {
  font-size: 1.1em;
  font-weight: 600;
  margin-bottom: 0.6rem;
  color: #495057; /* Dark gray heading */
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 0.5rem;
}
.item-description p,
.seller-info p {
  margin: 0;
  color: #444; /* Dark text for readability */
  line-height: 1.6;
}
.no-info {
  color: #999;
  font-style: italic;
}

/* Seller link style */
.seller-link {
  color: var(--vt-c-teal-dark); /* Use theme link color */
  text-decoration: none;
  font-weight: bold;
}
.seller-link:hover {
  text-decoration: underline;
}

/* Action buttons container */
.action-buttons {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
  flex-wrap: wrap; /* Allow buttons to wrap on smaller screens */
}
.action-buttons button,
.action-buttons .edit-button { /* Include router-link buttons */
  flex: 1 1 150px; /* Allow buttons to grow/shrink but have a base width */
  min-width: 150px; /* Minimum width */
  height: 48px;
  font-size: 1rem; /* Adjusted font size */
  padding: 10px 16px; /* Adjusted padding */
  border: none;
  border-radius: 5px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  color: white;
  display: inline-flex; /* Align text and icons */
  align-items: center;
  justify-content: center;
  text-align: center; /* Ensure text is centered */
  line-height: 1.2; /* Adjust line height */
}

.edit-button { /* Specific style for general/edit buttons */
  background-color: var(--vt-c-teal-dark);
}
.delete-button {
  background-color: var(--vt-c-red-dark);
}
.action-buttons button:hover:not(:disabled),
.action-buttons .edit-button:hover:not(:disabled) {
  opacity: 0.9;
  transform: translateY(-1px); /* Subtle lift effect */
}
.action-buttons button:disabled,
.action-buttons .edit-button:disabled {
  background-color: #cccccc;
  color: #666666;
  cursor: not-allowed;
  opacity: 0.7;
}

/* Admin specific actions container */
.admin-actions {
  /* display: flex; If you want them inline with others */
  /* gap: 1rem; */
  width: 100%; /* Make admin button take full width if needed */
}


/* Notices for login/seller status */
.login-notice, .seller-notice {
  font-size: 0.9rem;
  color: #6c757d; /* Muted text color */
  text-align: center;
  margin-top: 0.5rem;
  padding: 0.5rem;
  background-color: #f8f9fa; /* Light background */
  border-radius: 4px;
}

/* --- Responsive Design Adjustments --- */
@media (max-width: 992px) {
  /* Switch to single column layout on tablets */
  .item-detail-container {
    grid-template-columns: 1fr;
  }
  .gallery-column {
    max-height: 450px;
  }
  .gallery-column :deep(img),
  .gallery-column :deep(.image-gallery) {
    max-height: 450px;
  }
}

@media (max-width: 768px) {
  /* Adjust typography and padding for smaller tablets */
  .item-title { font-size: 1.6em; }
  .item-price { font-size: 1.5em; }
  .details-column {
    padding: 1rem;
    gap: 1.2rem;
  }
  .gallery-column { max-height: 400px; }
  .gallery-column :deep(img),
  .gallery-column :deep(.image-gallery) { max-height: 400px; }
  /* Stack action buttons vertically */
  .action-buttons {
    flex-direction: column;
    gap: 0.8rem;
  }
  .action-buttons button,
  .action-buttons .edit-button {
    min-width: 100%; /* Make buttons full width */
  }
}

@media (max-width: 480px) {
  /* Adjustments for small mobile screens */
  .item-details-page { margin: 1rem auto; }
  .gallery-column {
    max-height: 300px;
    border-radius: 4px; /* Slightly smaller radius */
  }
  .gallery-column :deep(img),
  .gallery-column :deep(.image-gallery) { max-height: 300px; }
  .item-title { font-size: 1.4em; }
  .item-price { font-size: 1.3em; }
  .action-buttons button,
  .action-buttons .edit-button {
    padding: 0.8rem 1rem; /* Adjust padding */
    font-size: 0.95rem;
  }
}
</style>
