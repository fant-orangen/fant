<script setup lang="ts">
/**
 * @fileoverview SearchBar component for filtering listings and search results.
 * <p>This component provides functionality for:</p>
 * <ul>
 *   <li>Text-based search with real-time filtering</li>
 *   <li>Advanced search options with collapsible UI</li>
 *   <li>Price range filtering (min/max)</li>
 *   <li>Distance-based location filtering</li>
 *   <li>Sorting options for search results</li>
 *   <li>Geolocation integration</li>
 * </ul>
 */
import { ref } from 'vue'

/**
 * Props for component configuration
 * @property {boolean} isLocationAvailable - Whether user location services are available
 */
const props = defineProps<{
  isLocationAvailable?: boolean
}>()

/**
 * Event emitter for search parameter updates
 * @type {Emitter}
 */
const emit = defineEmits<{
  (e: 'update:searchTerm', value: string): void
  (e: 'update:maxDistance', value: number | null): void
  (e: 'request-current-location'): void
  (e: 'update:sortOption', value: string): void
  (e: 'update:maxPrice', value: number | null): void
  (e: 'update:minPrice', value: number | null): void
}>()

/**
 * Current search query text value
 * @type {Ref<string>}
 */
const searchQuery = ref('')

/**
 * Maximum distance in kilometers for location-based filtering
 * @type {Ref<number | null>}
 */
const maxDistanceKm = ref<number | null>(50)

/**
 * Minimum price filter value
 * @type {Ref<number | null>}
 */
const minPriceValue = ref<number | null>(null)

/**
 * Maximum price filter value
 * @type {Ref<number | null>}
 */
const maxPriceValue = ref<number | null>(null)

/**
 * Selected sort option for results ordering
 * @type {Ref<string>}
 */
const sortOptionValue = ref<string>('default')

/**
 * Flag controlling visibility of advanced search options
 * @type {Ref<boolean>}
 */
const showAdvanced = ref(false)

/**
 * Handles search text input and emits updated value
 */
function handleSearchInput() {
  emit('update:searchTerm', searchQuery.value)
}

/**
 * Processes minimum price input, validates and emits updated value
 * @param {Event} event - Input change event
 */
function handleMinPriceInput(event: Event) {
  const value = parseFloat((event.target as HTMLInputElement).value)
  minPriceValue.value = isNaN(value) || value < 0 ? null : value
  emit('update:minPrice', minPriceValue.value)
}

/**
 * Processes maximum price input, validates and emits updated value
 * @param {Event} event - Input change event
 */
function handleMaxPriceInput(event: Event) {
  const value = parseFloat((event.target as HTMLInputElement).value)
  maxPriceValue.value = isNaN(value) || value < 0 ? null : value
  emit('update:maxPrice', maxPriceValue.value)
}

/**
 * Handles sort option changes and emits updated selection
 * @param {Event} event - Select change event
 */
function handleSortChange(event: Event) {
  sortOptionValue.value = (event.target as HTMLSelectElement).value
  emit('update:sortOption', sortOptionValue.value)
}

/**
 * Processes maximum distance input, validates and emits updated value
 * @param {Event} event - Slider input event
 */
function handleDistanceInput(event: Event) {
  const value = parseInt((event.target as HTMLInputElement).value, 10)
  maxDistanceKm.value = isNaN(value) || value < 0 ? null : value
  emit('update:maxDistance', maxDistanceKm.value)
}

/**
 * Emits event to trigger geolocation request
 */
function requestLocation() {
  emit('request-current-location')
}

/**
 * Toggles visibility of advanced search options panel
 */
function toggleAdvancedSearch() {
  showAdvanced.value = !showAdvanced.value
}
</script>

<template>
  <div class="search-controls-container">
    <div class="main-search-group">
      <div class="control-group search-term-group">
        <label for="search-term">{{ $t('SEARCH_LABEL') }}</label>
        <input
          id="search-term"
          type="text"
          v-model="searchQuery"
          @input="handleSearchInput"
          :placeholder="$t('SEARCH_PLACEHOLDER')"
          class="text-input"
        />
      </div>
      <button
        @click="toggleAdvancedSearch"
        class="advanced-toggle-button"
        :title="$t('ADVANCED_TOGGLE_TITLE')"
      >
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
          class="text-input"
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
          class="text-input"
        />
      </div>

      <div class="control-group">
        <label for="sort-select">{{ $t('SORT_LABEL') }}</label>
        <select
          id="sort-select"
          v-model="sortOptionValue"
          @change="handleSortChange"
        >
          <option value="default">{{ $t('SORT_DEFAULT') }}</option>
          <option value="price_asc">{{ $t('SORT_PRICE_LOW_HIGH') }}</option>
          <option value="price_desc">{{ $t('SORT_PRICE_HIGH_LOW') }}</option>
        </select>
      </div>

      <div class="control-group location-group">
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
  background-color: var(--vt-c-white-soft);
  border-radius: 6px;
  border: 1px solid var(--vt-c-divider-light-2);
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  box-sizing: border-box;
}

.main-search-group {
  display: flex;
  align-items: flex-end;
  gap: 0.5rem;
  width: 100%;
}

.search-term-group {
  flex-grow: 1;
}

.advanced-toggle-button {
  padding: 0.6rem 0.8rem;
  background-color: var(--vt-c-white-soft);
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
  background-color: var(--vt-c-white-mute);
}

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

.control-group {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  min-width: 120px;
}

/* Contrast override for text-input and select */
.text-input,
select {
  border: 1px solid var(--vt-c-divider-light-1); /* Stronger contrast */
  background-color: var(--vt-c-white-soft);
  color: var(--vt-c-text-dark-2);
}

/* Prevent cutoff in select options */
select {
  padding-right: 48px;
  background-position: right 14px center;
}

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
  background-color: var(--vt-c-teal-soft);
  color: var(--vt-c-white);
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: background-color 0.2s ease;
  width: 100%;
  text-align: center;
}
.location-button:hover {
  background-color: var(--vt-c-teal-dark);
}

@media (min-width: 768px) {
  .advanced-options {
    justify-content: space-between;
  }
  .control-group {
    width: auto;
  }
  .advanced-options input[type="number"] {
    width: 110px;
  }
  .advanced-options select {
    width: 170px;
  }
}

@media (min-width: 992px) {
  .advanced-options input[type="number"] {
    width: 120px;
  }
  .advanced-options select {
    width: 180px;
  }
}
</style>
