<template>
  <div class="item-list-container">
    <div v-if="loading && items.length === 0" class="loading-indicator">
      <p>Loading items...</p>
    </div>

    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
    </div>

    <div v-else-if="items.length > 0" class="items-container">
      <ItemPreview v-for="item in items" :key="item.id" :item="item" />
    </div>

    <div v-else class="no-items-message">
      <p>{{ emptyMessage }}</p>
    </div>

    <!-- Pagination controls -->
    <div v-if="items.length > 0 && totalPages > 1" class="pagination-controls">
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
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import ItemPreview from '@/components/item/ItemPreview.vue';
import {
  fetchPagedPreviewItems,
  fetchPagedPreviewItemsByCategory
} from '@/services/ItemService';
import type { ItemPreviewType, PaginatedItemPreviewResponse } from '@/models/Item';

const props = defineProps<{
  categoryId: string | null;
  pageSize: number;
  emptyMessage?: string;
}>();

const items = ref<ItemPreviewType[]>([]);
const currentPage = ref(1);
const totalPages = ref(0);
const totalItems = ref(0);
const loading = ref(true);
const error = ref<string | null>(null);

async function loadItems() {
  loading.value = true;
  error.value = null;

  try {
    let response: PaginatedItemPreviewResponse;

    if (props.categoryId) {
      response = await fetchPagedPreviewItemsByCategory(props.categoryId, currentPage.value - 1, props.pageSize);
    } else {
      response = await fetchPagedPreviewItems(currentPage.value - 1, props.pageSize);
    }

    items.value = response.content;
    totalItems.value = response.totalElements;
    totalPages.value = response.totalPages;
  } catch (err) {
    error.value = 'Failed to load items.';
    console.error(err);
  } finally {
    loading.value = false;
  }
}

function changePage(page: number) {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
}

watch(currentPage, loadItems);
watch(() => props.categoryId, () => {
  currentPage.value = 1;
  loadItems();
});

onMounted(loadItems);
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
}
.items-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
  margin-top: 1.5rem;
}
@media (max-width: 1200px) {
  .items-container {
    grid-template-columns: repeat(3, 1fr);
  }
}
@media (max-width: 900px) {
  .items-container {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 600px) {
  .items-container {
    grid-template-columns: 1fr;
  }
}
.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2rem;
  gap: 1rem;
}
.pagination-button {
  padding: 0.5rem 1rem;
  background-color: var(--color-background-soft);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  cursor: pointer;
}
.pagination-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.page-info {
  font-size: 0.9rem;
}
</style>
