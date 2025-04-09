<template>
  <EditAddItem
    :onSubmit="createItem"
  />
</template>

<script lang="ts">
import EditAddItem from "@/components/item/EditAddItem.vue";
import {createItem} from "@/services/ItemService.ts";

export default {
  methods: {createItem},
  components: {
    EditAddItem,
  },
};
</script>

<style>
@import '@/assets/styles/responsiveStyles.css'; /* This will still be global */
</style>
