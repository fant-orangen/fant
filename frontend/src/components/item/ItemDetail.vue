<template>
  <div class="item-details-page">
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>Loading item details...</p>
    </div>

    <div v-else-if="error" class="error-state">
      <h2>Unable to load item</h2>
      <p>{{ errorMessage }}</p>
      <button @click="retryLoading" class="retry-button">Try Again</button>
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
          <p>No Images Available</p>
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
          <h3>Description</h3>
          <p>{{ item.description || 'No description provided.' }}</p>
        </div>

        <div class="seller-info">
          <h3>Contact Information</h3>
          <p v-if="item.contact"><strong>Seller:</strong> {{ item.contact }}</p>
          <p v-else class="no-info">Contact information unavailable</p>
        </div>

        <div class="action-buttons">
          <button
            @click="startConversation"
            class="edit-button"
            :disabled="!canContactSeller || startingConversation"
          >
            {{ startingConversation ? 'Starting...' : 'Contact Seller' }}
          </button>

          <button
            @click="openBidModal"
            class="edit-button"
            :disabled="!isUserLoggedIn || isUserSeller"
          >
            Place Bid
          </button>
          <div v-if="isAdmin" class="admin-actions">  <button @click="confirmAdminDeleteItem" class="delete-button">
            Admin Delete Item
          </button>
          </div>
        </div>

        <div v-if="isUserSeller" class="seller-notice">
          You cannot bid on or contact the seller for your own item.
        </div>
        <div v-else-if="!isUserLoggedIn" class="login-notice">
          Please log in to place a bid or contact the seller.
        </div>
      </div>
    </div>

    <div v-else class="not-found-state">
      <h2>Item Not Found</h2>
      <p>The requested item could not be found or may have been removed.</p>
      <router-link to="/" class="home-link">Browse Other Items</router-link>
    </div>

    <BidModal
      :is-open="showBidModal"
      :item-id="itemId"
      :item-title="item ? item.title : ''"
    :current-price="item ? item.price : 0"
    :initial-bid="null" @close="closeBidModal"
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
 * - Seller contact details (and ID for messaging logic)
 * - Ability to favorite/save the item
 * - Contact seller functionality (navigates to or starts conversation)
 * - Bid placement functionality
 *
 * Handles loading, error, and not found states.
 */

import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router'; // <-- Import useRouter
import ImageGallery from '@/components/image/ImageGallery.vue';
import HeartIcon from '@/components/item/HeartIcon.vue';
import BidModal from '@/components/modals/BidModal.vue';
import type { ItemDetailsType } from '@/models/Item'; // Ensure ItemDetailsType includes sellerId
import {adminDeleteItem, fetchItem, recordItemView} from '@/services/ItemService.ts';
import { initiateConversation } from '@/services/MessageService'; // <-- Import the new service function
import { useUserStore } from '@/stores/UserStore';

/**
 * Component props
 * @property {string|number} itemId - The ID of the item to display
 */
const props = defineProps<{
  itemId: string | number
}>();

// --- Component State ---
const item = ref<ItemDetailsType | null>(null);
const loading = ref(true);
const error = ref(false);
const errorMessage = ref("Failed to load item details. Please try again later.");
const showBidModal = ref(false);
const startingConversation = ref(false); // State for disabling button during API call

// --- Hooks and Stores ---
const userStore = useUserStore();
const router = useRouter(); // <-- Get router instance

// --- Computed Properties ---

/**
 * Determines if the user can contact the seller.
 * Requires user to be logged in, not be the seller, and item data must have sellerId.
 */
const canContactSeller = computed(() => {
  return isUserLoggedIn.value && !isUserSeller.value && !!item.value?.sellerId;
});

/**
 * Checks if a user is logged in.
 */
const isUserLoggedIn = computed(() => {
  return userStore.loggedIn;
});

/**
 * Checks if the current logged-in user is the seller of the item.
 * Primarily uses sellerId for comparison.
 */
const isUserSeller = computed(() => {
  if (!item.value || !userStore.userId) return false;
  // Check using sellerId (most reliable)
  if (item.value.sellerId) {
    return item.value.sellerId.toString() === userStore.userId.toString();
  }
  // Fallback check using contact name (less reliable, assumes username matches contact field)
  if (!userStore.username) return false;
  return item.value.contact === userStore.username;
});

// --- Methods ---

/**
 * Formats the price with currency symbol (kr) and proper locale formatting.
 * @param {number|null} price - The price to format.
 * @returns {string} The formatted price string or a placeholder.
 */
function formatPrice(price: number | null): string {
  if (price === null || price === undefined) return 'Price not specified';
  // Uses Norwegian locale for formatting (kr)
  return `${price.toLocaleString('no-NO')} kr`;
}

/**
 * Opens the bidding modal if conditions are met.
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
 * Handles the event after a bid has been successfully placed.
 * Logs success and closes the modal after a short delay.
 */
