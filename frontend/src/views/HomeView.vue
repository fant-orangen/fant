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
    </div>

    <div class="items-section">
      <ItemList
        :categoryId="selectedCategoryId"
        :searchTerm="searchTerm"
        :latitude="currentLatitude"
        :longitude="currentLongitude"
        :maxDistance="maxDistance"
        :sortOption="sortOption"
        :minPrice="minPrice"
        :maxPrice="maxPrice"
        :pageSize="8"
        emptyMessage="No items found matching your criteria. Try adjusting filters."
        :paginationEnabled="true" />
    </div>
  </section>
</template>

<script setup lang="ts">
import CategoryGrid from '@/components/category/categoryGrid.vue'
import ItemList from '@/components/item/ItemList.vue'
import SearchBar from '@/components/search/searchBar.vue' // Ensure path is correct
import { ref, computed } from 'vue' // Import computed

// Filter States
const selectedCategoryId = ref<string | null>(null)
const searchTerm = ref<string>('')
const maxDistance = ref<number | null>(50)
const currentLatitude = ref<number | null>(null)
const currentLongitude = ref<number | null>(null)
const sortOption = ref<string>('default')
const minPrice = ref<number | null>(null)
const maxPrice = ref<number | null>(null)

// UI State for location fetching
const isFetchingLocation = ref(false);
const locationError = ref<string | null>(null);

// --- Computed property to check if location is ready ---
const isLocationAvailable = computed(() => {
  return currentLatitude.value !== null && currentLongitude.value !== null;
});

// --- Event Handlers ---
function onCategoryClick(categoryId: string) { selectedCategoryId.value = categoryId || null; }
function onSearchTermUpdate(newSearchTerm: string) { searchTerm.value = newSearchTerm; }
function onMaxDistanceUpdate(newMaxDistance: number | null) { maxDistance.value = newMaxDistance; }
function onSortOptionUpdate(newSortOption: string) { sortOption.value = newSortOption; }
function onMinPriceUpdate(newMinPrice: number | null) { minPrice.value = newMinPrice; }
function onMaxPriceUpdate(newMaxPrice: number | null) { maxPrice.value = newMaxPrice; }

function fetchCurrentUserLocation() {
  if (!navigator.geolocation) {
    locationError.value = "Geolocation is not supported by your browser.";
    return;
  }
  isFetchingLocation.value = true;
  locationError.value = null;
  navigator.geolocation.getCurrentPosition(
    (position) => {
      currentLatitude.value = position.coords.latitude;
      currentLongitude.value = position.coords.longitude;
      console.log(`Location obtained: Lat ${currentLatitude.value}, Lon ${currentLongitude.value}`);
      isFetchingLocation.value = false;
      // Clear error message about distance if location is now available
      if (locationError.value?.includes("Set a distance")) {
        locationError.value = null;
      }
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
    }
  );
}

</script>

<style scoped>
/* Styles remain the same */
@import '@/assets/styles/responsiveStyles.css';
.search-section { padding: 1rem 0; margin-bottom: 1rem; display: flex; flex-direction: column; align-items: center; gap: 0.5rem; }
.category-section { margin-bottom: 1.5rem; }
.location-error { color: red; font-size: 0.9em; text-align: center; max-width: 80%; }
.location-info { color: #555; font-size: 0.9em; text-align: center; }
</style>
