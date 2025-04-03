<script setup lang="ts">
import { ref, onMounted } from 'vue';
import type { Category } from '@/models/Category';
import CategoryButton from "@/components/category/categoryButton.vue";
import { fetchCategories } from '@/services/CategoryService.ts';

const categories = ref<Category[]>([]);

const emit = defineEmits<(e: 'select', categoryLabel: string) => void>();

async function loadCategories() {
  categories.value = await fetchCategories();
}
function handleFunction(id: string) {
  emit('select', id);
}

onMounted(loadCategories);
</script>

<template>
  <div class="category-grid">
    <CategoryButton
      v-for="category in categories"
      :key="category.id"
      :label="category.name"
      :icon="category.description"
      @click="handleFunction(category.id)"
    />
  </div>
</template>
