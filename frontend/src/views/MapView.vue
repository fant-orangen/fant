<template>
  <div class="map-view-container">
    <div class="search-controls-map">
      <SearchBar
        @update:searchTerm="onSearchTermUpdate"
        @update:maxDistance="onMaxDistanceUpdate"
        @request-current-location="fetchCurrentUserLocation"
        @update:sortOption="onSortOptionUpdate"
        @update:maxPrice="onMaxPriceUpdate"
        @update:minPrice="onMinPriceUpdate"
        :is-location-available="isLocationAvailable"
        :is-circle-search-active="isCircleSearchActive" />
      <button v-if="isCircleSearchActive" @click="clearDrawnSearchArea" class="clear-drawn-area-button">
        Clear Search Area
      </button>
      <p v-if="locationError" class="location-error">{{ locationError }}</p>
      <p v-if="isFetchingLocation" class="location-info">Getting your location...</p>
    </div>

    <div class="map-content-area">
      <div class="category-sidebar-desktop">
        <h3 class="sidebar-title">{{ t('CATEGORIES') }}</h3>
        <button v-if="selectedCategoryId" @click="clearCategorySelection" class="clear-category-button">
          Clear Category
        </button>
        <div class="scrollable-categories">
          <CategoryGrid
            @select="onCategoryClick"
            layout="vertical"
            :selectedCategoryId="selectedCategoryId"
            :noRecommendations="true"
          />
        </div>
      </div>

      <div class="map-wrapper">
        <MapComponent
          :categoryId="selectedCategoryId"
          :searchTerm="searchTerm"
          :minPrice="minPrice"
          :maxPrice="maxPrice"
          :latitude="searchLatitude"
          :longitude="searchLongitude"
          :maxDistance="searchRadiusKm"
          :sortOption="sortOption"
          :clearDrawnAreaTrigger="clearDrawnAreaTrigger"
          @update-search-area="onUpdateSearchArea"
          height="100%"
        />
      </div>
    </div>

    <div class="category-bar-mobile">
      <button v-if="selectedCategoryId" @click="clearCategorySelection" class="clear-category-button-mobile">
        X
      </button>
      <CategoryGrid
        @select="onCategoryClick"
        layout="horizontal"
        :selectedCategoryId="selectedCategoryId"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n';
import MapComponent from '@/components/map/MapComponent.vue'
import CategoryGrid from '@/components/category/CategoryGrid.vue'
import SearchBar from '@/components/search/searchBar.vue'
import { fetchCategories } from '@/services/CategoryService'; // [cite: uploaded:frontend 6/frontend/src/services/CategoryService.ts]
import type { Category } from '@/models/Category'; // [cite: uploaded:frontend 6/frontend/src/models/Category.ts]

const { t } = useI18n();

// --- Filter States ---
const selectedCategoryId = ref<string | null>(null)
const searchTerm = ref<string>('')
const sortOption = ref<string>('default')
const minPrice = ref<number | null>(null)
const maxPrice = ref<number | null>(null)
const categories = ref<Category[]>([]);

// --- Location State (Geolocation / Slider) ---
const maxDistance = ref<number | null>(50) // Default distance from slider
const currentLatitude = ref<number | null>(null) // User's geolocation lat
const currentLongitude = ref<number | null>(null) // User's geolocation lng
const isFetchingLocation = ref(false);
const locationError = ref<string | null>(null);

// --- Drawn Circle State ---
const drawnLatitude = ref<number | null>(null);
const drawnLongitude = ref<number | null>(null);
const drawnRadiusKm = ref<number | null>(null);
const clearDrawnAreaTrigger = ref(0); // Increment to trigger clear in MapComponent

// --- Computed properties ---

// Is Geolocation available?
const isLocationAvailable = computed(() => {
  return currentLatitude.value !== null && currentLongitude.value !== null;
});

// Is a drawn circle currently active?
const isCircleSearchActive = computed(() => {
  return drawnLatitude.value !== null && drawnLongitude.value !== null && drawnRadiusKm.value !== null;
});

// Determine the final search coordinates/radius to pass to MapComponent
const searchLatitude = computed(() => {
  return isCircleSearchActive.value ? drawnLatitude.value : currentLatitude.value;
});
const searchLongitude = computed(() => {
  return isCircleSearchActive.value ? drawnLongitude.value : currentLongitude.value;
});
const searchRadiusKm = computed(() => {
  // Use drawn radius if circle is active, otherwise use slider distance ONLY if geolocation is also active
  if (isCircleSearchActive.value) {
    return drawnRadiusKm.value;
  } else if (isLocationAvailable.value) {
    return maxDistance.value;
  } else {
    return null; // No distance search if neither is active
  }
});


// --- Event Handlers ---
function onCategoryClick(categoryId: string) {
  selectedCategoryId.value = categoryId === selectedCategoryId.value ? null : categoryId;
}

function clearCategorySelection() {
  selectedCategoryId.value = null;
}

function onSearchTermUpdate(newSearchTerm: string) {
  searchTerm.value = newSearchTerm;
}

