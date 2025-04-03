<template>
  <section class="homepage">
    <CategoryGrid @select="onCategoryClick" />
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
  // For example, you might want to fetch categories or items here
})

function onCategoryClick(categoryId: string) {
  selectedCategoryId.value = categoryId
}
</script>

<style>
@import '@/assets/styles/responsiveStyles.css'; /* This will still be global */
</style>
