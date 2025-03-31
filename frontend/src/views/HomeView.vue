<script setup lang="ts">
import CategoryGrid from "@/components/category/categoryGrid.vue";

function onCategoryClick(category: string) {
  console.log("Clicked", category)
}
</script>

<template>
  <section class="homepage">
    <h1>{{ $t('APP_WELCOME') }}</h1>
    <p>Markedsplass for salg og kjøp av varer</p>
    <CategoryGrid @select="onCategoryClick"/>
  </section>
</template>
