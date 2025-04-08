<template>
  <MarketListingFormComponent />
</template>

<script lang="ts">
import NewItemComponent from '@/components/item/NewItemComponent.vue';

export default {
  components: {
    MarketListingFormComponent: NewItemComponent,
  },
};
</script>

<style>
@import '@/assets/styles/responsiveStyles.css'; /* This will still be global */
</style>
