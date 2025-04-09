<template>
  <section class="homepage">

    <div class="search-section">
      <SearchBar
        @update:searchTerm="onSearchTermUpdate"
        @update:maxDistance="onMaxDistanceUpdate"
        @request-current-location="fetchCurrentUserLocation"
        @update:sortOption="onSortOptionUpdate"
        @update:maxPrice="onMaxPriceUpdate"
        @update:minPrice="onMinPriceUpdate"
        :is-location-available="isLocationAvailable" />
      <p v-if="locationError" class="location-error">{{ locationError }}</p>
      <p v-if="isFetchingLocation" class="location-info">Getting your location...</p>
    </div>

    <div class="category-section">
      <CategoryGrid
        layout="horizontal"
        @select="onCategoryClick"
        :selectedCategoryId="selectedCategoryId"
      />
      <button v-if="selectedCategoryId" @click="clearCategorySelection" class="clear-category-button">
        Clear Category Filter
      </button>
    </div>

    <div class="items-section">
      <ItemList
        :items="items"
        :is-loading="isLoading"
        :error="error"
        :current-page="currentPage"
        :total-pages="totalPages"
        @change-page="onPageChange"
        emptyMessage="No items found matching your criteria. Try adjusting filters."
        :paginationEnabled="true" />
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import CategoryGrid from '@/components/category/CategoryGrid.vue'
import ItemList from '@/components/item/ItemList.vue'
import SearchBar from '@/components/search/searchBar.vue'
import {
  searchItems,
  type ItemSearchParams,
  fetchItemsByDistribution
} from '@/services/ItemService'
import type { ItemPreviewType } from '@/models/Item'
import { fetchCategories } from '@/services/CategoryService';
import type { Category } from '@/models/Category';
import {
  fetchCategoryRecommendations,
  fetchUserViewCount
} from '@/services/RecommendationService.ts'
import { useUserStore } from '@/stores/UserStore.ts'


// --- Filter States ---
const selectedCategoryId = ref<string | null>(null)
const searchTerm = ref<string>('')
const maxDistance = ref<number | null>(50) // Default distance
const currentLatitude = ref<number | null>(null)
const currentLongitude = ref<number | null>(null)
const sortOption = ref<string>('default')
const minPrice = ref<number | null>(null)
const maxPrice = ref<number | null>(null)
const categories = ref<Category[]>([]);

// --- Data & UI States ---
const items = ref<ItemPreviewType[]>([]);
const currentPage = ref(1);
const totalPages = ref(1);
const isLoading = ref(false);
const error = ref<string | null>(null);
const isFetchingLocation = ref(false);
const insufficientItemViews = ref(false);
const locationError = ref<string | null>(null);
const pageSize = ref(8);

const numOfViewsLimit = 3

// --- Computed Properties ---
const isLocationAvailable = computed(() => {
  return currentLatitude.value !== null && currentLongitude.value !== null;
});

const selectedCategoryName = computed(() => {
  if (!selectedCategoryId.value) return null;
  const foundCategory = categories.value.find(cat => cat.id?.toString() === selectedCategoryId.value);
  return foundCategory ? foundCategory.name : null;
});

const backendSortParam = computed(() => {
  switch (sortOption.value) {
    case 'price_asc': return 'price,asc';
    case 'price_desc': return 'price,desc';
    default: return null;
  }
});