function handleBidPlaced() {
  console.log("Bid placed successfully event received");
  setTimeout(() => {
    closeBidModal();
  }, 1500); // Close modal after 1.5 seconds
}

/**
 * Loads the item data from the API and records a view.
 * Fetches item details using props.itemId.
 */
async function loadItemData() {
  loading.value = true;
  error.value = false;
  item.value = null; // Reset item state

  if (!props.itemId) {
    loading.value = false;
    error.value = true;
    errorMessage.value = "No item ID provided.";
    return;
  }

  try {
    // Fetch item details and record the view concurrently
    const [fetchedItem, viewResponse] = await Promise.all([
      fetchItem(props.itemId),
      recordItemView(props.itemId) // Record view might fail silently if needed
    ]);

    // Handle potential array response (though single item expected)
    const itemData = Array.isArray(fetchedItem) ? fetchedItem[0] : fetchedItem;

    if (!itemData) {
      throw new Error("Item not found");
    }

    item.value = itemData; // Assign fetched data to component state

    // Log crucial details for debugging conversation logic
    console.log("Item details loaded:", item.value);

    if (viewResponse) {
      console.log(`View recorded status: ${viewResponse.status}`);
    } else {
      console.warn("View recording did not return a response.");
    }

  } catch (err) {
    console.error('Error fetching item details:', err);
    error.value = true;
    // Provide more specific error message if possible
    if (err instanceof Error && err.message.includes("404")) { // Example check
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
 * Retries loading the item data if there was an initial error.
 */
function retryLoading() {
  loadItemData();
}


/**
 * Initiates or navigates to a conversation with the seller.
 * Calls the backend endpoint to get an existing or new conversation identifier.
 */
async function startConversation() {
  // Initial checks: item loaded, user can contact, not already processing
  if (!item.value || !canContactSeller.value || startingConversation.value) {
    console.warn("Conditions not met for starting conversation:", {
      itemExists: !!item.value,
      canContact: canContactSeller.value,
      alreadyStarting: startingConversation.value
    });
    // Optionally provide user feedback here if needed
    return;
  }
  // Explicit check for sellerId before proceeding
  if (!item.value.sellerId) {
    console.error("Cannot start conversation: Seller ID is missing from item data.");
    alert("Cannot contact seller: Seller information is unavailable.");
    return;
  }

  startingConversation.value = true; // Indicate processing started
  console.log(`Initiating conversation for item: ${props.itemId}`);

  try {
    // Call the service function that hits the backend /initiate endpoint
    const conversationId = await initiateConversation(props.itemId);

    // Navigate to the conversation view using the identifier returned by the backend
    console.log(`Received conversation identifier: ${conversationId}. Navigating...`);
    router.push({
      name: 'messages-conversation', // Ensure this route name matches router/index.ts
      params: { conversationId: conversationId.toString() }
    });

  } catch (err) {
    console.error('Failed to start or find conversation:', err);
    // Provide user-friendly error feedback
    const message = err instanceof Error ? err.message : 'An unknown error occurred.';
    alert(`Could not start conversation: ${message}. Please try again later.`);
  } finally {
    startingConversation.value = false; // Indicate processing finished
  }
}

/**
 * Checks if you want to delete
 */
async function confirmAdminDeleteItem() {
  if (!item.value) return;

  const confirmDelete = confirm(
    `ADMIN ACTION: Are you sure you want to permanently delete item "${item.value.title}" (ID: ${props.itemId})?`
  );

  if (confirmDelete) {
    try {
      await adminDeleteItem(props.itemId);
      alert('Item deleted successfully by admin.');
      router.push({ name: 'home' });
    } catch (err) {
      console.error("Admin item deletion failed:", err);
      alert(`Failed to delete item: ${err instanceof Error ? err.message : 'Unknown error'}`);
    }
  }
}

/**
 * Checks if user is admin
 */
const isAdmin = computed(() => {
  return userStore.getUserRole === 'ADMIN';
});

-

// Load item data when the component is mounted
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
  grid-template-columns: 1fr 1fr; /* Default to two columns */
  gap: 2rem;
  margin-bottom: 2rem;
}

/**
 * Loading state styling
 */
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
  border-top: 4px solid #007bff; /* Blue spinner */
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
  background-color: var(--color-background-soft);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  margin-bottom: 2rem;
  color: var(--vt-c-red-dark);
}
.not-found-state {
  color: var(--vt-c-text-light-2);
}
.error-state h2, .not-found-state h2 {
  margin-bottom: 0.5rem;
  color: var(--color-heading);
}

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
  background-color: #0056b3; /* Darker blue on hover */
}

/**
 * Gallery column styling
 */
