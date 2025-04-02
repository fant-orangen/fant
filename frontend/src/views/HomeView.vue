<template>
  <section class="homepage">
    <CategoryGrid @select="onCategoryClick" />
    <CategoryItemList :categoryId="selectedCategoryId" />
  </section>
</template>

<script setup lang="ts">
import CategoryGrid from "@/components/category/categoryGrid.vue";
import CategoryItemList from "@/components/item/CategoryItemList.vue";
import { ref } from 'vue';

const selectedCategoryId = ref<string | null>(null);

function onCategoryClick(categoryId: string) {
  selectedCategoryId.value = categoryId;
}
</script>

<style>
@import '@/assets/styles/responsiveStyles.css'; /* This will still be global */
</style>
