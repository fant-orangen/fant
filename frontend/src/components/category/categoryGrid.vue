<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import CategoryButton from "@/components/category/categoryButton.vue";
import { fetchCategories } from '@/services/CategoryService.ts';

const { t } = useI18n();
const categories = ref<{ label: string, icon: string }[]>([]);

const emit = defineEmits<(e: 'select', categoryLabel: string) => void>();

async function loadCategories() {
  categories.value = await fetchCategories();
}

function handleFunction(label: string) {
  emit('select', label);
}

onMounted(loadCategories);
</script>

<template>
  <div class="category-grid">
    <CategoryButton
      v-for="category in categories"
      :label="category.label"
      :icon="category.icon"
      @click="handleFunction(category.label)"
    />
  </div>
</template>
