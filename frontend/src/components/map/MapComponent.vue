<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';
import { fetchPreviewItems } from '@/services/ItemService';
import type { ItemPreviewType } from '@/models/Item';

// Define the props
const props = defineProps<{
  height?: string;
  width?: string;
  initialLatitude?: number;
  initialLongitude?: number;
  initialZoom?: number;
}>();

// Create refs
const mapContainer = ref<HTMLElement | null>(null);
const map = ref<L.Map | null>(null);
const markers = ref<L.Marker[]>([]);
const items = ref<ItemPreviewType[]>([]);

// Initialize the map
function initializeMap() {
  if (!mapContainer.value) return;

  // Set default values if props not provided
  const latitude = props.initialLatitude ?? 60.39;
  const longitude = props.initialLongitude ?? 5.32;
  const zoom = props.initialZoom ?? 7;

  // Create Leaflet map instance
  map.value = L.map(mapContainer.value).setView([latitude, longitude], zoom);

  // Add the tile layer (OpenStreetMap)
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
  }).addTo(map.value as L.Map);

  // Load items with coordinates
  loadItemsWithCoordinates();
}

// Function to load items and add markers for those with coordinates
async function loadItemsWithCoordinates() {
  try {
    // Make sure fetchPreviewItems returns items with imageUrl
    items.value = await fetchPreviewItems(); // [cite: uploaded:frontend 2/frontend/src/services/itemService.ts]

    // Remove existing markers
    if (map.value) {
      markers.value.forEach(marker => {
        marker.remove();
      });
      markers.value = [];
    }

    // Add new markers for items with coordinates
    items.value.forEach(item => {
      // Ensure item, coordinates, and map exist
      if (item && item.latitude != null && item.longitude != null && map.value) {
        // Check if imageUrl exists and is not empty
        const imageUrlContent = item.imageUrl
          ? `<img src="${item.imageUrl}" alt="${item.title || 'Item image'}" style="max-width: 100px; max-height: 100px; display: block; margin-top: 5px; margin-bottom: 5px;">`
          : ''; // Empty string if no image URL

        const marker = L.marker([item.latitude, item.longitude])
        .addTo(map.value as L.Map)
        .bindPopup(`
            <div style="text-align: center;"> <strong>${item.title || 'No Title'}</strong><br>
              Price: ${item.price ?? 'N/A'} kr
              ${imageUrlContent} </div>
          `);

        markers.value.push(marker);
      }
    });
  } catch (error) {
    console.error('Failed to load items for map:', error);
  }
}

// Watch for changes in initial coordinates
watch(() => [props.initialLatitude, props.initialLongitude, props.initialZoom],
  ([newLat, newLng, newZoom]) => {
    if (map.value && newLat && newLng) {
      map.value.setView([newLat, newLng], newZoom || map.value.getZoom());
    }
  }
);

// Initialize the map on component mount
onMounted(() => {
  initializeMap();

  // Handle resize for responsive behavior
  function handleResize() {
    if (map.value) {
      map.value.invalidateSize();
    }
  }

  window.addEventListener('resize', handleResize);

  // Clean up event listener
  return () => {
    window.removeEventListener('resize', handleResize);
  };
});
</script>

<template>
  <div
    ref="mapContainer"
    class="map-container"
    :style="{ height: height || '700px', width: width || '100%' }"
  ></div>
</template>

<style scoped>
.map-container {
  min-height: 700px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ddd;
}
</style>
