<template>
  <div class="category-edit-view">
    <h1>Category Management</h1>
    <CategoryEdit />
  </div>
</template>

<script setup lang="ts">
import CategoryEdit from '@/components/administrator/CategoryEdit.vue';
</script>

<style scoped>
.category-edit-view {
  padding: 20px;
}
</style>
