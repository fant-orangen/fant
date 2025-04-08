<script setup lang="ts">
import { ref, onMounted, watch, computed, nextTick } from 'vue';
import 'leaflet/dist/leaflet.css';
import 'leaflet-draw/dist/leaflet.draw.css'; // Import leaflet-draw CSS
import L from 'leaflet';
import 'leaflet-draw'; // Import leaflet-draw JS

import { searchItems, type ItemSearchParams } from '@/services/ItemService';
import type { ItemPreviewType } from '@/models/Item';
import type { MapComponentProps } from '@/models/MapComponent.ts';
import { fetchCategories } from '@/services/CategoryService';
import type { Category } from '@/models/Category';

// --- Define Emits ---
// Event emitted when a circle is drawn
const emit = defineEmits<{
  (e: 'update-search-area', payload: { latitude: number; longitude: number; radiusKm: number }): void
}>();

// --- Props ---
const props = defineProps<MapComponentProps & {
  // Filters (passed from MapView)
  categoryId?: string | null;
  searchTerm?: string | null;
  latitude?: number | null;    // Search center latitude (could be user's or drawn circle's)
  longitude?: number | null;   // Search center longitude
  maxDistance?: number | null; // Search radius in km
  minPrice?: number | null;
  maxPrice?: number | null;
  sortOption?: string;
  // Prop to trigger clearing the drawn area from the parent
  clearDrawnAreaTrigger?: number; // Increment this number in parent to clear
}>();

// --- Refs ---
const mapContainer = ref<HTMLElement | null>(null);
const map = ref<L.Map | null>(null);
const markers = ref<L.Marker[]>([]);
const isLoading = ref(false);
const error = ref<string | null>(null);
const categories = ref<Category[]>([]);
const drawnItems = ref<L.FeatureGroup | null>(null); // Ref for the drawn layer group
const drawControl = ref<L.Control.Draw | null>(null); // Ref for draw control

// --- Computed ---
const categoryName = computed(() => {
  if (!props.categoryId) return null;
  const foundCategory = categories.value.find(cat => cat.id?.toString() === props.categoryId);
  return foundCategory ? foundCategory.name : null;
});

const backendSortParam = computed(() => {
  if (!props.sortOption) return null;
  switch (props.sortOption) {
    case 'price_asc': return 'price,asc';
    case 'price_desc': return 'price,desc';
    default: return null;
  }
});

