<script setup lang="ts">
// --- Script remains the same as the previous version ---
import { ref } from 'vue';

const props = defineProps<{
  isLocationAvailable?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:searchTerm', value: string): void
  (e: 'update:maxDistance', value: number | null): void
  (e: 'request-current-location'): void
  (e: 'update:sortOption', value: string): void
  (e: 'update:maxPrice', value: number | null): void
  (e: 'update:minPrice', value: number | null): void
}>();

const searchQuery = ref('');
const maxDistanceKm = ref<number | null>(50);
const minPriceValue = ref<number | null>(null);
const maxPriceValue = ref<number | null>(null);
const sortOptionValue = ref<string>('default');
const showAdvanced = ref(false);

function handleSearchInput() { emit('update:searchTerm', searchQuery.value); }
function handleMinPriceInput(event: Event) {
  const target = event.target as HTMLInputElement; const value = parseFloat(target.value);
  if (!isNaN(value) && value >= 0) { minPriceValue.value = value; emit('update:minPrice', minPriceValue.value); }
  else { minPriceValue.value = null; emit('update:minPrice', null); }
}
function handleMaxPriceInput(event: Event) {
  const target = event.target as HTMLInputElement; const value = parseFloat(target.value);
  if (!isNaN(value) && value >= 0) { maxPriceValue.value = value; emit('update:maxPrice', maxPriceValue.value); }
  else { maxPriceValue.value = null; emit('update:maxPrice', null); }
}
function handleSortChange(event: Event) {
  const target = event.target as HTMLSelectElement; sortOptionValue.value = target.value; emit('update:sortOption', sortOptionValue.value);
}
function handleDistanceInput(event: Event) {
  const target = event.target as HTMLInputElement; const value = parseInt(target.value, 10);
  if (!isNaN(value) && value >= 0) { maxDistanceKm.value = value; emit('update:maxDistance', maxDistanceKm.value); }
  else { maxDistanceKm.value = null; emit('update:maxDistance', null); }
}
function requestLocation() { emit('request-current-location'); }
function toggleAdvancedSearch() { showAdvanced.value = !showAdvanced.value; }

</script>

<template>
  <div class="search-controls-container">
    <div class="main-search-group">
      <div class="control-group search-term-group">
        <label for="search-term">Search:</label>
        <input id="search-term" type="text" v-model="searchQuery" @input="handleSearchInput" placeholder="Search by title..." class="search-input"/>
      </div>
      <button @click="toggleAdvancedSearch" class="advanced-toggle-button" title="Toggle Advanced Search"> {{ showAdvanced ? '[- Less]' : '[+ More Filters]' }} </button>
    </div>
    <div v-if="showAdvanced" class="advanced-options">
      <div class="control-group">
        <label for="min-price">Min Price:</label>
        <input id="min-price" type="number" :value="minPriceValue" @input="handleMinPriceInput" placeholder="e.g., 100" min="0" step="10" class="number-input" />
      </div>
      <div class="control-group">
        <label for="max-price">Max Price:</label>
        <input id="max-price" type="number" :value="maxPriceValue" @input="handleMaxPriceInput" placeholder="e.g., 500" min="0" step="10" class="number-input" />
      </div>
      <div class="control-group">
        <label for="sort-select">Sort By:</label>
        <select id="sort-select" v-model="sortOptionValue" @change="handleSortChange" class="select-input">
          <option value="default">Default</option> <option value="price_asc">Price: Low to High</option> <option value="price_desc">Price: High to Low</option>
        </select>
      </div>
      <div class="control-group location-group">
        <label for="distance-slider">Max Distance: {{ maxDistanceKm ?? 0 }} km</label>
        <input id="distance-slider" type="range" :value="maxDistanceKm ?? 0" @input="handleDistanceInput" min="0" max="500" step="10" class="distance-slider" :disabled="!isLocationAvailable" :title="isLocationAvailable ? 'Adjust search distance' : 'Click \'Use My Location\' first'" />
        <button @click="requestLocation" class="location-button"> Use My Location </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* --- Main Container --- */
