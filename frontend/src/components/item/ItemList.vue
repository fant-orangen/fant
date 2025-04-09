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
  </div>
</template>

<script setup lang="ts">
import { withDefaults, defineProps, computed } from 'vue';
import ItemPreview from '@/components/item/ItemPreview.vue';
import type { ItemPreviewType, PaginatedItemPreviewResponse } from '@/models/Item';

// --- Define Props Interface including optional properties ---
interface Props {
  // Data properties – made optional with defaults
  items?: ItemPreviewType[] | null;
  isLoading?: boolean;
  error?: string | null;
  currentPage?: number;
  totalPages?: number;

  // UI and configuration props
  emptyMessage?: string;
  paginationEnabled?: boolean;
  // Optionally, a function to fetch items
  fetchFunction?: (page: number, size: number, sort?: string) => Promise<PaginatedItemPreviewResponse>;
}

// --- Provide default values via withDefaults() ---
const props = withDefaults(defineProps<Props>(), {
  items: null,
  isLoading: false,
  error: null,
  currentPage: 1,
  totalPages: 1,
  emptyMessage: 'No items found.',
  paginationEnabled: true,
  fetchFunction: undefined
});

// --- Emit event for page changes ---
const emit = defineEmits<{
  (e: 'change-page', page: number): void
}>();

// --- Computed property for effective empty message ---
const effectiveEmptyMessage = computed(() => props.emptyMessage || 'No items found.');

// --- Method for handling page changes ---
function changePage(page: number) {
  if (page >= 1 && page <= props.totalPages!) {
    emit('change-page', page);
  }
}
</script>

<style scoped>
.item-list-container {
  width: 100%;
}
.loading-indicator,
.error-message,
.no-items-message {
  text-align: center;
  margin: 2rem 0;
  color: var(--color-text);
}
.items-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
  margin-top: 1.5rem;
}
@media (max-width: 1200px) {
  .items-container { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 900px) {
  .items-container { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 600px) {
  .items-container { grid-template-columns: repeat(2, 1fr); gap: 1rem; }
}
.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2rem;
  padding-bottom: 1rem;
  gap: 1rem;
}
.pagination-button {
  padding: 0.5rem 1rem;
  background-color: var(--color-background-soft);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}
.pagination-button:hover:not(:disabled) {
  background-color: var(--color-background-mute);
}
.pagination-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.page-info {
  font-size: 0.9rem;
  color: var(--color-text);
}
</style>