// --- Map Initialization & Drawing Setup ---
function initializeMap() {
  if (!mapContainer.value || map.value) return;
  const initialLat = props.initialLatitude ?? 60.39;
  const initialLng = props.initialLongitude ?? 5.32;
  const initialZoom = props.initialZoom ?? 7;

  map.value = L.map(mapContainer.value).setView([initialLat, initialLng], initialZoom);

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
  }).addTo(map.value);

  // --- Initialize Leaflet.draw ---
  drawnItems.value = new L.FeatureGroup();
  map.value.addLayer(drawnItems.value);

  // Configure Draw Control
  const drawControlOptions: L.Control.DrawConstructorOptions = {
    position: 'topright', // Or 'topleft', etc.
    draw: {
      polygon: false, // Disable polygon
      polyline: false, // Disable polyline
      rectangle: false, // Disable rectangle
      marker: false, // Disable marker
      circlemarker: false, // Disable circle marker
      circle: { // Enable Circle options
        shapeOptions: {
          color: '#007bff', // Example color
          weight: 4,
          opacity: 0.7
        },
        showRadius: true, // Show radius while drawing
        metric: true, // Use metric units (meters)
        feet: false // Don't use feet
      }
    },
    edit: {
      featureGroup: drawnItems.value,
      edit: false,   // Disable edit button
      remove: false  // Disable delete button
    }
  };

  drawControl.value = new L.Control.Draw(drawControlOptions);
  map.value.addControl(drawControl.value);

  // --- Leaflet.draw Event Listeners ---
  map.value.on(L.Draw.Event.CREATED, (event: L.DrawEvents.Created) => {
    const layer = event.layer;
    if (drawnItems.value) {
      drawnItems.value.clearLayers(); // Clear previous drawings
      drawnItems.value.addLayer(layer); // Add the new one
    }

    if (event.layerType === 'circle' && layer instanceof L.Circle) {
      const center = layer.getLatLng();
      const radius = layer.getRadius(); // Radius in meters

      console.log(`Circle drawn: Center=${center}, Radius=${radius}m`);

      // Emit event to parent with circle data
      emit('update-search-area', {
        latitude: center.lat,
        longitude: center.lng,
        radiusKm: radius / 1000 // Convert to KM
      });
    }
  });

  // Optional: Handle edit event if needed
  map.value.on(L.Draw.Event.EDITED, (event: L.DrawEvents.Edited) => {
    event.layers.eachLayer(layer => {
      if (layer instanceof L.Circle) {
        const center = layer.getLatLng();
        const radius = layer.getRadius();
        console.log(`Circle edited: Center=${center}, Radius=${radius}m`);
        emit('update-search-area', {
          latitude: center.lat,
          longitude: center.lng,
          radiusKm: radius / 1000
        });
      }
    });
  });

  // Handle deletion
  map.value.on(L.Draw.Event.DELETED, () => {
    console.log("Drawn search area deleted");
    // Optionally tell the parent to revert to default search
    // This could be done by emitting null values or a specific event
    // For now, the parent should handle the state based on presence of drawn coords
  });

  // Invalidate size when ready
  map.value.whenReady(() => {
    map.value?.invalidateSize();
  });

  loadItemsAndAddMarkers(); // Initial load
}


// --- Data Loading and Marker Logic ---
async function loadItemsAndAddMarkers() {
  if (!map.value) return;
  isLoading.value = true;
  error.value = null;
  console.log("MapComponent: Loading items with props:", props);


  // Clear existing markers (but not the drawn circle)
  markers.value.forEach(marker => marker.remove());
  markers.value = [];

  // Use props directly as they contain either geolocation or drawn circle info
  const params: ItemSearchParams = {
    searchTerm: props.searchTerm || null,
    minPrice: props.minPrice,
    maxPrice: props.maxPrice,
    status: 'ACTIVE',
    categoryName: categoryName.value,
    userLatitude: props.latitude, // Use the latitude from props
    userLongitude: props.longitude, // Use the longitude from props
    maxDistance: (props.latitude !== null && props.longitude !== null) ? props.maxDistance : null, // Use radius from props
    page: 0,
    size: 200, // Fetch a large number for map view
    sort: backendSortParam.value
  };


  try {
    const response = await searchItems(params); // [cite: uploaded:frontend 6/frontend/src/services/ItemService.ts]
    const items = response.content ?? [];

    console.log(`MapComponent: Received ${items.length} items from search.`);

    items.forEach(item => {
      if (item && item.latitude != null && item.longitude != null && map.value) {
        const imageUrlContent = item.imageUrl
          ? `<img src="${item.imageUrl}" alt="${item.title || 'Item image'}" style="max-width: 100px; max-height: 100px; display: block; margin: 5px auto;">`
          : '';

        const itemDetailPath = `/item-detail/${item.id}`;
        const priceString = item.price != null ? `${item.price} kr` : '';
        const linkText = `${item.title || 'View Details'} ${priceString ? '- ' + priceString : ''}`.trim();

        const popupContent = `
          <div class="popup-content" style="text-align: center; padding: 5px;">
              ${imageUrlContent}
              <a href="${itemDetailPath}" target="_blank" rel="noopener noreferrer" style="display: block; margin-top: 8px; text-decoration: none; color: #007bff; font-weight: bold;">
              ${linkText} </a>
          </div>
          `;

        const marker = L.marker([item.latitude, item.longitude])
        .addTo(map.value)
        .bindPopup(popupContent);

        markers.value.push(marker);
      }
    });
    console.log(`MapComponent: Added ${markers.value.length} markers.`);

  } catch (err) {
    console.error('MapComponent: Failed to load items:', err);
    error.value = 'Failed to load items for the map.';
  } finally {
    isLoading.value = false;
  }
}

