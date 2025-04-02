// Example: In src/views/HomeView.vue
<template>
  <div>
    <h1>{{ $t('APP_WELCOME') }}</h1>
    <MapComponent />
  </div>
</template>

<script setup lang="ts">
import MapComponent from '@/components/map/MapComponent.vue'; // Import the map component

</script>
