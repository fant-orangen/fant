<template>
  <ItemDetail :itemId="itemId" />
</template>

<script setup lang="ts">
/**
 * Item Detail View component.
 *
 * This component serves as a wrapper for the ItemDetail component, passing the
 * item ID from the route parameters. It handles route parameter extraction and
 * ensures the ID is correctly formatted before passing it to the child component.
 *
 * @component
 */
import { useRoute } from 'vue-router';
import ItemDetail from '@/components/item/ItemDetail.vue'

/**
 * Extracts the item ID from route parameters.
 * Handles both array and string parameter formats.
 *
 * @type {string}
 */
const route = useRoute();
const itemId = Array.isArray(route.params.id) ? route.params.id[0] : route.params.id;
</script>
