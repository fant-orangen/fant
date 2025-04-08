<template>
  <div class="item-list-container">
    <div v-if="isLoading" class="loading-indicator">
      <p>Loading items...</p>
    </div>

    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
    </div>

    <div v-else>
      <div v-if="items && items.length > 0" class="items-container">
        <ItemPreview v-for="item in items" :key="item.id" :item="item" />
      </div>
      <div v-else class="no-items-message">
        <p>{{ effectiveEmptyMessage }}</p>
      </div>
      <div v-if="paginationEnabled && totalPages > 1" class="pagination-controls">
        <button :disabled="currentPage <= 1" @click="changePage(currentPage - 1)" class="pagination-button">Previous</button>
        <span class="page-info">Page {{ currentPage }} of {{ totalPages }}</span>
        <button :disabled="currentPage >= totalPages" @click="changePage(currentPage + 1)" class="pagination-button">Next</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import ItemPreview from '@/components/item/ItemPreview.vue'; // Assuming this path is correct
import type { ItemPreviewType } from '@/models/Item'; // [cite: uploaded:frontend 6/frontend/src/models/Item.ts]

// --- Props received from parent (HomeView) ---
const props = defineProps<{
  items: ItemPreviewType[] | null; // Array of items fetched by parent
  isLoading: boolean;
  error: string | null;
  currentPage: number;
  totalPages: number;
  // pageSize: number; // pageSize is managed by HomeView now for the API call
  emptyMessage?: string;
  paginationEnabled?: boolean;
}>();

// --- Emit event for page changes ---
const emit = defineEmits<{
  (e: 'change-page', page: number): void
}>();

// --- Computed property for empty message ---
const effectiveEmptyMessage = computed(() => props.emptyMessage || 'No items found.');

// --- Methods ---
function changePage(page: number) {
  // Emit the requested page number to the parent
  if (page >= 1 && page <= props.totalPages) {
    emit('change-page', page);
  }
}

// --- Removed ---
// - Internal state like allItems, currentPage, isLoadingAllItems, error (now props)
// - Computed properties for filtering/sorting/pagination (now done in backend/HomeView)
// - Watchers for filters
// - onMounted data fetching (now done in HomeView)
// - Haversine distance functions (now handled by backend)

</script>

<style scoped>
/* Styles remain the same */
.item-list-container { width: 100%; }
.loading-indicator, .error-message, .no-items-message { text-align: center; margin: 2rem 0; color: var(--color-text); }
.items-container { display: grid; grid-template-columns: repeat(4, 1fr); gap: 1.5rem; margin-top: 1.5rem; }
@media (max-width: 1200px) { .items-container { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 900px) { .items-container { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 600px) { .items-container { grid-template-columns: repeat(2, 1fr); gap: 1rem; } } /* Adjusted for potentially smaller screens */
.pagination-controls { display: flex; justify-content: center; align-items: center; margin-top: 2rem; padding-bottom: 1rem; gap: 1rem; }
.pagination-button { padding: 0.5rem 1rem; background-color: var(--color-background-soft); border: 1px solid var(--color-border); border-radius: 4px; cursor: pointer; transition: background-color 0.2s ease; }
.pagination-button:hover:not(:disabled) { background-color: var(--color-background-mute); }
.pagination-button:disabled { opacity: 0.5; cursor: not-allowed; }
.page-info { font-size: 0.9rem; color: var(--color-text); }
/* Ensure these CSS variables are defined globally or adjust as needed */
/* :root { --color-text: #333; --color-background-soft: #f9f9f9; --color-background-mute: #f0f0f0; --color-border: #ddd; } */
</style>
