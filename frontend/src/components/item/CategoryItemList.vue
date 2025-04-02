<!-- frontend/src/components/CategoryItemList.vue -->
<template>
  <div class="item-previews thumbnail-container">
    <ItemPreview v-for="item in items" :key="item.id" :item="item" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { fetchPreviewItems, fetchPreviewItemsByCategoryId } from '@/services/ItemService.ts';
import type { ItemPreviewType } from '@/models/Item';
import ItemPreview from "@/components/item/ItemPreview.vue";

const props = defineProps<{ categoryId: string | null }>();
const items = ref<ItemPreviewType[]>([]);

async function fetchItems() {
  try {
    if (props.categoryId) {
      items.value = await fetchPreviewItemsByCategoryId(props.categoryId);
    } else {
      items.value = await fetchPreviewItems();
    }
  } catch (error) {
    console.error('Error fetching items:', error);
  }
}

onMounted(fetchItems);
watch(() => props.categoryId, fetchItems);
</script>

