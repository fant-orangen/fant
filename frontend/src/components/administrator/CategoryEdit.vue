<template>
  <div class="category-edit">
    <h2>Manage Categories</h2>
    <form @submit.prevent="handleSubmit">
      <input v-model="form.label" placeholder="Category Label" required />
      <input v-model="form.icon" placeholder="Category Icon URL" required />
      <button type="submit">{{ isEditing ? 'Update' : 'Add' }} Category</button>
    </form>
    <ul>
      <li v-for="category in categories" :key="category.id">
        <span>{{ category.label }}</span>
        <img :src="category.icon" :alt="category.label" class="icon" />
        <button @click="editCategory(category)">Edit</button>
        <button @click="deleteCategory(category.id)">Delete</button>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import applianceIcon from "@/assets/icons/applianceIcon.svg";
import boatIcon from "@/assets/icons/boatIcon.svg";
import bookIcon from "@/assets/icons/bookIcon.svg";
import cameraIcon from "@/assets/icons/cameraIcon.svg";
import carIcon from "@/assets/icons/carIcon.svg";
import clothesIcon from "@/assets/icons/clothesIcon.svg";
import computerIcon from "@/assets/icons/computerIcon.svg";
import furnitureIcon from "@/assets/icons/furnitureIcon.svg";
import motorcycleIcon from "@/assets/icons/motorcycleIcon.svg";
import phoneIcon from "@/assets/icons/phoneIcon.svg";
import artIcon from "@/assets/icons/artIcon.svg";
import travelIcon from "@/assets/icons/travelIcon.svg";

interface Category {
  id: number;
  label: string;
  icon: string;
}

const categories = ref<Category[]>([
  { id: 1, label: "appliance", icon: applianceIcon },
  { id: 2, label: "boat", icon: boatIcon },
  { id: 3, label: "book", icon: bookIcon },
  { id: 4, label: "cameras", icon: cameraIcon },
  { id: 5, label: "cars", icon: carIcon },
  { id: 6, label: "clothes", icon: clothesIcon },
  { id: 7, label: "computers", icon: computerIcon },
  { id: 8, label: "furniture", icon: furnitureIcon },
  { id: 9, label: "motorcycle", icon: motorcycleIcon },
  { id: 10, label: "phone", icon: phoneIcon },
  { id: 11, label: "art", icon: artIcon },
  { id: 12, label: "travel", icon: travelIcon }
  // Add initial categories here
]);

const form = ref<Category>({ id: 0, label: '', icon: '' });
const isEditing = ref(false);

function handleSubmit() {
  if (isEditing.value) {
    const index = categories.value.findIndex(cat => cat.id === form.value.id);
    if (index !== -1) {
      categories.value[index] = { ...form.value };
    }
  } else {
    form.value.id = Date.now();
    categories.value.push({ ...form.value });
  }
  resetForm();
}

function editCategory(category: Category) {
  form.value = { ...category };
  isEditing.value = true;
}

function deleteCategory(id: number) {
  categories.value = categories.value.filter(cat => cat.id !== id);
}

function resetForm() {
  form.value = { id: 0, label: '', icon: '' };
  isEditing.value = false;
}
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