.gallery-column {
  border-radius: 8px;
  overflow: hidden;
  background-color: var(--vt-c-white); /* Light background for gallery area */
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  max-height: 500px; /* Adjust as needed */
  display: flex; /* Center placeholder content */
  align-items: center;
  justify-content: center;
}

/* Target images within the gallery component if needed */
.gallery-column :deep(img) {
  max-width: 100%;
  height: auto;
  max-height: 500px; /* Match container */
  object-fit: contain; /* Ensure image fits without distortion */
  display: block;
}

/* Ensure ImageGallery component itself doesn't cause overflow */
.gallery-column :deep(.image-gallery) {
  width: 100%;
  height: 100%; /* Make gallery fill the column */
  max-height: 500px;
  overflow: hidden;
  display: flex; /* Needed for vertical centering of single image */
  align-items: center;
}

/* Placeholder for when no images are available */
.no-image-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%; /* Fill gallery column */
  min-height: 300px; /* Ensure decent size */
  width: 100%;
  color: #888;
  font-size: 1.1em;
  background-color: #e9ecef; /* Slightly darker placeholder background */
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
  gap: 1.5rem; /* Spacing between sections */
  padding: 1.5rem;
  background-color: var(--vt-c-white);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

/**
 * Header section (Title + Heart Icon)
 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start; /* Align items to top */
  gap: 1rem;
  border-bottom: 1px solid #eee;
  padding-bottom: 1rem;
  margin-bottom: 0.5rem; /* Add slight space below header */
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
  font-size: 1.5rem;
  color: #adb5bd; /* Default grey color */
  cursor: pointer;
  margin-top: 0.2rem; /* Align slightly better with title */
  transition: color 0.2s ease;
  flex-shrink: 0; /* Prevent shrinking */
}
/* Add active state style if HeartIcon component doesn't handle it internally */
/* .heart-icon-details.active { color: #dc3545; } */

/**
 * Price display styling
 */
.item-price {
  font-size: 1.6em;
  font-weight: bold;
  color: var(--vt-c-teal-dark);
  margin: 0;
}

.action-buttons {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
  flex-wrap: wrap;
}

.action-buttons button {
  flex: 1 1 150px;
  min-width: 150px;
  height: 48px;
  font-size: 1.1rem;
  padding: 12px 18px;
}

/**
 * Section styling (Description, Seller Info)
 */
.item-description,
.seller-info {
  padding-top: 0.5rem; /* Add space above section content */
}

.item-description h3,
.seller-info h3 {
  font-size: 1.1em;
  font-weight: 600;
  margin-bottom: 0.6rem;
  color: #495057; /* Darker grey heading */
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 0.5rem;
}

.item-description p,
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
  margin-top: 1rem; /* More space above buttons */
  flex-wrap: wrap; /* Allow wrapping on small screens */
}
/**
 * Login and seller notices styling
 */
.login-notice, .seller-notice {
  font-size: 0.9rem;
  color: #6c757d; /* Grey notice text */
  text-align: center;
  margin-top: 0.5rem; /* Space above notice */
  padding: 0.5rem;
  background-color: #f8f9fa; /* Light background for notice */
  border-radius: 4px;
}

/**
 * Responsive adjustments
 */
@media (max-width: 992px) { /* Adjust breakpoint if needed */
  .item-detail-container {
    grid-template-columns: 1fr; /* Stack columns */
  }

  .gallery-column {
    max-height: 450px; /* Adjust gallery height */
  }
  .gallery-column :deep(img),
  .gallery-column :deep(.image-gallery) {
    max-height: 450px;
  }
}

@media (max-width: 768px) {
  .item-title {
    font-size: 1.6em;
  }
  .item-price {
    font-size: 1.5em;
  }
  .details-column {
    padding: 1rem; /* Reduce padding */
    gap: 1.2rem;
  }
  .gallery-column {
    max-height: 400px;
  }
  .gallery-column :deep(img),
  .gallery-column :deep(.image-gallery) {
    max-height: 400px;
  }
  .action-buttons {
    /* Buttons already wrap, ensure they stack nicely */
    flex-direction: column;
    gap: 0.8rem;
  }
  .contact-button, .bid-button {
    flex-basis: auto; /* Reset flex basis for stacking */
  }
}

/* Extra small devices */
@media (max-width: 480px) {
  .item-details-page {
    margin: 1rem auto; /* Reduce page margin */
  }
  .gallery-column {
    max-height: 300px; /* Further reduce gallery height */
    border-radius: 4px; /* Slightly smaller radius */
  }
  .gallery-column :deep(img),
  .gallery-column :deep(.image-gallery) {
    max-height: 300px;
  }
  .item-title {
    font-size: 1.4em;
  }
  .item-price {
    font-size: 1.3em;
  }
  .contact-button, .bid-button {
    padding: 0.7rem 1rem;
    font-size: 0.95rem;
  }
}
</style>
