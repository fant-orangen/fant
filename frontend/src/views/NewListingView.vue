<template>
  <MarketListingFormComponent />
</template>

<script lang="ts">
import MarketListingFormComponent from '@/components/item/MarketListingFormComponent.vue';

export default {
  components: {
    MarketListingFormComponent,
  },
};
</script>

<style>
@import '@/assets/styles/responsiveStyles.css'; /* This will still be global */
</style>
