<template>
  <div class="profile-favorites-view">
    <h2>{{ $t('MY_FAVORITES') }}</h2>
    <ItemList
      :items="items"
      :isLoading="isLoading"
      :error="error"
      :currentPage="currentPage"
      :totalPages="totalPages"
      :categoryId="null"
      :emptyMessage="$t('FAVORITES_EMPTY_MESSAGE')"
      :paginationEnabled="true"
      @change-page="handlePageChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'; // Import ref and onMounted
import { fetchPagedFavoriteItems } from '@/services/ItemService';
import type { ItemPreviewType, PaginatedItemPreviewResponse } from '@/models/Item';
import ItemList from '@/components/item/ItemList.vue';

// --- State Management within FavoritesView ---
const items = ref<ItemPreviewType[]>([]);
const isLoading = ref(false);
const error = ref<string | null>(null);
const currentPage = ref(1); // Use 1-based indexing for UI display if needed, but API uses 0-based
const totalPages = ref(1);
const pageSize = 5; // Define page size

// --- Data Fetching Function ---
async function loadFavorites(page: number) {
  isLoading.value = true;
  error.value = null;
  try {
    // API expects 0-based page index
    const apiPage = page - 1;
    const response: PaginatedItemPreviewResponse = await fetchPagedFavoriteItems(apiPage, pageSize, 'createdAt,desc');
    items.value = response.content;
    totalPages.value = response.totalPages;
    currentPage.value = response.number + 1; // Update current page based on response (API is 0-based)
  } catch (err) {
    console.error("Failed to load favorite items:", err);
    error.value = 'Failed to load favorites. Please try again.';
    items.value = []; // Clear items on error
    totalPages.value = 1;
    currentPage.value = 1;
  } finally {
    isLoading.value = false;
  }
}

// --- Event Handler for Pagination ---
function handlePageChange(newPage: number) {
  if (newPage !== currentPage.value) {
    loadFavorites(newPage);
  }
}

// --- Load initial data when component mounts ---
onMounted(() => {
  loadFavorites(currentPage.value);
});
</script>
