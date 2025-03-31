<script setup lang="ts">
import CategoryGrid from "@/components/category/categoryGrid.vue";
import {useRouter} from "vue-router";

const router = useRouter();

function onCategoryClick(categoryKey: string) {
  router.push({ name: 'category', params: { categoryKey } })
}
</script>

<template>
  <section class="homepage">
    <h1>{{ $t('APP_WELCOME') }}</h1>
    <p>Markedsplass for salg og kjøp av varer</p>
    <!-- Listen for 'select' event -->
    <CategoryGrid @select="onCategoryClick"/>
  </section>
</template>