.search-controls-container {
  padding: 1rem 1.5rem;
  background-color: #f8f8f8;
  border-radius: 6px;
  border: 1px solid #eee;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  /* *** Make the container wider *** */
  width: 100%;            /* Use full available width */
  max-width: 1100px;     /* Limit maximum width */
  margin: 0 auto;        /* Center the container horizontally */
  box-sizing: border-box; /* Include padding/border in width */
}

/* --- Main Search Group (Input + Toggle) --- */
.main-search-group {
  display: flex;
  align-items: flex-end;
  gap: 0.5rem;
  width: 100%;
}

.search-term-group {
  flex-grow: 1; /* Allow search input group to take available space */
  /* min-width removed to allow more flexibility */
}

.search-input {
  /* Inherits width: 100% from common styles below */
  /* Removed fixed width from media query */
}


.advanced-toggle-button {
  padding: 0.6rem 0.8rem;
  background-color: #e0e0e0;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  color: #333;
  white-space: nowrap;
  transition: background-color 0.2s;
  flex-shrink: 0; /* Prevent button from shrinking */
}
.advanced-toggle-button:hover { background-color: #d0d0d0; }


/* --- Advanced Options Container --- */
.advanced-options {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem; /* Keep gap between controls */
  width: 100%;
  border-top: 1px dashed #ddd;
  padding-top: 1rem;
  align-items: flex-end;
  justify-content: flex-start; /* Align items to the start */
}


/* --- Individual Control Groups --- */
.control-group {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  /* flex-grow: 1; removed to allow natural sizing based on content */
  min-width: 120px; /* Reintroduce minimum width for smaller inputs */
}
/* Allow location group to take more space if needed */
.location-group {
  flex-grow: 1;
  min-width: 250px;
}


.control-group label { font-size: 0.85rem; color: #555; margin-bottom: 0.1rem; }

/* --- Input Styles --- */
.search-input, .number-input, .select-input {
  padding: 0.6rem 0.8rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 0.95rem;
  box-sizing: border-box;
  width: 100%; /* Inputs take full width of their group */
}
.number-input { -moz-appearance: textfield; }
.number-input::-webkit-outer-spin-button, .number-input::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }

/* --- Location Specific --- */
.distance-slider { width: 100%; cursor: pointer; height: 20px; margin-top: 5px; }
.distance-slider:disabled { cursor: not-allowed; opacity: 0.5; }
.location-button { padding: 0.6rem 1rem; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 0.95rem; transition: background-color 0.2s ease; margin-top: 0.5rem; text-align: center; width: 100%; }
.location-button:hover { background-color: #0056b3; }

/* --- Responsive Adjustments --- */
/* Media query adjustments focus on layout changes */
@media (min-width: 768px) {
  .advanced-options {
    /* Use space-between for better distribution on medium screens */
    justify-content: space-between;
  }
  .control-group {
    width: auto; /* Allow groups to size naturally */
  }
  /* Specific widths for advanced controls on medium screens */
  .advanced-options .number-input { width: 110px; }
  .advanced-options .select-input { width: 170px; }
  .advanced-options .location-group {
    width: 300px; /* Adjust width as needed */
    flex-grow: 0; /* Don't let it grow excessively */
  }
  .location-group { flex-direction: row; align-items: center; gap: 1rem; }
  .location-group label { margin-bottom: 0; white-space: nowrap; }
  .location-button { width: auto; margin-top: 0; }
  .distance-slider { width: auto; flex-grow: 1; }

}

@media (min-width: 992px) {
  /* On larger screens, maybe allow advanced controls more width */
  .advanced-options .number-input { width: 120px; }
  .advanced-options .select-input { width: 180px; }
  .advanced-options .location-group { width: 320px; }
}

</style>
