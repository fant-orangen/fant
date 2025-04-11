<template>
  <div :class="[
    isVertical ? 'category-list' :
    isHorizontal ? 'category-row' : 'category-grid'
  ]">
    <!-- Recommended Items Button -->
    <div
      class="category-wrapper"
      @click="showRecommendedItems"
      v-if="!props.noRecommendations && useUserStore().isLoggedInUser"
    >
      <button
        class="all-categories-button recommended-button"
        :class="{
          'active': selectedCategoryId === '-1',
          'compact': isVertical
        }"
      >
        <span class="category-icon all-icon">
          <img :src="recommendedIcon" class="recommended-icon" alt="Recommended" width="32" height="32" />
        </span>
        <span class="category-label">{{ t('category.recommended') }}</span>
      </button>
    </div>

    <!-- All Items Button -->
    <div
      class="category-wrapper"
      @click="showAllItems"
    >
      <button
        class="all-categories-button"
        :class="{
          'active': !selectedCategoryId,
          'compact': isVertical
        }"
      >
        <span class="category-icon all-icon" v-html="getAllCategoryIcon()"></span>
        <span class="category-label">{{ t('category.allItems') }}</span>
      </button>
    </div>

    <!-- Category Buttons -->
    <div
      v-for="category in categories"
      :key="category.id?.toString() || `temp-${Math.random()}`"
      class="category-wrapper"
      @click="handleCategoryClick(category.id?.toString() || '')"
    >
      <CategoryButton
        :label="category.translationKey ? t(category.translationKey) : category.name"
        :icon="category.imageUrl"
        :compact="isVertical"
        :active="selectedCategoryId === category.id?.toString()"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * @fileoverview CategoryGrid component for displaying filterable category buttons.
 * <p>This component provides functionality for:</p>
 * <ul>
 *   <li>Displaying categories in different layouts (vertical, horizontal, grid)</li>
 *   <li>Supporting category selection with visual feedback</li>
 *   <li>Displaying "All Items" and optional "Recommended" filters</li>
 *   <li>Dynamic icon loading and internationalization</li>
 * </ul>
 */
