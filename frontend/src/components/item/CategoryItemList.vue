<template>
  <div class="item-previews thumbnail-container">
    <ItemList
      :categoryId="categoryId"
      :pageSize="10"
      emptyMessage="No items found in this category."
    />
  </div>
</template>

<script setup lang="ts">
import { defineProps } from 'vue';
import ItemList from '@/components/item/ItemList.vue';

const props = defineProps<{
  categoryId: string | null;
}>();
</script>

<style scoped>
.thumbnail-container {
  padding: 1rem;
}
</style>