function onMaxDistanceUpdate(newMaxDistance: number | null) {
  // Update the slider distance state. The computed searchRadiusKm will handle
  // whether this value or the drawn radius is used.
  maxDistance.value = newMaxDistance;
  // If a circle is drawn, changing the slider shouldn't clear it.
  // If no circle is drawn, changing the slider WILL trigger a map update via the watcher in MapComponent
  // because the `searchRadiusKm` computed property will change.
}


function onSortOptionUpdate(newSortOption: string) {
  sortOption.value = newSortOption;
}

function onMinPriceUpdate(newMinPrice: number | null) {
  minPrice.value = newMinPrice;
}

function onMaxPriceUpdate(newMaxPrice: number | null) {
  maxPrice.value = newMaxPrice;
}

// Handler for when a circle is drawn/edited in MapComponent
function onUpdateSearchArea(payload: { latitude: number; longitude: number; radiusKm: number }) {
  console.log("MapView: Received update-search-area", payload);
  drawnLatitude.value = payload.latitude;
  drawnLongitude.value = payload.longitude;
  drawnRadiusKm.value = payload.radiusKm;
  // Optionally clear geolocation to avoid confusion, though computed props handle precedence
  // currentLatitude.value = null;
  // currentLongitude.value = null;
  locationError.value = null; // Clear any location fetching errors
}

// Method to clear the drawn search area
function clearDrawnSearchArea() {
  console.log("MapView: Clearing drawn search area.");
  drawnLatitude.value = null;
  drawnLongitude.value = null;
  drawnRadiusKm.value = null;
  clearDrawnAreaTrigger.value++; // Increment trigger to signal MapComponent
  // The computed properties will automatically revert to using geolocation/slider values
}

// --- Location Fetching ---
function fetchCurrentUserLocation() {
  // Clear drawn area if user explicitly asks for their location
  clearDrawnSearchArea();

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
      console.log(`Location obtained for map: Lat ${currentLatitude.value}, Lon ${currentLongitude.value}`);
      locationError.value = null;
      isFetchingLocation.value = false;
      // Computed properties will update, triggering MapComponent watcher
    },
    (error) => {
      console.error("Error getting location for map: ", error);
      currentLatitude.value = null;
      currentLongitude.value = null;
      switch(error.code) {
        case error.PERMISSION_DENIED: locationError.value = "Location permission denied."; break;
        case error.POSITION_UNAVAILABLE: locationError.value = "Location unavailable."; break;
        case error.TIMEOUT: locationError.value = "Location request timed out."; break;
        default: locationError.value = "Error fetching location."; break;
      }
      isFetchingLocation.value = false;
      // Computed properties will update, triggering MapComponent watcher
    }
  );
}

// --- Lifecycle ---
onMounted(async () => {
  try {
    categories.value = await fetchCategories(); // [cite: uploaded:frontend 6/frontend/src/services/CategoryService.ts]
  } catch (err) {
    console.error("Failed to fetch categories for MapView:", err);
  }
});

</script>

<style scoped>
/* --- Styles from previous version --- */
/* --- Main Layout --- */
.map-view-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px);
  max-width: 1800px;
  margin: 0 auto;
  gap: 1rem;
  box-sizing: border-box;
  background-color: var(--color-background, #fff);
}

