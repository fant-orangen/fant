<template>
  <div class="item-previews thumbnail-container">
    <ItemList
      :fetchFunction="fetchItems"
      :fetchParams="[props.categoryId]"
      emptyMessage="No items found in this category."
      :pageSize="10"
      :paginationEnabled="true"
    />
  </div>
</template>

<script setup lang="ts">
import { defineProps } from 'vue';
import {
  fetchPreviewItemsByCategoryId,
  fetchPreviewItems,
  fetchItemsByDistribution
} from '@/services/ItemService.ts';
import {
  fetchCategoryRecommendations,
  fetchUserViewCount
} from '@/services/RecommendationService.ts';
import ItemList from '@/components/item/ItemList.vue';

const props = defineProps<{ categoryId: string | null }>();
const minimumViews = 5;

// Wrapper function to handle pagination
async function fetchItems(categoryId: string | null) {
  if (categoryId) {
    // Modify your API service to accept pagination parameters
    return await fetchPreviewItemsByCategoryId(categoryId); //not added pagination.
  } else {
    try {
      const viewCount = await fetchUserViewCount();
      if (viewCount > minimumViews) {
        const recommendations = await fetchCategoryRecommendations();
        // Modify your API service to accept pagination parameters
        return await fetchItemsByDistribution(recommendations); //not added pagination
      } else {
        // Modify your API service to accept pagination parameters
        return await fetchPreviewItems(); //not pagination
      }
    } catch (error) {
      console.error('Error with recommendations:', error);
      // Modify your API service to accept pagination parameters
      return await fetchPreviewItems(); //not pagination
    }
  }
}
</script>
