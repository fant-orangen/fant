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
      <div class="category-container">
        <div class="scroll-wrapper">
          <CategoryGrid
            layout="horizontal"
            @select="onCategoryClick"
            :selectedCategoryId="selectedCategoryId"
          />
        </div>

        <div class="display-button-wrapper">
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
import { ref, computed, onMounted, watch, nextTick } from 'vue'
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

// --- Icons ---
import scrollicon from '@/assets/icons/scrollicon.svg'
import thumbnailicon from '@/assets/icons/thumbnailicon.svg'

// --- Filters & State ---
const selectedCategoryId = ref<string | null>(null)
const searchTerm = ref<string>('')
const maxDistance = ref<number | null>(50)
const currentLatitude = ref<number | null>(null)
const currentLongitude = ref<number | null>(null)
const sortOption = ref<string>('default')
const minPrice = ref<number | null>(null)
const maxPrice = ref<number | null>(null)
const categories = ref<Category[]>([])

// --- Items & UI ---
const items = ref<ItemPreviewType[]>([])
const currentPage = ref(1)
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

const displaySmallThumbnails = ref(false)
const scrollToggleLabel = computed(() =>
  paginationEnabled.value ? 'Scroll' : 'Page'
)
const thumbnailToggleLabel = computed(() =>
  displaySmallThumbnails.value ? 'Large' : 'Small'
)

// --- Button Actions ---
function onScrollButtonClick() {
  paginationEnabled.value = !paginationEnabled.value
  currentPage.value = 1
  items.value = []
  nextTick(() => fetchItems())
}

function onToggleThumbnailSize() {
  displaySmallThumbnails.value = !displaySmallThumbnails.value
}

// --- Computed ---
const isLocationAvailable = computed(() =>
  currentLatitude.value !== null && currentLongitude.value !== null
)

const selectedCategoryName = computed(() => {
  if (!selectedCategoryId.value) return null
  const foundCategory = categories.value.find(
    cat => cat.id?.toString() === selectedCategoryId.value
  )
  return foundCategory ? foundCategory.name : null
})

const backendSortParam = computed(() => {
  switch (sortOption.value) {
    case 'price_asc': return 'price,asc'
    case 'price_desc': return 'price,desc'
    default: return null
  }
})

// --- Fetch Items ---
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
    console.error('Error fetching items:', err)
    error.value = 'Failed to load items. Please try again later.'
    items.value = []
    totalPages.value = 1
  } finally {
    isLoading.value = false
  }
}

// --- Event Handlers ---
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
  }
}
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
      console.error('Geolocation error:', err)
      currentLatitude.value = null
      currentLongitude.value = null
      isFetchingLocation.value = false
      locationError.value = 'Failed to get your location.'
    }
  )
}

// --- Watch to trigger item fetch ---
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

onMounted(() => {
  fetchCategories().then(res => categories.value = res)
  fetchItems()
})
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
}

.category-container {
  display: flex;
  flex-direction: row;
  align-items: stretch;
  width: 100%;
  overflow: hidden;
  gap: 1rem;
}

.scroll-wrapper {
  flex-grow: 1;
  overflow-x: auto;
  padding-bottom: 0.5rem;
}

.display-button-wrapper {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
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
  border: 1px solid rgb(128, 200, 190);
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
