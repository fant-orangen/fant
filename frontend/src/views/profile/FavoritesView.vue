<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { fetchFavoriteItems } from '@/services/itemService';
import type { ItemPreviewType } from '@/models/Item'; //

// Optional: Import a dedicated card component
// import ItemPreviewCard from '@/components/ItemPreviewCard.vue';

const favoriteItems = ref<ItemPreviewType[]>([]);
const isLoading = ref(false);
const error = ref<string | null>(null);

// Updated function to load data using the API service
async function loadFavoriteItems() {
  isLoading.value = true;
  error.value = null;
  try {
    // --- CALL THE REAL API SERVICE ---
    // favoriteItems.value = await fetchFavoriteItems();
    // --- END API CALL ---

    console.log("Favorite items fetched from API:", favoriteItems.value);
  } catch (err) {
    console.error("Failed to load favorite items from API:", err);
    error.value = "Could not load your favorite items. Please try again later.";
    favoriteItems.value = [];
  } finally {
    isLoading.value = false;
  }
}

// Handle cases where an image fails to load (remains the same)
function handleImageError(event: Event) {
  const imgElement = event.target as HTMLImageElement;
  imgElement.src = '/placeholder-image.png';
}

// Fetch items when the component is mounted (remains the same)
onMounted(() => {
  loadFavoriteItems();
});
</script>

<template>
  <div class="profile-favorites-view">
    <h2>{{ $t('MY_FAVORITES') }}</h2>

    <div v-if="isLoading" class="loading-indicator">
      <p>Loading your favorite items...</p>
    </div>

    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
    </div>

    <div v-else-if="favoriteItems.length > 0" class="items-grid">
      <div v-for="item in favoriteItems" :key="item.id" class="item-preview-card">
        <img
          :src="item.imageUrl || '/placeholder-image.png'"
          :alt="item.title"
          class="item-image"
          @error="handleImageError"
        >
        <div class="item-details">
          <h3>{{ item.title }}</h3>
          <p class="item-price">Price: {{ item.price }} kr</p>
        </div>
      </div>
    </div>

    <div v-else class="no-items-message">
      <p>You haven't added any items to your favorites yet.</p>
    </div>

  </div>
</template>

<style scoped>
/* Reuse styles similar to ProfileAdsView or create specific ones */
.profile-favorites-view {
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
  gap: 1.5rem;
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
  height: 180px;
  object-fit: cover;
  display: block;
  background-color: #eee;
}

.item-details {
  padding: 1rem;
}

.item-details h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.1em;
  color: var(--color-heading);
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
