<template>
  <div id="map-container"></div>
</template>

<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref } from 'vue';
import L from 'leaflet'; // Import Leaflet


const map = ref<L.Map | null>(null);

onMounted(() => {

  if (!map.value) {

    map.value = L.map('map-container').setView([62.57, 7.08], 10);


    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map.value);

    L.marker([62.57, 7.08]).addTo(map.value)
    .bindPopup('Vestnes!')
    .openPopup();
  }
});

// Clean up the map instance when the component is unmounted
onBeforeUnmount(() => {
  if (map.value) {
    map.value.remove();
    map.value = null;
  }
});
</script>

<style scoped>
#map-container {
  height: 700px;
  width: 100%;
}
</style>
