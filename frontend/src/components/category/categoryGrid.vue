<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import type { Category } from '@/models/Category';
import CategoryButton from "./CategoryButton.vue";
import { fetchCategories } from '@/services/CategoryService.ts';

// Set up i18n
const { t } = useI18n();

// Import icons directly
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

// Add props for layout and to track selected category
const props = defineProps<{
  layout?: 'vertical' | 'grid',
  selectedCategoryId?: string | null
}>();

const isVertical = computed(() => props.layout === 'vertical');

// Define categories array
const categories = ref<Category[]>([]);
const emit = defineEmits<{
  select: [categoryId: string]
}>();

// Local icon import map
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

// Map of category names to translation keys
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

// Load categories from API
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
        resolvedIcon = matchedIconKey ? iconMap[matchedIconKey] : '/fallback-icon.png'; // Use fallback if not found
      }
      category.imageUrl = resolvedIcon;

      // Add translation key to the category if a mapping exists
      if (category.name && translationKeyMap[category.name]) {
        category.translationKey = translationKeyMap[category.name];
      }
    });
  } catch (error) {
    console.error("Error loading categories:", error);
  }
}

// Handle category selection
function handleCategoryClick(id: string) {
  emit('select', id);
}

// Reset category selection to show all items
function showAllItems() {
  emit('select', '');
}

onMounted(loadCategories);
</script>

<template>
  <div :class="isVertical ? 'category-list' : 'category-grid'">
    <!-- All Categories button -->
    <button
      class="all-categories-button"
      :class="{ 'active': !selectedCategoryId, 'compact': isVertical }"
      @click="showAllItems"
    >
      <span class="category-icon-placeholder">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <rect x="3" y="3" width="7" height="7" rx="1" />
          <rect x="14" y="3" width="7" height="7" rx="1" />
          <rect x="3" y="14" width="7" height="7" rx="1" />
          <rect x="14" y="14" width="7" height="7" rx="1" />
        </svg>
      </span>
      <span class="category-label">{{ t('category.allItems') }}</span>
    </button>

    <!-- Existing category buttons with translation -->
    <CategoryButton
      v-for="category in categories"
      :key="category.id?.toString() || `temp-${Math.random()}`"
      :label="category.translationKey ? t(category.translationKey) : category.name"
      :icon="category.imageUrl"
      :compact="isVertical"
      :active="selectedCategoryId === category.id?.toString()"
      @click="handleCategoryClick(category.id?.toString() || '')"
    />
  </div>
</template>

<style scoped>
.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 1rem;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
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
}

.all-categories-button:hover {
  background-color: #f0f0f0;
  transform: translateY(-2px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.all-categories-button.active {
  background-color: #e0f2fe;
  border-color: #93c5fd;
}

.category-icon-placeholder {
  width: 48px;
  height: 48px;
  margin-bottom: 0.5rem;
  color: #4b5563;
  display: flex;
  align-items: center;
  justify-content: center;
}

.category-icon-placeholder svg {
  width: 32px;
  height: 32px;
}

.all-categories-button.compact {
  flex-direction: row;
  justify-content: flex-start;
  padding: 0.5rem 0.75rem;
  width: 100%;
}

.all-categories-button.compact .category-icon-placeholder {
  width: 24px;
  height: 24px;
  margin-bottom: 0;
  margin-right: 0.5rem;
}

.all-categories-button.compact .category-icon-placeholder svg {
  width: 18px;
  height: 18px;
}

.category-label {
  font-size: 0.9rem;
  font-weight: 500;
  text-align: center;
}
</style>
