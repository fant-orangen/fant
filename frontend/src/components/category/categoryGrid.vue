<script setup lang="ts">
import { ref, onMounted } from 'vue';
import CategoryButton from "@/components/category/categoryButton.vue";
import { fetchCategories } from '@/services/CategoryService.ts';

const categories = ref<{id: string,  label: string, icon: string }[]>([]);

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
      :label="category.label"
      :icon="category.icon"
      @click="handleFunction(category.id)"
    />
  </div>
</template>
