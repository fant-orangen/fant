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
import { ref, onMounted, watch } from 'vue'
import ItemPreview from '@/components/item/ItemPreview.vue' // Ensure ItemPreview is correctly imported
import { fetchPagedPreviewItems, fetchPagedPreviewItemsByCategory } from '@/services/ItemService'
import type { ItemPreviewType, PaginatedItemPreviewResponse } from '@/models/Item'

const props = defineProps<{
  categoryId: string | null
  pageSize: number
  emptyMessage?: string
  paginationEnabled?: boolean // Added prop to explicitly enable/disable pagination controls if needed
  // Removed fetchFunction prop as it wasn't used in HomeView and complicates standard usage
}>()

const items = ref<ItemPreviewType[]>([])
const currentPage = ref(1)
const totalPages = ref(0)
const totalItems = ref(0)
const loading = ref(true)
const error = ref<string | null>(null)

async function loadItems() {
  loading.value = true
  error.value = null

  try {
    let response: PaginatedItemPreviewResponse

    // Use categoryId prop to determine which API function to call
    if (props.categoryId) {
      response = await fetchPagedPreviewItemsByCategory(
        props.categoryId,
        currentPage.value - 1, // API uses 0-based index
        props.pageSize,
      )
    } else {
      // No categoryId means fetch all items
      response = await fetchPagedPreviewItems(
        currentPage.value - 1, // API uses 0-based index
        props.pageSize
      )
    }

    items.value = response.content
    totalItems.value = response.totalElements
    totalPages.value = response.totalPages

    // Handle cases where totalPages might be 0 from the API if no items exist
    if (response.totalPages === 0 && response.totalElements === 0) {
      totalPages.value = 1; // Treat as 1 page if empty
    } else {
      totalPages.value = response.totalPages;
    }

  } catch (err) {
    error.value = 'Failed to load items.'
    console.error(err)
    items.value = [] // Clear items on error
    totalPages.value = 0
    totalItems.value = 0

  } finally {
    loading.value = false
  }
}

function changePage(page: number) {
  // Ensure page stays within valid bounds (1 to totalPages)
  const newPage = Math.max(1, Math.min(page, totalPages.value || 1));
  if (newPage !== currentPage.value) {
    currentPage.value = newPage;
    // loadItems() will be called by the watcher
  }
}

// Watch for changes in the current page and reload items
watch(currentPage, loadItems)

// Watch for changes in categoryId, reset to page 1, and reload
watch(
  () => props.categoryId,
  (newCategoryId, oldCategoryId) => {
    // Reset to page 1 only if the category actually changes
    if (newCategoryId !== oldCategoryId) {
      currentPage.value = 1;
      // loadItems() is called immediately after page reset if page was not 1,
      // or called here if page was already 1. To avoid double call, only call if page didn't change.
      if (currentPage.value === 1) {
        loadItems();
      }
    }
  },
  { immediate: false } // Don't run immediately on mount, let onMounted handle initial load
)

// Initial load when the component mounts
onMounted(loadItems)
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
  color: var(--color-text); /* Ensure text is visible */
}

.items-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* Default: 4 columns */
  gap: 1.5rem;
  margin-top: 1.5rem;
}

/* Responsive adjustments for grid columns */
@media (max-width: 1200px) {
  .items-container {
    grid-template-columns: repeat(3, 1fr); /* 3 columns */
  }
}

@media (max-width: 900px) {
  .items-container {
    grid-template-columns: repeat(2, 1fr); /* 2 columns */
  }
}

@media (max-width: 600px) {
  .items-container {
    grid-template-columns: repeat(2, 1fr); /* 2 columns for small mobile */
    gap: 1rem; /* Adjust gap for smaller screens */
  }
}

.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2rem;
  padding-bottom: 1rem; /* Add padding at the bottom */
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

/* Add default variables if not globally defined */
:root {
  --color-text: #333;
  --color-background-soft: #f9f9f9;
  --color-background-mute: #f0f0f0;
  --color-border: #ddd;
}
</style>
