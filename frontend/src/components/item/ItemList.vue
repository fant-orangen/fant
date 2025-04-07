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

    <!-- Pagination controls - only show if pagination is enabled -->
    <div v-if="paginationEnabled && items.length > 0 && totalPages > 1" class="pagination-controls">
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

    <div v-if="loading && items.length > 0" class="loading-more">
      Loading more items...
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue';
import ItemPreview from '@/components/item/ItemPreview.vue';
import type { ItemPreviewType } from '@/models/Item';

const props = defineProps({
  fetchFunction: {
    type: Function,
    required: true
  },
  fetchParams: {
    type: Array,
    default: () => []
  },
  emptyMessage: {
    type: String,
    default: 'No items available'
  },
  pageSize: {
    type: Number,
    default: 10
  },
  paginationEnabled: {
    type: Boolean,
    default: true
  }
});

const items = ref<ItemPreviewType[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);
const currentPage = ref(1);
const totalItems = ref(0);
const totalPages = computed(() =>
  Math.ceil(totalItems.value / props.pageSize)
);

async function loadItems() {
  loading.value = true;
  error.value = null;

  try {
    let response;

    if (props.paginationEnabled) {
      // Append pagination parameters to the fetch params
      const paginatedParams = [...props.fetchParams, currentPage.value, props.pageSize];
      response = await props.fetchFunction(...paginatedParams);
    } else {
      // Call without pagination parameters
      response = await props.fetchFunction(...props.fetchParams);
    }

    // Handle different response structures
    if (response.items && response.totalItems !== undefined) {
      // Paginated response structure
      items.value = response.items;
      totalItems.value = response.totalItems;
    } else if (Array.isArray(response)) {
      // Array response (non-paginated)
      items.value = response;
      totalItems.value = response.length;
    } else {
      items.value = [];
      error.value = "Invalid response format";
    }
  } catch (err) {
    console.error("Failed to load items:", err);
    error.value = "Could not load items. Please try again later.";
    items.value = [];
  } finally {
    loading.value = false;
  }
}

function changePage(newPage: number) {
  if (newPage >= 1 && newPage <= totalPages.value) {
    currentPage.value = newPage;
    loadItems();
  }
}

onMounted(loadItems);
watch(() => props.fetchParams, loadItems, { deep: true });
</script>

<style scoped>
.item-list-container {
  width: 100%;
}

.loading-indicator,
.error-message,
.no-items-message,
.loading-more {
  text-align: center;
  margin: 2rem 0;
  color: var(--color-text);
}

.error-message p {
  color: red;
  font-weight: bold;
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