// --- Methods ---
async function fetchItems() {
  isLoading.value = true;
  error.value = null;
  console.log(`Workspacing items for page: ${currentPage.value}`);

  const params: ItemSearchParams = {
    searchTerm: searchTerm.value || null,
    minPrice: minPrice.value,
    maxPrice: maxPrice.value,
    status: 'ACTIVE',
    categoryName: selectedCategoryName.value,
    userLatitude: currentLatitude.value, // Pass directly
    userLongitude: currentLongitude.value, // Pass directly
    // Only send distance if location coords are available
    maxDistance: isLocationAvailable.value ? maxDistance.value : null,
    page: currentPage.value - 1,
    size: pageSize.value,
    sort: backendSortParam.value ?? undefined
  };

  console.log("fetchItems was called");
  try {
    console.log("Category ID:", selectedCategoryId.value);
    console.log("User ID:", useUserStore().getUserId);

    const isRecommendationSelected = selectedCategoryId.value === '-1';

    if (useUserStore().isLoggedInUser) {
      if (isRecommendationSelected) {
        const numOfViews = await fetchUserViewCount();
        console.log("User view count:", numOfViews);

        if (numOfViews > numOfViewsLimit) {
          console.log("Fetching recommendations...");
          const recommendations = await fetchCategoryRecommendations();
          const response = await fetchItemsByDistribution(recommendations);
          console.log("Fetched recommended items:", response.content);

          items.value = response.content ?? [];
          totalPages.value = response.totalPages ?? 1;

          if (currentPage.value > totalPages.value) {
            currentPage.value = totalPages.value > 0 ? totalPages.value : 1;
          }
        } else {
          items.value = [];
          totalPages.value = 1;
          error.value = "You have insufficient item views to fetch recommendations.";
          insufficientItemViews.value = true;
        }
      } else {
        // Logged in and not viewing recommendations — search as normal
        const response = await searchItems(params);
        items.value = response.content ?? [];
        totalPages.value = response.totalPages ?? 1;

        if (currentPage.value > totalPages.value) {
          currentPage.value = totalPages.value > 0 ? totalPages.value : 1;
        }
      }
    } else {
      if (isRecommendationSelected) {
        // Not logged in but selected recommended
        items.value = [];
        totalPages.value = 1;
        error.value = "You must be logged in to view recommended items.";
      } else {
        // Not logged in, normal category
        const response = await searchItems(params);
        items.value = response.content ?? [];
        totalPages.value = response.totalPages ?? 1;

        if (currentPage.value > totalPages.value) {
          currentPage.value = totalPages.value > 0 ? totalPages.value : 1;
        }
      }
    }

    console.log(`Fetched ${items.value.length} items. Total pages: ${totalPages.value}`);
  } catch (err) {
    console.error("Error fetching items:", err);
    error.value = 'Failed to load items. Please try again later.';
    items.value = [];
    totalPages.value = 1;
  } finally {
    isLoading.value = false;
  }
}

// --- Event Handlers ---
function onCategoryClick(categoryId: string) {
  selectedCategoryId.value = selectedCategoryId.value === categoryId ? null : categoryId;
  // Don't reset page here, watcher will handle it
}

function clearCategorySelection() {
  selectedCategoryId.value = null;
  // Don't reset page here, watcher will handle it
}

function onSearchTermUpdate(newSearchTerm: string) {
  searchTerm.value = newSearchTerm;
  // Don't reset page here, watcher will handle it
}

function onMaxDistanceUpdate(newMaxDistance: number | null) {
  maxDistance.value = newMaxDistance;
  // Don't reset page here, watcher will handle it if location is available
}

function onSortOptionUpdate(newSortOption: string) {
  sortOption.value = newSortOption;
  // Don't reset page here, watcher will handle it
}

function onMinPriceUpdate(newMinPrice: number | null) {
  minPrice.value = newMinPrice;
  // Don't reset page here, watcher will handle it
}

function onMaxPriceUpdate(newMaxPrice: number | null) {
  maxPrice.value = newMaxPrice;
  // Don't reset page here, watcher will handle it
}

function onPageChange(newPage: number) {
  if (newPage >= 1 && newPage <= totalPages.value) {
    currentPage.value = newPage;
    // fetchItems(); // Watcher handles this
  }
}

