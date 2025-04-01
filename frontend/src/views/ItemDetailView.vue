<template>
  <ItemDetail :itemId="itemId" />
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';
import ItemDetail from '@/components/item/ItemDetail.vue';

const route = useRoute();
const itemId = Array.isArray(route.params.id) ? route.params.id[0] : route.params.id;
</script>