.search-controls-map {
  padding: 0.5rem 1rem;
  border-bottom: 1px solid var(--color-border, #e0e0e0);
  flex-shrink: 0;
  background-color: var(--color-background, #fff);
  position: sticky;
  top: 0;
  z-index: 1001;
  /* Add flex to layout button next to search bar if needed */
  display: flex;
  flex-wrap: wrap; /* Allow wrapping if space is tight */
  align-items: flex-start; /* Align items to the top */
  gap: 1rem; /* Space between search bar and button */
}
/* Make SearchBar take most space */
.search-controls-map > :deep(.search-controls-container) {
  flex-grow: 1;
}

.search-controls-map .location-error { color: red; font-size: 0.8em; text-align: center; margin-top: 0.25rem; width: 100%; /* Take full width below */ order: 3; /* Place below button/search */}
.search-controls-map .location-info { color: #555; font-size: 0.8em; text-align: center; margin-top: 0.25rem; width: 100%; /* Take full width below */ order: 3; }

/* Style for the clear button */
.clear-drawn-area-button {
  padding: 0.5rem 1rem;
  background-color: #f0ad4e; /* Warning color */
  color: white;
  border: 1px solid #eea236;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  white-space: nowrap;
  height: fit-content; /* Align with search inputs */
  margin-top: 1.5rem; /* Align vertically roughly with search inputs */
  flex-shrink: 0; /* Prevent button shrinking */
  order: 2; /* Place after search bar */
}
.clear-drawn-area-button:hover {
  background-color: #ec971f;
  border-color: #d58512;
}


.map-content-area {
  display: flex;
  flex-grow: 1;
  gap: 1rem;
  padding: 0 1rem 1rem 1rem;
  overflow: hidden;
}


/* --- Desktop Sidebar Styles --- */
.category-sidebar-desktop {
  width: 250px;
  flex-shrink: 0;
  background-color: var(--color-background-soft, #f9f9f9);
  border-radius: var(--border-radius, 8px);
  border: 1px solid var(--color-border, #e0e0e0);
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.sidebar-title {
  padding: 1rem 1rem 0.5rem 1rem;
  margin: 0;
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--color-heading, #333);
  border-bottom: 1px solid var(--color-border, #e0e0e0);
  flex-shrink: 0;
}
.clear-category-button {
  background: none;
  border: 1px solid var(--color-border, #ccc);
  border-radius: 4px;
  padding: 0.2rem 0.5rem;
  margin: 0.5rem 1rem 0 1rem;
  cursor: pointer;
  font-size: 0.8rem;
  color: var(--color-text);
  align-self: flex-start;
}
.clear-category-button:hover {
  background-color: var(--color-background-mute);
}


.scrollable-categories {
  flex-grow: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 0 1rem 1rem 1rem;
}
.scrollable-categories::-webkit-scrollbar { width: 6px; }
.scrollable-categories::-webkit-scrollbar-track { background: transparent; }
.scrollable-categories::-webkit-scrollbar-thumb { background-color: var(--color-border-hover, #ccc); border-radius: 3px; }
.scrollable-categories::-webkit-scrollbar-thumb:hover { background-color: var(--color-text, #555); }

/* --- Map Wrapper Styles --- */
.map-wrapper {
  flex: 1;
  position: relative;
  border-radius: var(--border-radius, 8px);
  overflow: hidden;
  height: 100%;
  border: 1px solid var(--color-border, #e0e0e0);
}

/* --- Mobile Bottom Bar Styles --- */
.category-bar-mobile {
  display: none;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 85px;
  background-color: var(--color-background-soft, #f9f9f9);
  border-top: 1px solid var(--color-border, #e0e0e0);
  box-shadow: 0 -2px 5px rgba(0, 0, 0, 0.1);
  overflow-x: auto;
  overflow-y: hidden;
  white-space: nowrap;
  padding: 0.5rem 0.5rem 0.5rem 1rem;
  z-index: 1000;
  box-sizing: border-box;
  align-items: center;
  gap: 0.5rem;
}

.category-bar-mobile::-webkit-scrollbar { height: 4px; }
.category-bar-mobile::-webkit-scrollbar-track { background: transparent; }
.category-bar-mobile::-webkit-scrollbar-thumb { background: var(--color-border-hover, #ccc); border-radius: 2px; }

.clear-category-button-mobile {
  background-color: #e0e0e0;
  border: 1px solid #ccc;
  border-radius: 50%;
  color: #333;
  cursor: pointer;
  font-size: 1rem;
  font-weight: bold;
  line-height: 1;
  width: 28px;
  height: 28px;
  padding: 0;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 0.5rem;
}
.clear-category-button-mobile:hover {
  background-color: #d0d0d0;
}


.category-bar-mobile > :deep(.category-grid-horizontal) {
  flex-grow: 1;
  height: 100%;
  overflow-x: auto;
  overflow-y: hidden;
}
.category-bar-mobile > :deep(.category-row) {
  display: flex;
  flex-wrap: nowrap;
  gap: 0.75rem;
  align-items: center;
  height: 100%;
}
.category-bar-mobile :deep(.category-wrapper) { min-width: auto; height: 100%; }
.category-bar-mobile :deep(.category-button),
.category-bar-mobile :deep(.all-categories-button) { padding: 0.4rem 0.6rem; flex-shrink: 0; height: 100%; }
.category-bar-mobile :deep(.category-icon),
.category-bar-mobile :deep(.all-icon) { width: 28px; height: 28px; margin-bottom: 0.2rem; }
.category-bar-mobile :deep(.category-icon svg),
.category-bar-mobile :deep(.all-icon svg) { width: 20px; height: 20px; }
.category-bar-mobile :deep(.category-label) { font-size: 0.75rem; }

/* --- Media Query for Mobile --- */
@media (max-width: 768px) {
  .map-view-container {
    padding: 0;
    padding-bottom: 85px;
    height: 100vh;
    gap: 0;
  }
  .search-controls-map {
    position: static;
    border-bottom: 1px solid var(--color-border, #e0e0e0);
    /* Hide clear drawn area button on mobile? Optional */
    /* .clear-drawn-area-button { display: none; } */
  }

  .map-content-area {
    flex-direction: column;
    padding: 0;
    gap: 0;
    height: 100%;
    overflow: hidden;
  }

  .category-sidebar-desktop {
    display: none;
  }

  .category-bar-mobile {
    display: flex;
  }

  .map-wrapper {
    width: 100%;
    height: 100%;
    border-radius: 0;
    border: none;
    flex-grow: 1;
  }
  .map-wrapper > :deep(.map-container) {
    height: 100% !important;
    width: 100% !important;
    min-height: unset;
  }
}

/* Default CSS Variables */
:root {
  --color-background: #ffffff;
  --color-background-soft: #f9f9f9;
  --color-background-mute: #f0f0f0;
  --color-border: #e0e0e0;
  --color-border-hover: #cccccc;
  --color-heading: #333333;
  --color-text: #555555;
  --border-radius: 8px;
}
</style>
