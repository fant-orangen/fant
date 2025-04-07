<template>
  <div class="profile-ads-view">
    <h2>{{ $t('MY_LISTINGS') }}</h2>

    <div v-if="isLoading" class="loading-indicator">
      <p>Loading your items...</p> </div>

    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
    </div>

    <div v-else-if="userItems.length > 0" class="items-grid">
      <div v-for="item in userItems" :key="item.id" class="item-preview-card" @click="navigateToItem(item.id)">
        <img
          :src="item.imageUrl || '/placeholder-image.png'"
          :alt="item.title"
          class="item-image"
          @error="handleImageError"
          loading="lazy" />
        <div class="item-details">
          <h3>{{ item.title }}</h3>
          <p class="item-price">{{ formatPrice(item.price) }}</p>
        </div>
      </div>
    </div>

    <div v-else class="no-items-message">
      <p>You haven't listed any items yet.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router' // Import useRouter
// Import the specific function to fetch the user's items
import { fetchMyItems } from '@/services/ItemService' // <-- Use fetchMyItems
import type { ItemPreviewType } from '@/models/Item'
// Optional: Import a dedicated card component
// import ItemPreviewCard from '@/components/ItemPreviewCard.vue';

const router = useRouter() // Initialize router
const userItems = ref<ItemPreviewType[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)

// Function to fetch items listed by the logged-in user
async function loadUserItems() {
  isLoading.value = true
  error.value = null
  try {
    // Use the new service function specific to the logged-in user
    console.log('Fetching logged-in user items...')
    userItems.value = await fetchMyItems() // <-- Call fetchMyItems
    console.log('User items fetched:', userItems.value.length)
  } catch (err: any) {
    console.error('Failed to load user items:', err)
    // Provide a more specific error message if possible
    if (err.response?.status === 401 || err.response?.status === 403) {
      error.value = 'Authentication error. Please log in again.'
      // Optionally redirect to login: router.push('/login');
    } else {
      error.value = 'Could not load your items. Please try again later.'
    }
    userItems.value = [] // Clear items on error
  } finally {
    isLoading.value = false
  }
}

// Handle cases where an image fails to load
function handleImageError(event: Event) {
  const imgElement = event.target as HTMLImageElement
  // Ensure placeholder path is correct relative to the public directory
  imgElement.src = '/placeholder-image.png'
}

// Function to format price (example)
function formatPrice(price: number | null | undefined): string {
  if (price === null || price === undefined) {
    return 'N/A';
  }
  // Example formatting for Norwegian Krone (NOK)
  return price.toLocaleString('no-NO', { style: 'currency', currency: 'NOK' });
}

// Function to navigate to item detail page
function navigateToItem(itemId: string | number) {
  router.push({ name: 'item-detail', params: { id: itemId } });
}

// Fetch items when the component is mounted
onMounted(() => {
  loadUserItems()
})

// --- Placeholder functions for Edit/Delete ---
/*
function editItem(itemId: string | number) {
    console.log('Edit item:', itemId);
    // Navigate to edit page: router.push({ name: 'edit-item', params: { id: itemId } });
}

async function deleteItem(itemId: string | number) {
    if (!confirm('Are you sure you want to delete this item?')) return;
    console.log('Delete item:', itemId);
    try {
        // await deleteItemService(itemId); // Call your delete service function
        // Refresh list after deletion
        // userItems.value = userItems.value.filter(item => item.id !== itemId);
    } catch (err) {
        console.error('Failed to delete item:', err);
        // Show error message to user
    }
}
*/
</script>

<style scoped>
.profile-ads-view {
  padding: 1rem;
  max-width: 1200px;
  margin: 0 auto;
}

.loading-indicator,
.error-message,
.no-items-message {
  text-align: center;
  margin-top: 2rem;
  color: var(--color-text);
}

.error-message p {
  color: red; /* Or use a specific error color variable */
  font-weight: bold;
}

.items-grid {
  display: grid;
  /* Adjust minmax for desired card size */
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 1.5rem; /* Spacing between items */
  margin-top: 1.5rem;
}

.item-preview-card {
  border: 1px solid var(--color-border);
  border-radius: 8px;
  overflow: hidden;
  background-color: var(--color-background-soft);
  transition: box-shadow 0.3s ease, transform 0.2s ease; /* Added transform */
  cursor: pointer; /* Indicate clickable */
  display: flex; /* Use flexbox for better control */
  flex-direction: column; /* Stack image and details vertically */
}

.item-preview-card:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-3px); /* Slight lift effect */
}

.item-image {
  width: 100%;
  height: 180px; /* Adjust height as needed */
  object-fit: cover; /* Crop image nicely */
  display: block;
  background-color: #f0f0f0; /* Lighter placeholder background */
}

.item-details {
  padding: 1rem;
  flex-grow: 1; /* Allow details to fill remaining space */
  display: flex;
  flex-direction: column;
  justify-content: space-between; /* Push price down */
}

.item-details h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem; /* Slightly larger title */
  color: var(--color-heading);
  /* Prevent long titles from breaking layout */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-price {
  font-weight: 600; /* Slightly bolder price */
  color: hsla(160, 100%, 37%, 1); /* Standard Vue green */
  margin-top: 0.5rem; /* Add space above price */
  margin-bottom: 0;
  font-size: 1rem;
}

.item-actions {
  margin-top: 1rem;
  display: flex;
  gap: 0.5rem;
}

.item-actions button {
  padding: 0.3rem 0.6rem;
  font-size: 0.85rem;
  cursor: pointer;
}

/* Add styles for a potential 'Create Listing' button */
.no-items-message a {
  display: inline-block;
  margin-top: 1rem;
  padding: 0.6rem 1.2rem;
  background-color: hsla(160, 100%, 37%, 1);
  color: white;
  text-decoration: none;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.no-items-message a:hover {
  background-color: hsla(160, 100%, 30%, 1);
}
</style>
