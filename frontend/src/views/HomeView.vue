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
        :is-location-available="isLocationAvailable"
      />
      <p v-if="locationError" class="location-error">{{ locationError }}</p>
      <p v-if="isFetchingLocation" class="location-info">Getting your location...</p>
    </div>

    <div class="category-section">
      <div class="category-scroll-container">
        <div class="category-content-wrapper" ref="categoryScrollContainer">
          <div class="category-content-row">
            <CategoryGrid
              layout="horizontal"
              @select="onCategoryClick"
              :selectedCategoryId="selectedCategoryId"
            />
            <div class="button-stack">
              <CategoryButton
                :label="thumbnailToggleLabel"
                :icon="thumbnailicon"
                @click="onToggleThumbnailSize"
                class="toggle-scroll-button"
              />
              <CategoryButton
                :label="scrollToggleLabel"
                :icon="scrollicon"
                @click="onScrollButtonClick"
                class="toggle-scroll-button"
              />
            </div>
          </div>
        </div>
      </div>

      <button
        v-if="selectedCategoryId"
        @click="clearCategorySelection"
        class="clear-category-button"
      >
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
        :paginationEnabled="paginationEnabled"
        :smallThumbnails="displaySmallThumbnails"
        emptyMessage="No items found matching your criteria. Try adjusting filters."
      />
    </div>
  </section>
</template>

<script setup lang="ts">
/**
 * Home View component.
 *
 * This is the main landing page component that provides item search, filtering,
 * and browsing capabilities with various customization options.
 *
 * Features:
 * - Search bar with term-based search
 * - Category selection with horizontal scrolling
 * - Location-based search with distance filtering
 * - Price range filtering
 * - Item sorting options
 * - Configurable pagination or infinite scroll
 * - Adjustable thumbnail size
 * - Personalized recommendations for logged-in users
 *
 * @component
 */
import { ref, computed, onMounted, watch, nextTick, onBeforeUnmount } from 'vue'
import CategoryGrid from '@/components/category/CategoryGrid.vue'
import CategoryButton from '@/components/category/CategoryButton.vue'
import ItemList from '@/components/item/ItemList.vue'
import SearchBar from '@/components/search/searchBar.vue'
import {
  searchItems,
  type ItemSearchParams,
  fetchItemsByDistribution,
} from '@/services/ItemService'
import type { ItemPreviewType } from '@/models/Item'
import { fetchCategories } from '@/services/CategoryService'
import type { Category } from '@/models/Category'
import {
  fetchCategoryRecommendations,
  fetchUserViewCount,
} from '@/services/RecommendationService.ts'
import { useUserStore } from '@/stores/UserStore.ts'
import { useI18n } from 'vue-i18n'

import scrollicon from '@/assets/icons/scrollicon.svg'
import thumbnailicon from '@/assets/icons/thumbnailicon.svg'

const { t } = useI18n()

const selectedCategoryId = ref<string | null>(null)
const searchTerm = ref<string>('')
const maxDistance = ref<number | null>(50)
const currentLatitude = ref<number | null>(null)
const currentLongitude = ref<number | null>(null)
const sortOption = ref<string>('default')
const minPrice = ref<number | null>(null)
const maxPrice = ref<number | null>(null)
const categories = ref<Category[]>([])

const items = ref<ItemPreviewType[]>([])
const savedPage = parseInt(localStorage.getItem('savedCurrentPage') || '1')
const currentPage = ref(savedPage > 0 ? savedPage : 1)
const totalPages = ref(1)
const isLoading = ref(false)
const error = ref<string | null>(null)
const isFetchingLocation = ref(false)
const insufficientItemViews = ref(false)
const locationError = ref<string | null>(null)
const pageSize = ref(12)

const paginationEnabled = ref(localStorage.getItem('paginationEnabled') !== 'false')
watch(paginationEnabled, newVal => {
  localStorage.setItem('paginationEnabled', String(newVal))
})

