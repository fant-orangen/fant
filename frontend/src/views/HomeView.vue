<template>
  <section class="homepage">
    <CategoryGrid
      @select="onCategoryClick"
      :selectedCategoryId="selectedCategoryId"
    />
    <CategoryItemList :categoryId="selectedCategoryId" />
  </section>
</template>

<script setup lang="ts">
import CategoryGrid from '@/components/category/categoryGrid.vue'
import CategoryItemList from '@/components/item/CategoryItemList.vue'
import { ref, onMounted } from 'vue'

const selectedCategoryId = ref<string | null>(null)

onMounted(async () => {
  // Fetch initial data or perform any setup needed when the component is mounted
})

function onCategoryClick(categoryId: string) {
  // If empty string is received, set to null (all items)
  selectedCategoryId.value = categoryId || null;
}
</script>

<style>
@import '@/assets/styles/responsiveStyles.css';
</style>
