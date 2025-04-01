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
      <div v-for="item in userItems" :key="item.id" class="item-preview-card">
        <img
          :src="item.imageUrl || '/placeholder-image.png'"
          :alt="item.title"
          class="item-image"
          @error="handleImageError"
        />
        <div class="item-details">
          <h3>{{ item.title }}</h3>
          <p class="item-price">Price: {{ item.price }} kr</p>
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
// Adjust path as necessary if you create a user-specific fetch function
import { fetchPreviewItems } from '@/services/itemService/itemPreviewService'
import type { ItemPreviewType } from '@/models/Item' //
// Optional: Import a dedicated card component
// import ItemPreviewCard from '@/components/ItemPreviewCard.vue';

const userItems = ref<ItemPreviewType[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)

// Function to fetch items - REMEMBER TO MODIFY for user-specific items
async function loadUserItems() {
  isLoading.value = true
  error.value = null
  try {
    // TODO: Replace fetchPreviewItems with a function that fetches items
    //       specific to the logged-in user (e.g., fetchMyItems()).
    //       This requires backend changes and a new service function.
    console.log('Fetching items... (Using placeholder function)') // Added log
    userItems.value = await fetchPreviewItems()
    console.log('Items fetched:', userItems.value) // Added log
  } catch (err) {
    console.error('Failed to load user items:', err)
    error.value = 'Could not load your items. Please try again later.'
    userItems.value = [] // Clear items on error
  } finally {
    isLoading.value = false
  }
}

// Handle cases where an image fails to load
function handleImageError(event: Event) {
  const imgElement = event.target as HTMLImageElement
  imgElement.src = '/placeholder-image.png' // Path to your placeholder image
}

// Fetch items when the component is mounted
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

.loading-indicator,
.error-message,
.no-items-message {
  text-align: center;
  margin-top: 2rem;
  color: var(--color-text);
}

.error-message p {
  color: red;
  font-weight: bold;
}

.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); /* Responsive grid */
  gap: 1.5rem; /* Spacing between items */
  margin-top: 1.5rem;
}

.item-preview-card {
  border: 1px solid var(--color-border);
  border-radius: 8px;
  overflow: hidden;
  background-color: var(--color-background-soft);
  transition: box-shadow 0.3s ease;
}

.item-preview-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.item-image {
  width: 100%;
  height: 180px; /* Fixed height for consistency */
  object-fit: cover; /* Crop image nicely */
  display: block;
  background-color: #eee; /* Placeholder background */
}

.item-details {
  padding: 1rem;
}

.item-details h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.1em;
  color: var(--color-heading);
  /* Prevent long titles from breaking layout */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-price {
  font-weight: bold;
  color: hsla(160, 100%, 37%, 1); /* Vue green */
  margin-bottom: 0;
}
</style>
