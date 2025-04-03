<template>
  <div class="category-edit">
    <h2>Manage Categories</h2>
    <form @submit.prevent="handleSubmit">
      <input v-model="form.name" placeholder="Category Label" required />
      <select v-model="form.description">
        <option v-for="icon in availableIcons" :key="icon.path" :value="icon.path">{{ icon.name }}</option>
      </select>
      <input v-model="customIconUrl" placeholder="Or enter custom Icon URL" @input="updateIconUrl" />
      <button type="submit">{{ isEditing ? 'Update' : 'Add' }} Category</button>
    </form>
    <ul>
      <li v-for="category in categories" :key="category.id">
        <span>{{ category.name }}</span>
        <img :src="category.description" :alt="category.name" class="icon" />
        <button @click="editCategory(category)">Edit</button>
        <button @click="removeCategory(category.id)">Delete</button>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import type { Category } from '@/models/Category';
import { fetchCategories, addCategory, updateCategory, deleteCategory } from '@/services/CategoryService.ts';

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

const categories = ref<Category[]>([]);
const form = ref<Category>({
  id: '0',
  name: '',
  description: '',
  parent: null
});
const customIconUrl = ref('');
const isEditing = ref(false);
const availableIcons = ref<{ name: string, path: string }[]>([
  { name: 'Travel', path: travelIcon },
  { name: 'Appliance', path: applianceIcon },
  { name: 'Boat', path: boatIcon },
  { name: 'Book', path: bookIcon },
  { name: 'Camera', path: cameraIcon },
  { name: 'Car', path: carIcon },
  { name: 'Clothes', path: clothesIcon },
  { name: 'Computer', path: computerIcon },
  { name: 'Furniture', path: furnitureIcon },
  { name: 'Motorcycle', path: motorcycleIcon },
  { name: 'Phone', path: phoneIcon },
  { name: 'Art', path: artIcon },
]);

async function loadCategories() {
  categories.value = await fetchCategories();
}

async function handleSubmit() {
  if (isEditing.value) {
    await updateCategory(form.value.id, { label: form.value.name, icon: form.value.description });
  } else {
    const highestId = Math.max(...categories.value.map(category => Number(category.id)));
    form.value.id = (highestId + 1).toString();
    await addCategory(form.value);
  }
  resetForm();
  await loadCategories();
}

function editCategory(category: Category) {
  form.value = { ...category };
  customIconUrl.value = category.description;
  isEditing.value = true;
}

async function removeCategory(id: string) {
  await deleteCategory(id);
  await loadCategories();
}

function resetForm() {
  form.value = { id: '0', name: '', description: '' };
  customIconUrl.value = '';
  isEditing.value = false;
}

function updateIconUrl() {
  if (customIconUrl.value) {
    form.value.description = customIconUrl.value;
  } else {
    form.value.description = ''; // Reset if no URL is provided
  }
}

onMounted(loadCategories);
</script>

<style scoped>
.category-edit {
  max-width: 600px;
  margin: auto;
}

form {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.icon {
  width: 24px;
  height: 24px;
}
</style>
