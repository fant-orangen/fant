<template>
  <section class="homepage">
    <SearchField
      :apiCall="searchProductsApi"
      placeholder="Search for products..."
    />
    <CategoryGrid @select="onCategoryClick" />
    <h1>Search Products</h1>
    <ItemList
      :fetchFunction="fetchItems"
      :fetchParams="[selectedCategoryId]"
      emptyMessage="No items found. Try selecting a category."
      :pageSize="12"
      :paginationEnabled="false"
    />
  </section>
</template>

<script setup lang="ts">
import CategoryGrid from '@/components/category/categoryGrid.vue'
import ItemList from '@/components/item/ItemList.vue'
import SearchField from '@/components/input/SearchField.vue';
import { ref, onMounted } from 'vue'
import {
  fetchPreviewItemsByCategoryId,
  fetchPreviewItems,
  fetchItemsByDistribution
} from '@/services/ItemService.ts';
import {
  fetchCategoryRecommendations,
  fetchUserViewCount
} from '@/services/RecommendationService.ts';

const selectedCategoryId = ref<string | null>(null)
const minimumViews = 5;

function onCategoryClick(categoryId: string) {
  // If empty string is received, set to null (all items)
  selectedCategoryId.value = categoryId || null;
}

// Function to handle fetching items with or without a category
async function fetchItems(categoryId: string | null) {
  if (categoryId) {
    return await fetchPreviewItemsByCategoryId(categoryId);
  } else {
    try {
      const viewCount = await fetchUserViewCount();
      if (viewCount > minimumViews) {
        const recommendations = await fetchCategoryRecommendations();
        return await fetchItemsByDistribution(recommendations);
      } else {
        return await fetchPreviewItems();
      }
    } catch (error) {
      console.error('Error with recommendations:', error);
      return await fetchPreviewItems();
    }
  }
}
</script>

<style scoped>
@import '@/assets/styles/responsiveStyles.css';
</style>
