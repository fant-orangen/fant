<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import 'leaflet/dist/leaflet.css'; // Required Leaflet CSS for proper map display
import L from 'leaflet';
import { fetchPreviewItems, fetchPreviewItemsByCategoryId } from '@/services/ItemService';
import type { ItemPreviewType } from '@/models/Item';
import type { MapComponentProps } from '@/models/MapComponent.ts';


const props = defineProps<MapComponentProps & {
  categoryId?: string | null;
}>();

/**
 * Component References
 * @property {HTMLElement | null} mapContainer - Reference to the DOM element containing the map
 * @property {L.Marker[]} markers - Array to store and track all item markers
 * @property {ItemPreviewType[]} items - Array to store fetched items with location data
 */
const mapContainer = ref<HTMLElement | null>(null);
const map = ref<L.Map | null>(null);
const markers = ref<L.Marker[]>([]);
const items = ref<ItemPreviewType[]>([]);

/**
 * Initializes the Leaflet map with proper configuration
 *
 * This function sets up the map with either the default or user-provided coordinates,
 * adds the OpenStreetMap tile layer, and triggers the loading of item markers.
 */
function initializeMap() {
  if (!mapContainer.value) return;

  // Set default values if props not provided
  // These coordinates default to Bergen, Norway if not specified
  const latitude = props.initialLatitude ?? 60.39;
  const longitude = props.initialLongitude ?? 5.32;
  const zoom = props.initialZoom ?? 7;

  // Create Leaflet map instance with the specified view
  map.value = L.map(mapContainer.value).setView([latitude, longitude], zoom);

  // Add the OpenStreetMap tile layer with proper attribution
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
  }).addTo(map.value as L.Map);

  // Load items with coordinates to display as markers
  loadItemsWithCoordinates();
}

/**
 * Fetches items with location data and creates map markers
 *
 * This function handles the entire marker lifecycle:
 * 1. Fetches items from the ItemService based on category selection
 * 2. Clears any existing markers from the map
 * 3. Creates new markers for all items that have valid coordinates
 * 4. Configures each marker with a popup containing item details and image
 */
async function loadItemsWithCoordinates() {
  try {
    // Fetch items based on category selection
    let fetchedItems: ItemPreviewType[] | undefined; // Allow undefined from fetch potentially
    if (props.categoryId) {
      fetchedItems = await fetchPreviewItemsByCategoryId(props.categoryId);
    } else {
      fetchedItems = await fetchPreviewItems();
    }
    // Ensure items.value is an array, even if fetch returned undefined or null
    items.value = Array.isArray(fetchedItems) ? fetchedItems : [];

    // Clear existing markers
    if (map.value) {
      markers.value.forEach(marker => marker.remove());
      markers.value = [];
    }

    // --- FIX: Check if items.value is actually an array ---
    if (Array.isArray(items.value)) { // <-- Add this check
      items.value.forEach(item => {
        if (item && item.id != null && item.latitude != null && item.longitude != null && map.value) {
          const imageUrlContent = item.imageUrl
            ? `<img src="${item.imageUrl}" alt="${item.title || 'Item image'}" style="max-width: 100px; max-height: 100px; display: block; margin: 5px auto;">`
            : '';

          const itemDetailPath = `/item-detail/${item.id}`;
          const priceString = item.price != null ? `${item.price} kr` : '';
          const linkText = `${item.title || 'View Details'} ${priceString ? '- ' + priceString : ''}`.trim();

          // Create popup content with item picture, title, price and link
          const popupContent = `
            <div class="popup-content" style="text-align: center; padding: 5px;">
                ${imageUrlContent}
                <a href="${itemDetailPath}" target="_blank" rel="noopener noreferrer" style="display: block; margin-top: 8px; text-decoration: none; color: #007bff; font-weight: bold;">
                ${linkText} </a>
            </div>
            `;
          const marker = L.marker([item.latitude, item.longitude])
          .addTo(map.value as L.Map)
          .bindPopup(popupContent);

          markers.value.push(marker);
        }
      });
    } // <-- End of added check
  } catch (error) {
    console.error('Failed to load items for map:', error);
  }
}

/**
 * Watch for changes in coordinate props to update the map view
 *
 * This watcher enables the parent component to programmatically change
 * the map's view location and zoom level after initial rendering.
 */
watch(() => [props.initialLatitude, props.initialLongitude, props.initialZoom],
  ([newLat, newLng, newZoom]) => {
    if (map.value && newLat && newLng) {
      // Update the map view while preserving current zoom if not specified
      map.value.setView([newLat, newLng], newZoom || map.value.getZoom());
    }
  }
);

/**
 * Watch for changes in category selection to refresh markers
 */
watch(() => props.categoryId, () => {
  loadItemsWithCoordinates();
});

/**
 * Set up the map and event listeners when the component is mounted
 *
 * This lifecycle hook handles:
 * 1. Initial map creation
 * 2. Setting up a resize handler for responsive behavior
 * 3. Cleanup of event listeners when the component is unmounted
 */
onMounted(() => {
  // Create the map with initial settings
  initializeMap();

  // Define resize handler to ensure map dimensions stay correct when container changes
  function handleResize() {
    if (map.value) {
      // Forces leaflet to recalculate dimensions and update the display
      map.value.invalidateSize();
    }
  }

  // Resize listener for responsive behavior
  window.addEventListener('resize', handleResize);

  // Return cleanup function to prevent memory leaks when component is unmounted
  return () => {
    window.removeEventListener('resize', handleResize);
  };
});
</script>

<template>
  <!-- Map container with dynamic sizing from props -->
  <div
    ref="mapContainer"
    class="map-container"
    :style="{ height: height || '800px', width: width || '100%' }"
  ></div>
</template>

<style scoped>
/* Styling for the map container */
.map-container {
  min-height: 800px; /* Ensures a minimum height even on small screens */
  border-radius: 8px; /* Rounded corners for better aesthetics */
  overflow: hidden; /* Prevents content from overflowing the rounded corners */
  border: 1px solid #ddd; /* Light border to define the map area */
}
</style>