import { ref, onMounted, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import type { Category } from '@/models/Category';
import CategoryButton from "./CategoryButton.vue";
import { fetchCategories } from '@/services/CategoryService.ts';

const { t } = useI18n();

import travelIcon from '@/assets/icons/travelIcon.svg';
import applianceIcon from '@/assets/icons/applianceIcon.svg';
import boatIcon from '@/assets/icons/boatIcon.svg';
import bookIcon from '@/assets/icons/bookIcon.svg';
import cameraIcon from '@/assets/icons/cameraIcon.svg';
import carIcon from '@/assets/icons/carIcon.svg';
import clothesIcon from '@/assets/icons/clothesIcon.svg';
import computerIcon from '@/assets/icons/computerIcon.svg';
import furnitureIcon from '@/assets/icons/furnitureIcon.svg';
import motorcycleIcon from '@/assets/icons/motorcycleIcon.svg';
import phoneIcon from '@/assets/icons/phoneIcon.svg';
import artIcon from '@/assets/icons/artIcon.svg';
import recommendedIcon from '@/assets/icons/recommended.svg';
import { useUserStore } from '@/stores/UserStore.ts'

/**
 * Component props definition
 * @type {Props}
 */
const props = defineProps<{
  /**
   * Display layout for the categories
   * @type {'vertical' | 'grid' | 'horizontal'}
   * @default 'horizontal'
   */
  layout?: 'vertical' | 'grid' | 'horizontal',

  /**
   * Currently selected category ID
   * @type {string | null}
   * @default null
   */
  selectedCategoryId?: string | null,

  /**
   * When true, hides the recommended items button
   * @type {boolean}
   * @default false
   */
  noRecommendations?: boolean
}>();

/**
 * Computed property to determine if vertical layout is active
 * @type {ComputedRef<boolean>}
 */
const isVertical = computed(() => props.layout === 'vertical');

/**
 * Computed property to determine if horizontal layout is active
 * @type {ComputedRef<boolean>}
 */
const isHorizontal = computed(() => props.layout === 'horizontal' || !props.layout);

/**
 * List of categories fetched from the backend
 * @type {Ref<Category[]>}
 */
const categories = ref<Category[]>([]);

/**
 * Emits event when a category is selected
 * @type {EmitFn}
 */
const emit = defineEmits<{
  select: [categoryId: string],
}>();

/**
 * Mapping between category names and their corresponding icon assets
 * @type {Record<string, string>}
 */
const iconMap: Record<string, string> = {
  Travel: travelIcon,
  Appliance: applianceIcon,
  Boat: boatIcon,
  Book: bookIcon,
  Camera: cameraIcon,
  Car: carIcon,
  Clothes: clothesIcon,
  Computer: computerIcon,
  Furniture: furnitureIcon,
  Motorcycle: motorcycleIcon,
  Phone: phoneIcon,
  Art: artIcon,
};

/**
 * Mapping between category names and their translation keys
 * @type {Record<string, string>}
 */
const translationKeyMap: Record<string, string> = {
  'Travel': 'category.travel',
  'Appliance': 'category.appliance',
  'Boat': 'category.boat',
  'Book': 'category.book',
  'Camera': 'category.cameras',
  'Car': 'category.cars',
  'Clothes': 'category.clothes',
  'Computer': 'category.computers',
  'Furniture': 'category.furniture',
  'Motorcycle': 'category.motorcycle',
  'Phone': 'category.phone',
  'Art': 'category.art'
};

/**
 * Generates SVG markup for the "All Categories" icon
 * @returns {string} SVG markup as an HTML string
 */
const getAllCategoryIcon = () => {
  return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
    <rect x="3" y="3" width="7" height="7" rx="1" />
    <rect x="14" y="3" width="7" height="7" rx="1" />
    <rect x="3" y="14" width="7" height="7" rx="1" />
    <rect x="14" y="14" width="7" height="7" rx="1" />
  </svg>`;
};

/**
 * Loads categories from the backend and resolves their icons
 * @async
 * @returns {Promise<void>}
 */
async function loadCategories() {
  try {
    categories.value = await fetchCategories();

    categories.value.forEach(category => {
      let resolvedIcon;

      if (category.imageUrl && category.imageUrl.startsWith('http')) {
        resolvedIcon = category.imageUrl;
      } else {
        const matchedIconKey = Object.keys(iconMap).find(key =>
          key.toLowerCase() === (category.imageUrl || '').toLowerCase()
        );
        resolvedIcon = matchedIconKey ? iconMap[matchedIconKey] : '/fallback-icon.png';
      }
      category.imageUrl = resolvedIcon;

      if (category.name && translationKeyMap[category.name]) {
        category.translationKey = translationKeyMap[category.name];
      }
    });
  } catch (error) {
    console.error("Error loading categories:", error);
  }
}

/**
 * Handles category selection and emits the select event
 * @param {string} id - ID of the selected category
 */
function handleCategoryClick(id: string) {
  emit('select', id);
}

/**
 * Selects the "Recommended Items" filter
 * <p>Emits '-1' as the category ID to indicate recommended items</p>
 */
function showRecommendedItems() {
  emit('select', '-1');
}

/**
 * Selects the "All Items" filter
 * <p>Emits an empty string as the category ID to indicate all items</p>
 */
function showAllItems() {
  emit('select', '');
}

/**
 * Load categories when component is mounted
 */
onMounted(loadCategories);
</script>

<style scoped>
.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(110px, 1fr));
  gap: 1rem;
  max-width: 1200px;
  margin: 0 auto;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  width: 100%;
}

.category-row {
  display: flex;
  flex-wrap: nowrap;
  gap: 1rem;
  overflow-x: auto;
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
  padding: 0.5rem 0;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  border-left: 2px solid lightgray;
  border-right: 2px solid lightgray;
  border-radius: 0px
}

.category-row::-webkit-scrollbar {
  display: none; /* Chrome, Safari, Opera */
}

.category-wrapper {
  flex-shrink: 0;
  min-width: 90px;
  cursor: pointer;
}

.all-categories-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1rem;
  cursor: pointer;
  transition: all 0.2s ease;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
}

.all-categories-button:hover {
  background-color: #f0f0f0;
  transform: translateY(-2px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.all-categories-button.active {
  background-color: var(--vt-c-teal-divider-light);
  border-color: var(--vt-c-teal-dark);
}

.all-icon {
  width: 32px;
  height: 32px;
  margin-bottom: 0.5rem;
  color: #4b5563;
  display: flex;
  align-items: center;
  justify-content: center;
}

.all-icon :deep(svg) {
  width: 32px;
  height: 32px;
}

.all-categories-button.compact {
  flex-direction: row;
  justify-content: flex-start;
  padding: 0.5rem 0.75rem;
}

.all-categories-button.compact .all-icon {
  width: 24px;
  height: 24px;
  margin-bottom: 0;
  margin-right: 0.5rem;
}

.all-categories-button.compact .all-icon :deep(svg) {
  width: 18px;
  height: 18px;
}

.recommended-icon {
  filter: brightness(0) saturate(100%) invert(52%) sepia(78%) saturate(445%) hue-rotate(77deg) brightness(94%) contrast(92%);
}

.recommended-container {
  border: 2px solid lightgray;
}

.category-label {
  font-size: 0.9rem;
  font-weight: 500;
  text-align: center;
}

@media (max-width: 768px) {
  .category-grid {
    grid-template-columns: repeat(auto-fill, minmax(90px, 1fr));
    gap: 0.75rem;
  }

  .category-row {
    gap: 0.75rem;
  }

  .category-wrapper {
    min-width: 80px;
    border: 1px solid #ddd;
    border-radius: 1px;
  }

  .all-categories-button {
    padding: 0.8rem;
  }

  .all-icon :deep(svg) {
    width: 26px;
    height: 26px;
  }

  .category-label {
    font-size: 0.85rem;
  }
}

.recommended-button {
  border-width: 2px; /* Tailwind's emerald-500: a nice green */
}

.recommended-button.active {
  border-color: var(--vt-c-teal-dark);
  background-color: var(--vt-c-teal-divider-light);
}


@media (max-width: 480px) {
  .category-grid {
    grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
    gap: 0.5rem;
  }

  .category-row {
    gap: 0.5rem;
  }

  .category-wrapper {
    min-width: 70px;
    border: 1px solid #ddd;
    border-radius: 1px;
  }

  .all-categories-button {
    padding: 0.7rem;
  }

  .all-icon {
    width: 36px;
    height: 36px;
    margin-bottom: 0.4rem;
  }

  .all-icon :deep(svg) {
    width: 24px;
    height: 24px;
  }

  .category-label {
    font-size: 0.8rem;
  }
}
</style>
