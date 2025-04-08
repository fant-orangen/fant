<template>
  <div class="item-list-container">
    <div v-if="isLoadingAllItems" class="loading-indicator">
      <p>Loading initial item data...</p>
    </div>

    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
    </div>

    <div v-else>
      <div v-if="paginatedItems.length > 0" class="items-container">
        <ItemPreview v-for="item in paginatedItems" :key="item.id" :item="item" />
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
import { ref, onMounted, watch, computed } from 'vue';
import ItemPreview from '@/components/item/ItemPreview.vue';
import { fetchPagedPreviewItems } from '@/services/ItemService'; // [cite: uploaded:frontend 5/frontend/src/services/ItemService.ts]
import type { ItemPreviewType } from '@/models/Item'; // [cite: uploaded:frontend 5/frontend/src/models/Item.ts]

const props = defineProps<{
  // Filters
  categoryId: string | null;
  searchTerm?: string | null;
  latitude?: number | null;
  longitude?: number | null;
  maxDistance?: number | null; // in km
  minPrice?: number | null; // Added min price prop
  maxPrice?: number | null;
  sortOption?: string; // ('default', 'price_asc', 'price_desc')

  // Pagination & Display
  pageSize: number;
  emptyMessage?: string;
  paginationEnabled?: boolean;
}>();

// --- State ---
const allItems = ref<ItemPreviewType[]>([]);
const currentPage = ref(1);
const isLoadingAllItems = ref(true);
const error = ref<string | null>(null);

const effectiveEmptyMessage = computed(() => props.emptyMessage || 'No items found matching your criteria.');

// --- Haversine Distance Calculation ---
function getDistanceFromLatLonInKm(lat1: number, lon1: number, lat2: number, lon2: number): number { /* ... same as before ... */
  const R = 6371; const dLat = deg2rad(lat2 - lat1); const dLon = deg2rad(lon2 - lon1);
  const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)); return R * c;
}
function deg2rad(deg: number): number { return deg * (Math.PI / 180); }

// --- Combined Filtering Logic ---
const filteredItems = computed(() => {
  let items = allItems.value;

  // 1. Category
  if (props.categoryId) { items = items.filter(item => item.categoryId === props.categoryId); }

  // 2. Search Term (Title only)
  if (props.searchTerm && props.searchTerm.trim() !== '') {
    const lowerSearchTerm = props.searchTerm.toLowerCase();
    items = items.filter(item => item.title.toLowerCase().includes(lowerSearchTerm));
  }

  // 3. Min Price Filter *** Added ***
  if (props.minPrice !== null && props.minPrice >= 0) {
    items = items.filter(item => item.price >= props.minPrice!);
  }

  // 4. Max Price Filter (applied after min price)
  if (props.maxPrice !== null && props.maxPrice >= 0) {
    items = items.filter(item => item.price <= props.maxPrice!);
  }

  // 5. Location
  const hasValidLocation = props.latitude != null && props.longitude != null && props.maxDistance != null && props.maxDistance > 0;
  if (hasValidLocation) {
    items = items.filter(item => {
      if (item.latitude != null && item.longitude != null) {
        const distance = getDistanceFromLatLonInKm(props.latitude!, props.longitude!, item.latitude, item.longitude);
        return distance <= props.maxDistance!;
      } return false;
    });
  }

  return items;
});

// --- Sorting Logic ---
const sortedAndFilteredItems = computed(() => {
  let itemsToSort = [...filteredItems.value];
  switch (props.sortOption) {
    case 'price_asc':
      itemsToSort.sort((a, b) => a.price - b.price);
      break;
    case 'price_desc': // *** Added price_desc sort ***
      itemsToSort.sort((a, b) => b.price - a.price);
      break;
    default:
      // Keep original order from filtering
      break;
  }
  return itemsToSort;
});


// --- Pagination Logic (Based on Sorted & Filtered Items) ---
const totalPages = computed(() => {
  if (!sortedAndFilteredItems.value || sortedAndFilteredItems.value.length === 0) return 1;
  return Math.ceil(sortedAndFilteredItems.value.length / props.pageSize);
});
const paginatedItems = computed(() => {
  const start = (currentPage.value - 1) * props.pageSize;
  const end = start + props.pageSize;
  return sortedAndFilteredItems.value.slice(start, end);
});
function changePage(page: number) {
  const newPage = Math.max(1, Math.min(page, totalPages.value || 1));
  currentPage.value = newPage;
}

// --- Watchers ---
watch(
  [ // *** Watch minPrice as well ***
    () => props.categoryId, () => props.searchTerm,
    () => props.latitude, () => props.longitude, () => props.maxDistance,
    () => props.minPrice, () => props.maxPrice, () => props.sortOption
  ],
  () => {
    console.log('Filters or sort changed, resetting page.');
    if (currentPage.value !== 1) { currentPage.value = 1; }
  },
  { deep: true }
);

// --- Initial Data Load ---
async function fetchInitialBatch() {
  isLoadingAllItems.value = true; error.value = null;
  try {
    const response = await fetchPagedPreviewItems(0, 200);
    allItems.value = response.content ?? [];
    console.log(`Workspaceed initial batch of ${allItems.value.length} items.`);
  } catch (err) {
    console.error("Error fetching initial item batch:", err);
    error.value = 'Failed to load initial item data.'; allItems.value = [];
  } finally { isLoadingAllItems.value = false; }
}
onMounted(fetchInitialBatch);

</script>

<style scoped>
/* Styles remain the same */
.item-list-container { width: 100%; }
.loading-indicator, .error-message, .no-items-message { text-align: center; margin: 2rem 0; color: var(--color-text); }
.items-container { display: grid; grid-template-columns: repeat(4, 1fr); gap: 1.5rem; margin-top: 1.5rem; }
@media (max-width: 1200px) { .items-container { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 900px) { .items-container { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 600px) { .items-container { grid-template-columns: repeat(2, 1fr); gap: 1rem; } }
.pagination-controls { display: flex; justify-content: center; align-items: center; margin-top: 2rem; padding-bottom: 1rem; gap: 1rem; }
.pagination-button { padding: 0.5rem 1rem; background-color: var(--color-background-soft); border: 1px solid var(--color-border); border-radius: 4px; cursor: pointer; transition: background-color 0.2s ease; }
.pagination-button:hover:not(:disabled) { background-color: var(--color-background-mute); }
.pagination-button:disabled { opacity: 0.5; cursor: not-allowed; }
.page-info { font-size: 0.9rem; color: var(--color-text); }
:root { --color-text: #333; --color-background-soft: #f9f9f9; --color-background-mute: #f0f0f0; --color-border: #ddd; }
</style>
