<!-- frontend/src/components/CategoryItemList.vue -->
<template>
  <div class="item-previews thumbnail-container">
    <ItemPreview v-for="item in items" :key="item.id" :item="item" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import {
  fetchItemsByDistribution,
  fetchPreviewItems,
  fetchPreviewItemsByCategoryId
} from '@/services/ItemService.ts'
import type { ItemPreviewType } from '@/models/Item'
import ItemPreview from '@/components/item/ItemPreview.vue'
import {
  fetchCategoryRecommendations,
  fetchUserViewCount
} from '@/services/RecommendationService.ts'

const props = defineProps<{ categoryId: string | null }>()
const items = ref<ItemPreviewType[]>([])

const minimumViews = 5;

async function fetchItems() {
  try {
    if (props.categoryId) {
      items.value = await fetchPreviewItemsByCategoryId(props.categoryId)
    } else {
      // Insert new code here
      try {
        // Fetch the user's view count
        const viewCount = await fetchUserViewCount()
        console.log(`User view count: ${viewCount}`)

        if (viewCount > minimumViews) {
          // User has enough views for personalized recommendations
          console.log('Using personalized recommendations')
          const recommendations = await fetchCategoryRecommendations()
          items.value = await fetchItemsByDistribution(recommendations)
        } else {
          // Not enough views, show regular items
          console.log('Using regular item list (not enough views)')
          items.value = await fetchPreviewItems()
        }
      } catch (error) {
        console.error('Error checking user views or fetching recommendations:', error)
        // Fallback to standard items if there's an error
        items.value = await fetchPreviewItems()
      }
    }
  } catch (error) {
    console.error('Error fetching items:', error)
  }
}

onMounted(fetchItems)
watch(() => props.categoryId, fetchItems)
</script>
