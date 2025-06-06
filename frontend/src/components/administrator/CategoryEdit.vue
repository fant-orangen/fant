<template>
  <div class="category-edit">
    <h2>{{ $t('MANAGE_CATEGORIES') }}</h2>

    <div class="card">
      <h3>{{ isEditing ? $t('EDIT') : $t('ADD') }} {{ $t('CATEGORIES') }}</h3>
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="name">{{ $t('CATEGORY_NAME') }}</label>
          <input id="name" v-model="form.name" :placeholder="$t('ENTER_CATEGORY_NAME')" required />
        </div>

        <div class="form-group">
          <label>{{ $t('ICON') }}</label>
          <div class="icon-selector">
            <select v-model="form.imageUrl">
              <option value="" disabled>{{ $t('SELECT_ICON') }}</option>
              <option v-for="icon in availableIcons" :key="icon" :value="icon">{{ icon }}</option>
            </select>
            <div v-if="resolvedIcon" class="icon-preview-container">
              <img :src="resolvedIcon" :alt="$t('ICON_PREVIEW')" class="icon-preview" />
            </div>
          </div>
        </div>

        <div class="form-group">
          <label for="custom-icon">{{ $t('CUSTOM_ICON_URL') }}</label>
          <input
            id="custom-icon"
            v-model="customIconUrl"
            :placeholder="$t('CUSTOM_ICON_URL_PLACEHOLDER')"
            @input="updateIconUrl"
          />
        </div>

        <div class="form-actions">
          <button type="submit" class="edit-button">
            {{ isEditing ? $t('UPDATE') : $t('ADD_CATEGORY') }}
          </button>
          <button v-if="isEditing" type="button" class="cancel-button" @click="resetForm">
            {{ $t('CANCEL') }}
          </button>
        </div>
      </form>
    </div>

    <div class="card categories-list">
      <h3>{{ $t('CATEGORIES') }}</h3>
      <ul>
        <li v-for="category in categories" :key="category.id ?? 'new'" class="category-item">
          <div class="category-info">
            <img :src="getIconForCategory(category)" :alt="$t('CATEGORY_ICON')" class="icon" />
            <span class="category-name">{{ category.name }}</span>
          </div>
          <div class="category-actions">
            <button class="edit-button" @click="editCategory(category)">{{ $t('EDIT') }}</button>
            <button class="delete-button" @click="removeCategory(category.id ?? '')">{{ $t('DELETE') }}</button>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">

/**
 * @fileoverview CategoryEdit component for managing categories in the administrator panel.
 * <p>This component provides functionality for:</p>
 * <ul>
 *   <li>Viewing all existing categories</li>
 *   <li>Creating new categories</li>
 *   <li>Editing existing categories</li>
 *   <li>Deleting categories</li>
 *   <li>Assigning predefined or custom icons to categories</li>
 * </ul>
 */

import '@/assets/styles/buttons/buttons.css';

import { ref, onMounted, computed } from 'vue';
import type { Category } from '@/models/Category';
import { fetchCategories, addCategory, updateCategory, deleteCategory } from '@/services/CategoryService.ts';

// Import icons
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

/**
 * List of categories retrieved from the backend
 * @type {Ref<Category[]>}
 */
const categories = ref<Category[]>([]);
const form = ref<Category>({
  id: null,
  name: '',
  imageUrl: '',
  parent: null
});

/**
 * Custom icon URL input value
 * @type {Ref<string>}
 */
const customIconUrl = ref('');

/**
 * Flag indicating whether we're in edit mode or create mode
 * @type {Ref<boolean>}
 */
const isEditing = ref(false);

/**
 * List of available predefined icons
 * @type {Ref<string[]>}
 */
const availableIcons = ref<string[]>([
  'travel', 'appliance', 'boat', 'book', 'camera', 'car', 'clothes', 'computer', 'furniture', 'motorcycle', 'phone', 'art'
]);

/**
 * Mapping between icon names and their imported SVG assets
 * @type {Record<string, string>}
 */
