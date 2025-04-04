<template>
  <div class="category-edit">
    <h2>Manage Categories</h2>
    <form @submit.prevent="handleSubmit">
      <input v-model="form.name" placeholder="Category Label" required />
      <select v-model="form.description">
        <!-- Send only the icon name as lowercase -->
        <option v-for="icon in availableIcons" :key="icon" :value="icon">{{ icon }}</option>
      </select>
      <input v-model="customIconUrl" placeholder="Or enter custom Icon URL" @input="updateIconUrl" />
      <button type="submit">{{ isEditing ? 'Update' : 'Add' }} Category</button>
    </form>
    <ul>
      <li v-for="category in categories" :key="category.id ?? 'new'">
        <span>{{ category.name }}</span>
        <span>{{ category.description }}</span>
        <button @click="editCategory(category)">Edit</button>
        <button @click="removeCategory(category.id ?? '')">Delete</button>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import type { Category } from '@/models/Category';
import { fetchCategories, addCategory, updateCategory, deleteCategory } from '@/services/CategoryService.ts';

const categories = ref<Category[]>([]);
const form = ref<Category>({
  id: null,
  name: '',
  description: '', // This will hold the selected icon name as a string
  parent: null
});
const customIconUrl = ref('');
const isEditing = ref(false);

// Available icon names, no paths or imports
const availableIcons = ref<string[]>([
  'travel', 'appliance', 'boat', 'book', 'camera', 'car', 'clothes', 'computer', 'furniture', 'motorcycle', 'phone', 'art'
]);

async function loadCategories() {
  categories.value = await fetchCategories();
}

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

function editCategory(category: Category) {
  form.value = { ...category };
  customIconUrl.value = category.description;
  isEditing.value = true;
}

async function removeCategory(id: string | number | null) {
  if (id !== null) {
    await deleteCategory(Number(id));
    await loadCategories();
  }
}

function resetForm() {
  form.value = { id: null, name: '', description: '' };
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
