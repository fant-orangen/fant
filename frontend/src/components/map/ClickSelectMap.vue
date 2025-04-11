<template>
  <div
    ref="mapContainer"
    class="location-select-map"
    :style="{ height: height || '300px', width: width || '100%' }"
  ></div>
</template>

<script setup lang="ts">
/**
 * @fileoverview ClickSelectMap component for location selection on interactive maps.
 * <p>This component provides functionality for:</p>
 * <ul>
 *   <li>Interactive map display with click-based location selection</li>
 *   <li>Visual marker placement at selected coordinates</li>
 *   <li>External coordinate control and synchronization</li>
 *   <li>Responsive sizing with automatic resize handling</li>
 * </ul>
 * @author nicktuf
 * @version 1.0
 */
import { ref, onMounted, watch } from 'vue';
import 'leaflet/dist/leaflet.css';
import * as L from 'leaflet';

/**
 * Component props definition
 */
const props = defineProps<{

  /**
   * Height of the map container
   * @type {string}
   * @default "300px"
   */
  height?: string;

  /**
   * Width of the map container
   * @type {string}
   * @default "100%"
   */
  width?: string;

  /**
   * Initial map center latitude
   * @type {number}
   * @default 60.39 (Bergen, Norway)
   */
  initialLatitude?: number;

  /**
   * Initial map center longitude
   * @type {number}
   * @default 5.32 (Bergen, Norway)
   */
  initialLongitude?: number;

  /**
   * Initial map zoom level
   * @type {number}
   * @default 12
   */
  initialZoom?: number;

  /**
   * Pre-selected location latitude
   * @type {number|null}
   */
  selectedLatitude?: number | null;

  /**
   * Pre-selected location longitude
   * @type {number|null}
   */
  selectedLongitude?: number | null;
}>();

/**
 * Event emitters definition
 */
const emit = defineEmits<{
  /**
   * Emitted when a user selects a location on the map
   * @param {{ lat: number, lng: number }} Location coordinates
   */
  'location-selected': [{ lat: number, lng: number }]
}>();

/**
 * Reference to the map container HTML element
 * @type {Ref<HTMLElement|null>}
 */
const mapContainer = ref<HTMLElement | null>(null);

/**
 * Reference to the Leaflet map instance
 * @type {Ref<L.Map|null>}
 */
const map = ref<L.Map | null>(null);

/**
 * Reference to the location selection marker
 * @type {Ref<L.Marker|null>}
 */
const selectionMarker = ref<L.Marker | null>(null);

/**
 * Initializes the map for location selection
 * <p>Sets up the map instance, tile layer, and click handler</p>
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
 * <p>Places marker at click location and emits coordinates</p>
 * @param {L.LeafletMouseEvent} e - Click event from Leaflet
 */
function handleMapClick(e: L.LeafletMouseEvent) {
  const { lat, lng } = e.latlng;
  placeMarker(lat, lng);
  emit('location-selected', { lat, lng });
}

/**
 * Place or update the selection marker
 * <p>Removes existing marker if present and creates a new one</p>
 * @param {number} lat - Latitude for marker placement
 * @param {number} lng - Longitude for marker placement
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

/**
 * Initialize component on mount
 * <p>Sets up map and resize handlers</p>
 */
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

<style scoped>
.location-select-map {
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #ddd;
}
</style>
