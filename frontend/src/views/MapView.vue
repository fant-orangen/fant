<template>
  <div class="map-view">
    <h1>{{ $t('MAP') }}</h1>
    <p class="map-description">Explore items available in different locations</p>
    <MapComponent />
  </div>
</template>

<script setup lang="ts">
import MapComponent from '@/components/map/MapComponent.vue';
</script>

<style scoped>
.map-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem;
}

.map-description {
  margin-bottom: 1rem;
}
</style>
