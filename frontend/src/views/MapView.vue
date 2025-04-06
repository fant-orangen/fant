<template>
  <div class="map-view">
    <p class="map-description"></p>
    <MapComponent />
  </div>
</template>

<script setup lang="ts">
import MapComponent from '@/components/map/MapComponent.vue';
</script>

<style scoped>
.map-view {
  max-width: 1800px;
  margin: 0 auto;
  padding: 1rem;
}

.map-description {
  margin-bottom: 1rem;
}
</style>