const displaySmallThumbnails = ref(localStorage.getItem('displaySmallThumbnails') === 'true')
watch(displaySmallThumbnails, newVal => {
  localStorage.setItem('displaySmallThumbnails', String(newVal))
})

const scrollToggleLabel = computed(() =>
  paginationEnabled.value ? t('SCROLL_SETTING_SCROLL') : t('SCROLL_SETTING_PAGE')
)
const thumbnailToggleLabel = computed(() =>
  displaySmallThumbnails.value ? t('THUMBNAIL_SETTING_LARGE') : t('THUMBNAIL_SETTING_SMALL')
)

function onScrollButtonClick() {
  paginationEnabled.value = !paginationEnabled.value
  currentPage.value = 1
  items.value = []
  nextTick(() => fetchItems())
}

function onToggleThumbnailSize() {
  displaySmallThumbnails.value = !displaySmallThumbnails.value
}

const isLocationAvailable = computed(() =>
  currentLatitude.value !== null && currentLongitude.value !== null
)

const selectedCategoryName = computed(() => {
  if (!selectedCategoryId.value) return null
  const found = categories.value.find(cat => cat.id?.toString() === selectedCategoryId.value)
  return found ? found.name : null
})

const backendSortParam = computed(() => {
  switch (sortOption.value) {
    case 'price_asc': return 'price,asc'
    case 'price_desc': return 'price,desc'
    default: return null
  }
})

/**
 * Fetches items based on current filters and search parameters.
 * Will use recommendations if the recommendation category is selected.
 *
 * @returns {Promise<void>}
 */
async function fetchItems() {
  isLoading.value = true
  error.value = null

  const params: ItemSearchParams = {
    searchTerm: searchTerm.value || null,
    minPrice: minPrice.value,
    maxPrice: maxPrice.value,
    status: 'ACTIVE',
    categoryName: selectedCategoryName.value,
    userLatitude: currentLatitude.value,
    userLongitude: currentLongitude.value,
    maxDistance: isLocationAvailable.value ? maxDistance.value : null,
    page: currentPage.value - 1,
    size: pageSize.value,
    sort: backendSortParam.value ?? undefined,
  }

  try {
    const isRecommendationSelected = selectedCategoryId.value === '-1'

    if (useUserStore().isLoggedInUser && isRecommendationSelected) {
      const views = await fetchUserViewCount()
      if (views > 3) {
        const recs = await fetchCategoryRecommendations()
        const response = await fetchItemsByDistribution(recs)
        items.value = response.content ?? []
        totalPages.value = response.totalPages ?? 1
      } else {
        items.value = []
        totalPages.value = 1
        error.value = 'You have insufficient item views to fetch recommendations.'
        insufficientItemViews.value = true
      }
    } else if (!useUserStore().isLoggedInUser && isRecommendationSelected) {
      items.value = []
      totalPages.value = 1
      error.value = 'You must be logged in to view recommended items.'
    } else {
      const response = await searchItems(params)
      if (paginationEnabled.value && currentPage.value === 1) {
        items.value = response.content ?? []
      } else if (!paginationEnabled.value && currentPage.value > 1) {
        items.value.push(...(response.content ?? []))
      } else {
        items.value = response.content ?? []
      }
      totalPages.value = response.totalPages ?? 1
    }
  } catch (err) {
    error.value = 'Failed to load items. Please try again later.'
    items.value = []
    totalPages.value = 1
  } finally {
    isLoading.value = false
  }
}

/**
 * Handles category selection/deselection.
 *
 * @param {string} categoryId - The ID of the clicked category
 * @returns {void}
 */