const iconMap: Record<string, string> = {
  travel: travelIcon,
  appliance: applianceIcon,
  boat: boatIcon,
  book: bookIcon,
  camera: cameraIcon,
  car: carIcon,
  clothes: clothesIcon,
  computer: computerIcon,
  furniture: furnitureIcon,
  motorcycle: motorcycleIcon,
  phone: phoneIcon,
  art: artIcon,
};

/**
 * Resolves the current icon selection to its display URL
 * @returns {string|null} The URL to display for the selected icon
 */
const resolvedIcon = computed(() => {
  if (!form.value.imageUrl) return null;

  if (form.value.imageUrl.startsWith('http')) {
    return form.value.imageUrl;
  }

  return iconMap[form.value.imageUrl.toLowerCase()] || null;
});

/**
 * Gets the icon URL for a specific category
 * @param {Category} category - The category to get an icon for
 * @returns {string} URL of the icon to display
 */
function getIconForCategory(category: Category): string {
  console.log("kategori: ", category);
  if (category.imageUrl.startsWith('http')) {
    return category.imageUrl;
  }

  return iconMap[category.imageUrl.toLowerCase()] || '/fallback-icon.png';
}

/**
 * Loads all categories from the backend service
 * @async
 * @returns {Promise<void>}
 */
async function loadCategories() {
  categories.value = await fetchCategories();
}

/**
 * Handles form submission for both create and update operations
 * @async
 * @returns {Promise<void>}
 */
async function handleSubmit() {
  console.log(form.value)
  if (isEditing.value) {
    await updateCategory(form.value);
  } else {
    await addCategory(form.value);
  }
  resetForm();
  await loadCategories();
}

/**
 * Sets the form state to edit an existing category
 * @param {Category} category - The category to edit
 */
function editCategory(category: Category) {
  form.value = { ...category };

  // Set customIconUrl only if it's an actual URL
  customIconUrl.value = category.imageUrl.startsWith('http')
    ? category.imageUrl
    : '';

  isEditing.value = true;
}

/**
 * Removes a category by its ID
 * @async
 * @param {string|number|null} id - ID of the category to remove
 * @returns {Promise<void>}
 */
async function removeCategory(id: string | number | null) {
  if (id !== null) {
    await deleteCategory(Number(id));
    await loadCategories();
  }
}

/**
 * Resets the form to its initial state
 */
function resetForm() {
  form.value = { id: null, name: '', imageUrl: '' };
  customIconUrl.value = '';
  isEditing.value = false;
}

/**
 * Updates the form's imageUrl when a custom URL is entered
 */
function updateIconUrl() {
  if (customIconUrl.value) {
    form.value.imageUrl = customIconUrl.value;
  } else {
    form.value.imageUrl = ''; // Reset if no URL is provided
  }
}

/**
 * Load categories when component is mounted
 */
onMounted(loadCategories);
</script>

<style scoped>
.category-edit {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

h2 {
  margin-bottom: 20px;
  color: #333;
  text-align: center;
}

h3 {
  margin: 0 0 15px 0;
  color: #444;
}

.card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 25px;
}

.form-group {
  margin-bottom: 16px;
}

label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #555;
}

input, select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

input:focus, select:focus {
  outline: none;
  border-color: #4a90e2;
  box-shadow: 0 0 0 2px rgba(74, 144, 226, 0.2);
}

.icon-selector {
  display: flex;
  align-items: center;
  gap: 15px;
}

.icon-preview-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: #f5f5f5;
  border-radius: 4px;
}

.icon-preview {
  width: 24px;
  height: 24px;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}
.categories-list {
  margin-top: 30px;
}

ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.category-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  border-bottom: 1px solid #eee;
}

.category-item:last-child {
  border-bottom: none;
}

.category-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon {
  width: 24px;
  height: 24px;
  object-fit: contain;
}

.category-name {
  font-size: 15px;
  font-weight: 500;
}

.category-actions {
  display: flex;
  gap: 8px;
}

@media (max-width: 600px) {
  .category-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .category-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