// --- Watchers ---
// Watch for changes in *final search parameters* props to reload items
// These props are now controlled by MapView based on either geolocation or drawn circle
watch(
  [
    categoryName,
    () => props.searchTerm,
    () => props.minPrice,
    () => props.maxPrice,
    () => props.latitude,    // Watch the final search center lat
    () => props.longitude,   // Watch the final search center lng
    () => props.maxDistance, // Watch the final search radius
    backendSortParam,
  ],
  (newValues, oldValues) => {
    // Avoid reload if only coordinates/distance change slightly without user interaction
    // (e.g., high accuracy geolocation updates) - requires more complex logic if needed.
    // For now, reload on any change.
    console.log("MapComponent: Search parameters changed, reloading items.");
    loadItemsAndAddMarkers();
  },
  { deep: true }
);

// Watcher to clear the drawn circle when triggered by parent
watch(() => props.clearDrawnAreaTrigger, () => {
  if (drawnItems.value) {
    console.log("MapComponent: Clearing drawn search area via trigger.");
    drawnItems.value.clearLayers();
    // Note: Parent (MapView) is responsible for setting its drawnLat/Lng/RadiusKm
    // back to null after incrementing the trigger.
  }
});


// --- Lifecycle Hooks ---
onMounted(async () => {
  // Need to wait for the DOM element to be ready
  await nextTick();
  try {
    categories.value = await fetchCategories(); // [cite: uploaded:frontend 6/frontend/src/services/CategoryService.ts]
    initializeMap();
  } catch (err) {
    console.error("MapComponent: Failed to fetch categories on mount:", err);
    error.value = "Failed to load initial map data.";
  }

  // Resize Observer
  const resizeObserver = new ResizeObserver(() => {
    map.value?.invalidateSize();
  });
  if (mapContainer.value) {
    resizeObserver.observe(mapContainer.value);
  }

  // Cleanup
  return () => {
    if (mapContainer.value) {
      resizeObserver.unobserve(mapContainer.value);
    }
    map.value?.remove();
  };
});

</script>

<template>
  <div class="map-component-wrapper">
    <div v-if="isLoading" class="loading-overlay">Loading map data...</div>
    <div v-if="error" class="error-overlay">{{ error }}</div>
    <div
      ref="mapContainer"
      class="map-container"
      :style="{ height: height || '800px', width: width || '100%' }"
    ></div>
  </div>
</template>

<style scoped>
/* leaflet-draw styles are now handled by the global import */
/* import 'leaflet-draw/dist/leaflet.draw.css'; */ /* Keep the import in <script setup> */

.map-component-wrapper {
  position: relative;
  height: 100%;
  width: 100%;
}
.map-container {
  height: 100%;
  width: 100%;
  min-height: 400px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid var(--color-border, #ddd);
  z-index: 0;
}

.loading-overlay, .error-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(255, 255, 255, 0.7);
  z-index: 1;
  font-size: 1.2em;
  color: #333;
  border-radius: 8px; /* Match container */
  pointer-events: none; /* Allow map interaction through overlay */
}
.error-overlay {
  color: red;
}

/* Ensure Leaflet controls are interactable */
:deep(.leaflet-control-container) {
  z-index: 10 !important;
}

/* Optional: Adjust draw control general appearance if needed */
:deep(.leaflet-draw) {
  /* Example: Add a slight shadow */
  /* box-shadow: 0 1px 5px rgba(0,0,0,0.65); */
  /* border-radius: 4px; */
}

</style>