function onCategoryClick(categoryId: string) {
  selectedCategoryId.value = selectedCategoryId.value === categoryId ? null : categoryId
}
function clearCategorySelection() {
  selectedCategoryId.value = null
}
function onSearchTermUpdate(val: string) {
  searchTerm.value = val
}
function onMaxDistanceUpdate(val: number | null) {
  maxDistance.value = val
}
function onSortOptionUpdate(val: string) {
  sortOption.value = val
}
function onMinPriceUpdate(val: number | null) {
  minPrice.value = val
}
function onMaxPriceUpdate(val: number | null) {
  maxPrice.value = val
}
function onPageChange(page: number) {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    if (paginationEnabled.value) {
      localStorage.setItem('savedCurrentPage', String(page))
    }
  }
}

/**
 * Requests and stores the user's current geolocation.
 *
 * @returns {void}
 */
function fetchCurrentUserLocation() {
  if (!navigator.geolocation) {
    locationError.value = 'Geolocation is not supported by your browser.'
    return
  }

  isFetchingLocation.value = true
  navigator.geolocation.getCurrentPosition(
    pos => {
      currentLatitude.value = pos.coords.latitude
      currentLongitude.value = pos.coords.longitude
      isFetchingLocation.value = false
      locationError.value = null
    },
    err => {
      currentLatitude.value = null
      currentLongitude.value = null
      isFetchingLocation.value = false
      locationError.value = 'Failed to get your location.'
    }
  )
}

const categoryScrollContainer = ref<HTMLElement | null>(null)
let cleanupScrollListener: (() => void) | null = null

function enableHorizontalScrollOnWheel(wrapper: HTMLElement) {
  const scrollTarget = wrapper.querySelector('.category-row') as HTMLElement | null

  if (!scrollTarget) return () => {}

  const handler = (e: WheelEvent) => {
    if (e.deltaY !== 0 && scrollTarget.scrollWidth > scrollTarget.clientWidth) {
      e.preventDefault()
      scrollTarget.scrollLeft += e.deltaY
    }
  }

  wrapper.addEventListener('wheel', handler, { passive: false })
  return () => wrapper.removeEventListener('wheel', handler)
}

onMounted(() => {
  fetchCategories().then(res => categories.value = res)

  if (paginationEnabled.value) {
    const stored = parseInt(localStorage.getItem('savedCurrentPage') || '1')
    if (!isNaN(stored) && stored > 0) currentPage.value = stored
  }

  if (categoryScrollContainer.value) {
    cleanupScrollListener = enableHorizontalScrollOnWheel(categoryScrollContainer.value)
  }

  fetchItems()
})

onBeforeUnmount(() => {
  if (cleanupScrollListener) cleanupScrollListener()
})

watch(
  [
    searchTerm,
    minPrice,
    maxPrice,
    selectedCategoryId,
    sortOption,
    currentLatitude,
    currentLongitude,
    maxDistance,
    currentPage,
  ],
  (newVals, oldVals) => {
    const pageIndex = newVals.length - 1
    const onlyPageChanged = newVals.every((val, i) => i === pageIndex || val === oldVals[i])

    if (!onlyPageChanged && currentPage.value !== 1) {
      currentPage.value = 1
    } else {
      fetchItems()
    }
  },
  { deep: true }
)
</script>

<style scoped>
@import '@/assets/styles/responsiveStyles.css';

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
  width: 100%;
  overflow-y: visible;
}

.category-scroll-container {
  width: 100%;
  overflow-x: auto;
  overflow-y: visible;
}


.category-content-wrapper {
  display: flex;
  justify-content: center;
  width: 100%;
  overflow-x: auto; /* <-- ADD THIS */
  white-space: nowrap; /* <-- Helps preserve row layout */
  overflow-y: visible;
}

.category-content-row {
  display: inline-flex;
  align-items: center;
  gap: 1rem;
  min-width: max-content; /* <-- Makes sure it expands beyond wrapper if needed */
}

.button-stack {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.toggle-scroll-button {
  width: 80px;
  height: 60px;
  padding: 0;
  border: 1px solid var(--vt-c-teal-text-light);
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

.location-error,
.location-info {
  font-size: 0.9em;
  text-align: center;
  max-width: 80%;
}

.location-error {
  color: red;
}
.location-info {
  color: #555;
}
</style>
