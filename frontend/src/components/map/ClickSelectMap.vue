<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import 'leaflet/dist/leaflet.css';
import * as L from 'leaflet';

// Define props and emits
const props = defineProps<{
  height?: string;
  width?: string;
  initialLatitude?: number;
  initialLongitude?: number;
  initialZoom?: number;
  selectedLatitude?: number | null;
  selectedLongitude?: number | null;
}>();

const emit = defineEmits<{
  'location-selected': [{ lat: number, lng: number }]
}>();

// Component references
const mapContainer = ref<HTMLElement | null>(null);
const map = ref<L.Map | null>(null);
const selectionMarker = ref<L.Marker | null>(null);

/**
 * Initializes the map for location selection
 */
function initializeMap() {
  if (!mapContainer.value) return;

  // Default to Bergen, Norway if not specified
  const latitude = props.initialLatitude ?? 60.39;
  const longitude = props.initialLongitude ?? 5.32;
  const zoom = props.initialZoom ?? 12;

  // Create map instance
  map.value = L.map(mapContainer.value).setView([latitude, longitude], zoom);

  // Add tile layer
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
  }).addTo(map.value as L.Map);

  // Set up click handler
  map.value.on('click', handleMapClick);

  // If initial selection coordinates are provided, show marker
  if (props.selectedLatitude && props.selectedLongitude) {
    placeMarker(props.selectedLatitude, props.selectedLongitude);
  }
}

/**
 * Handle map click events
 */
function handleMapClick(e: L.LeafletMouseEvent) {
  const { lat, lng } = e.latlng;
  placeMarker(lat, lng);
  emit('location-selected', { lat, lng });
}

/**
 * Place or update the selection marker
 */
function placeMarker(lat: number, lng: number) {
  if (!map.value) return;

  // Remove existing marker if any
  if (selectionMarker.value) {
    selectionMarker.value.remove();
  }

  // Create marker icon
  const icon = L.icon({
    iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
    shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
  });

  // Create marker
  selectionMarker.value = L.marker([lat, lng], { icon })
  .addTo(map.value as L.Map)  // Add type assertion here
  .bindPopup('Selected location')
  .openPopup();
}

// Watch for external changes to selected coordinates
watch(
  () => [props.selectedLatitude, props.selectedLongitude],
  ([newLat, newLng]) => {
    if (map.value && newLat !== undefined && newLng !== undefined && newLat !== null && newLng !== null) {
      placeMarker(newLat, newLng);
      map.value.setView([newLat, newLng]);
    }
  }
);

onMounted(() => {
  // Initialize map
  initializeMap();

  // Handle resize events
  const handleResize = () => {
    if (map.value) {
      map.value.invalidateSize();
    }
  };

  window.addEventListener('resize', handleResize);

  return () => {
    window.removeEventListener('resize', handleResize);
  };
});
</script>

<template>
  <div
    ref="mapContainer"
    class="location-select-map"
    :style="{ height: height || '300px', width: width || '100%' }"
  ></div>
</template>

<style scoped>
.location-select-map {
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #ddd;
}
</style>
