<template>
  <section class="homepage">
    <CategoryGrid @select="onCategoryClick" />
    <div class="item-previews thumbnail-container">
      <ItemPreview v-for="item in items" :key="item.id" :item="item" />
    </div>
  </section>
</template>

<script setup lang="ts">
import CategoryGrid from "@/components/category/categoryGrid.vue";
import ItemPreview from "@/components/item/itemPreview.vue";
import { useRouter } from "vue-router";
import { ref, onMounted } from 'vue';
import { fetchPreviewItems } from '@/services/itemService/itemPreviewService';
import type { ItemPreviewType } from '@/models/Item';

const router = useRouter();
const items = ref<ItemPreviewType[]>([]);

function onCategoryClick(categoryKey: string) {
  router.push({ name: 'category', params: { categoryKey } });
}

onMounted(async () => {
  try {
    items.value = await fetchPreviewItems();
  } catch (error) {
    console.error('Error fetching items:', error);
  }
});
</script>
<style>
@import '@/assets/styles/responsiveStyles.css'; /* This will still be global */
</style>
<style scoped>

</style>
