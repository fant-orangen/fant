<template>
  <section class="homepage">
    <CategoryGrid @select="onCategoryClick" />
    <ItemList
      :categoryId="selectedCategoryId"
      :pageSize="6"
      emptyMessage="No items found. Try selecting a category."
    />
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import CategoryGrid from '@/components/category/categoryGrid.vue';
import ItemList from '@/components/item/ItemList.vue';

const selectedCategoryId = ref<string | null>(null);

function onCategoryClick(categoryId: string) {
  selectedCategoryId.value = categoryId || null;
}
</script>

<style scoped>
@import '@/assets/styles/responsiveStyles.css';
</style>
