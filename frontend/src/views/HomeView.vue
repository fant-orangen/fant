<template>
  <section class="homepage">
    <div class="category-section">
      <CategoryGrid
        layout="horizontal"
        @select="onCategoryClick"
        :selectedCategoryId="selectedCategoryId"
      />

    </div>

    <div class="items-section">
      <ItemList
        :categoryId="selectedCategoryId"
        :pageSize="8"  emptyMessage="No items found. Try selecting a category."
        :paginationEnabled="true" />
    </div>
  </section>
</template>

<script setup lang="ts">
import CategoryGrid from '@/components/category/categoryGrid.vue' //
import ItemList from '@/components/item/ItemList.vue' //
import { ref } from 'vue'

const selectedCategoryId = ref<string | null>(null) //

function onCategoryClick(categoryId: string) {
  selectedCategoryId.value = categoryId || null
}


</script>

<style scoped>

@import '@/assets/styles/responsiveStyles.css';
</style>
