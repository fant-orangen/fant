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
  const target = event.target as HTMLInputElement;
  const value = parseFloat(target.value);
  if (!isNaN(value) && value >= 0) {
    minPriceValue.value = value;
    emit('update:minPrice', minPriceValue.value);
  } else {
    minPriceValue.value = null;
    emit('update:minPrice', null);
  }
}

function handleMaxPriceInput(event: Event) {
  const target = event.target as HTMLInputElement;
  const value = parseFloat(target.value);
  if (!isNaN(value) && value >= 0) {
    maxPriceValue.value = value;
    emit('update:maxPrice', maxPriceValue.value);
  } else {
    maxPriceValue.value = null;
    emit('update:maxPrice', null);
  }
}

function handleSortChange(event: Event) {
  const target = event.target as HTMLSelectElement;
  sortOptionValue.value = target.value;
  emit('update:sortOption', sortOptionValue.value);
}

function handleDistanceInput(event: Event) {
  const target = event.target as HTMLInputElement;
  const value = parseInt(target.value, 10);
  if (!isNaN(value) && value >= 0) {
    maxDistanceKm.value = value;
    emit('update:maxDistance', maxDistanceKm.value);
  } else {
    maxDistanceKm.value = null;
    emit('update:maxDistance', null);
  }
}

function requestLocation() {
  emit('request-current-location');
}

function toggleAdvancedSearch() {
  showAdvanced.value = !showAdvanced.value;
}
</script>

<template>
  <div class="search-controls-container">
    <div class="main-search-group">
      <div class="control-group search-term-group">
        <!-- Use translation key for label -->
        <label for="search-term">{{ $t('SEARCH_LABEL') }}</label>
        <input
          id="search-term"
          type="text"
          v-model="searchQuery"
          @input="handleSearchInput"
          :placeholder="$t('SEARCH_PLACEHOLDER')"
          class="search-input"
        />
      </div>
      <button
        @click="toggleAdvancedSearch"
        class="advanced-toggle-button"
        :title="$t('ADVANCED_TOGGLE_TITLE')"
      >
        <!-- Toggle text for advanced search -->
        {{ showAdvanced ? $t('ADVANCED_LESS') : $t('ADVANCED_MORE') }}
      </button>
    </div>

    <div v-if="showAdvanced" class="advanced-options">
      <div class="control-group">
        <label for="min-price">{{ $t('MIN_PRICE_LABEL') }}</label>
        <input
          id="min-price"
          type="number"
          :value="minPriceValue"
          @input="handleMinPriceInput"
          :placeholder="$t('MIN_PRICE_PLACEHOLDER')"
          min="0"
          step="10"
          class="number-input"
        />
      </div>
      <div class="control-group">
        <label for="max-price">{{ $t('MAX_PRICE_LABEL') }}</label>
        <input
          id="max-price"
          type="number"
          :value="maxPriceValue"
          @input="handleMaxPriceInput"
          :placeholder="$t('MAX_PRICE_PLACEHOLDER')"
          min="0"
          step="10"
          class="number-input"
        />
      </div>
      <div class="control-group">
        <label for="sort-select">{{ $t('SORT_LABEL') }}</label>
        <select
          id="sort-select"
          v-model="sortOptionValue"
          @change="handleSortChange"
          class="select-input"
        >
          <option value="default">{{ $t('SORT_DEFAULT') }}</option>
          <option value="price_asc">{{ $t('SORT_PRICE_LOW_HIGH') }}</option>
          <option value="price_desc">{{ $t('SORT_PRICE_HIGH_LOW') }}</option>
        </select>
      </div>
      <div class="control-group location-group">
        <!-- Example with dynamic placeholder using interpolation -->
        <label for="distance-slider">
          {{ $t('MAX_DISTANCE_LABEL', { distance: maxDistanceKm ?? 0 }) }}
        </label>
        <input
          id="distance-slider"
          type="range"
          :value="maxDistanceKm ?? 0"
          @input="handleDistanceInput"
          min="0"
          max="500"
          step="10"
          class="distance-slider"
          :disabled="!isLocationAvailable"
          :title="isLocationAvailable ? $t('DISTANCE_SLIDER_TITLE') : $t('DISTANCE_SLIDER_DISABLED')"
        />
        <button @click="requestLocation" class="location-button">
          {{ $t('USE_MY_LOCATION') }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>

.search-controls-container {
  padding: 1rem 1.5rem;
  background-color: var(--vt-c-white-soft); /* #f8f8f8 */
  border-radius: 6px;
  border: 1px solid var(--vt-c-divider-light-2); /* Subtle light border */
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  box-sizing: border-box;
}

/* --- Main Search Group (Input + Toggle) --- */
.main-search-group {
  display: flex;
  align-items: flex-end;
  gap: 0.5rem;
  width: 100%;
}

.search-term-group {
  flex-grow: 1;
}

.search-input {
  width: 100%;
}

/* --- Toggle Button --- */
.advanced-toggle-button {
  padding: 0.6rem 0.8rem;
  background-color: var(--vt-c-white-soft); /* Using light background */
  border: 1px solid var(--vt-c-divider-light-1);
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  color: var(--vt-c-text-dark-2);
  white-space: nowrap;
  transition: background-color 0.2s;
  flex-shrink: 0;
}

.advanced-toggle-button:hover {
  background-color: var(--vt-c-white-mute); /* Slightly darker muted white */
}

/* --- Advanced Options Container --- */
.advanced-options {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  width: 100%;
  border-top: 1px dashed var(--vt-c-divider-light-2);
  padding-top: 1rem;
  align-items: flex-start;
  justify-content: flex-start;
}

/* --- Individual Control Groups --- */
.control-group {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  min-width: 120px;
}

/* --- Input Styles --- */
.search-input,
.number-input,
.select-input {
  padding: 0.6rem 0.8rem;
  border: 1px solid var(--vt-c-divider-light-1);
  border-radius: 4px;
  font-size: 0.95rem;
  box-sizing: border-box;
  width: 100%;
  background-color: var(--vt-c-white);
  color: var(--vt-c-text-dark-2);
}

.number-input {
  -moz-appearance: textfield;
}
.number-input::-webkit-outer-spin-button,
.number-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

/*
  --- Location Specific ---
  Force column layout at all screen sizes so the button is always below the slider.
*/
.location-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  width: 100%;
}

.distance-slider {
  width: 100%;
  cursor: pointer;
  height: 20px;
}
.distance-slider:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.location-button {
  padding: 0.6rem 1rem;
  background-color: var(--vt-c-teal-soft); /* Using medium soft teal */
  color: var(--vt-c-white);
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: background-color 0.2s ease;
  width: 100%; /* full width in a column layout */
  text-align: center;
}

.location-button:hover {
  background-color: var(--vt-c-teal-dark); /* Darker teal for hover */
}


/* --- Responsive Adjustments (Optional) ---
   If you don't want any layout changes for the location group at larger screens,
   simply omit the rules that switch to row layout.
*/

@media (min-width: 768px) {
  .advanced-options {
    justify-content: space-between;
  }
  .control-group {
    width: auto;
  }
  .advanced-options .number-input {
    width: 110px;
  }
  .advanced-options .select-input {
    width: 170px;
  }
  /*
    If you previously had .location-group set to row layout,
    remove or comment it out to keep the button below:

    .location-group {
      flex-direction: row; // Remove or comment out
      ...
    }
  */
}

@media (min-width: 992px) {
  .advanced-options .number-input {
    width: 120px;
  }
  .advanced-options .select-input {
    width: 180px;
  }
  /*
    .location-group {
      max-width: 320px; // optional
    }
  */
}
</style>