function fetchCurrentUserLocation() {
  if (!navigator.geolocation) {
    locationError.value = "Geolocation is not supported by your browser.";
    isFetchingLocation.value = false;
    return;
  }
  isFetchingLocation.value = true;
  locationError.value = null;
  navigator.geolocation.getCurrentPosition(
    (position) => {
      currentLatitude.value = position.coords.latitude;
      currentLongitude.value = position.coords.longitude;
      console.log(`Location obtained: Lat ${currentLatitude.value}, Lon ${currentLongitude.value}`);
      locationError.value = null;
      isFetchingLocation.value = false;
      // Don't need to reset page or call fetchItems here, watcher will detect coordinate changes
    },
    (error) => {
      console.error("Error getting location: ", error);
      currentLatitude.value = null; // Reset location on error
      currentLongitude.value = null;
      switch(error.code) {
        case error.PERMISSION_DENIED: locationError.value = "Permission denied."; break;
        case error.POSITION_UNAVAILABLE: locationError.value = "Location unavailable."; break;
        case error.TIMEOUT: locationError.value = "Location request timed out."; break;
        default: locationError.value = "Error fetching location."; break;
      }
      isFetchingLocation.value = false;
      // Trigger fetch to update list without location filter if needed
      // fetchItems(); // Or rely on watcher detecting null coordinates
    }
  );
}

// --- Watchers to trigger fetchItems ---
watch(
  [
    searchTerm,
    minPrice,
    maxPrice,
    selectedCategoryId, // Watch computed name derWived from ID
    sortOption,
    // Watch location refs directly
    currentLatitude,
    currentLongitude,
    maxDistance,
    // Keep watching currentPage last
    currentPage,
  ],
  (newValues, oldValues) => {
    // Identify if the change was ONLY the currentPage
    const pageIndex = newValues.length - 1; // Index of currentPage
    let pageChangedOnly = true;
    for (let i = 0; i < pageIndex; i++) {
      if (newValues[i] !== oldValues[i]) {
        pageChangedOnly = false;
        break;
      }
    }

    // If a filter (not page) changed, reset to page 1
    if (!pageChangedOnly && currentPage.value !== 1) {
      console.log("Filters changed, resetting page to 1.");
      // This will trigger the watcher again, but only fetchItems once when page becomes 1
      currentPage.value = 1;
    } else {
      // If only page changed, or if filters changed and page is already 1
      console.log("Watcher triggered, fetching items.");
      fetchItems();
    }
  },
  { deep: true } // deep might be needed if watching nested objects, but check performance
);


// --- Initial Data Load ---
async function loadInitialData() {
  isLoading.value = true;

  try {
    categories.value = await fetchCategories(); // [cite: uploaded:frontend 6/frontend/src/services/CategoryService.ts]
    await fetchItems();
  } catch (err) {
    console.error("Error loading initial data:", err);
    error.value = "Failed to load initial page data.";
  } finally {
    isLoading.value = false;
  }
}

onMounted(loadInitialData);

</script>

<style scoped>
@import '@/assets/styles/responsiveStyles.css'; /* [cite: uploaded:frontend 6/frontend/src/assets/styles/responsiveStyles.css] */
/* Styles remain the same as previous version */
.homepage {
  /* Add any specific homepage layout styles if needed */
}
.search-section {
  padding: 1rem 0;
  margin-bottom: 1rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}
.category-section {
  margin-bottom: 1.5rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}
.items-section {
  /* Styles for the item listing area */
}
.location-error {
  color: red;
  font-size: 0.9em;
  text-align: center;
  max-width: 80%;
}
.location-info {
  color: #555;
  font-size: 0.9em;
  text-align: center;
}
.recommendation-info {
  color: #555;
  font-size: 0.9em;
  text-align: center;
}

.clear-category-button {
  padding: 0.5rem 1rem;
  margin-top: 0.5rem;
  background-color: var(--color-background-mute);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s ease;
}
.clear-category-button:hover {
  background-color: var(--color-border);
}
</style>
