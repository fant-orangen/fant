<script setup lang="ts">
import { ref, onMounted, watch, computed, nextTick, onUnmounted } from 'vue';
import 'leaflet/dist/leaflet.css';
import 'leaflet-draw/dist/leaflet.draw.css'; // Import leaflet-draw CSS
import L from 'leaflet';
import 'leaflet-draw'; // Import leaflet-draw JS

import { searchItems, type ItemSearchParams } from '@/services/ItemService'; // [cite: uploaded:frontend 6/frontend/src/services/ItemService.ts]
import type { MapComponentProps } from '@/models/MapComponent';
import { fetchCategories } from '@/services/CategoryService'; // [cite: uploaded:frontend 6/frontend/src/services/CategoryService.ts]
import type { Category } from '@/models/Category';

// --- Define Emits ---
const emit = defineEmits<{
  (e: 'update-search-area', payload: { latitude: number; longitude: number; radiusKm: number }): void
}>();

// --- Props ---
const props = defineProps<MapComponentProps & {
  categoryId?: string | null;
  searchTerm?: string | null;
  latitude?: number | null;    // Search center latitude (could be user's or drawn circle's)
  longitude?: number | null;   // Search center longitude
  maxDistance?: number | null; // Search radius in km
  minPrice?: number | null;
  maxPrice?: number | null;
  sortOption?: string;
  clearDrawnAreaTrigger?: number; // Increment this number in parent to clear
}>();

// --- Refs ---
const mapContainer = ref<HTMLElement | null>(null);
const map = ref<L.Map | null>(null);
const markers = ref<L.Marker[]>([]);
const isLoading = ref(false);
const error = ref<string | null>(null);
const categories = ref<Category[]>([]);

// Ensure that drawnItems is a valid Leaflet FeatureGroup.
const drawnItems = ref<L.FeatureGroup | null>(null);
// Ref for draw control
const drawControl = ref<L.Control.Draw | null>(null);

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
  }).addTo(map.value as L.Map);

  // --- Initialize Leaflet.draw ---
  drawnItems.value = new L.FeatureGroup();
  // Cast drawnItems to any to satisfy TS since Leaflet adds _map at runtime.
  map.value.addLayer(drawnItems.value as any);

  // Configure Draw Control with type assertion on featureGroup.
  const drawControlOptions: L.Control.DrawConstructorOptions = {
    position: 'topright',
    draw: {
      polygon: false,
      polyline: false,
      rectangle: false,
      marker: false,
      circlemarker: false,
      circle: {
        shapeOptions: {
          color: '#007bff',
          weight: 4,
          opacity: 0.7
        },
        showRadius: true,
        metric: true,
        feet: false
      }
    },
    edit: {
      // Cast drawnItems.value to any here to meet the expected type.
      featureGroup: drawnItems.value! as any,
      edit: false,
      remove: false
    }
  };

  drawControl.value = new L.Control.Draw(drawControlOptions);
  map.value.addControl(drawControl.value);

  // --- Leaflet.draw Event Listeners ---
  // For the CREATED event: use a generic event parameter and cast it inside.
  map.value.on(L.Draw.Event.CREATED, (event: any) => {
    const createdEvent = event as L.DrawEvents.Created;
    if (drawnItems.value) {
      drawnItems.value.clearLayers();
      drawnItems.value.addLayer(createdEvent.layer);
    }

    if (createdEvent.layerType === 'circle' && createdEvent.layer instanceof L.Circle) {
      const center = createdEvent.layer.getLatLng();
      const radius = createdEvent.layer.getRadius(); // in meters
      console.log(`Circle drawn: Center=${center}, Radius=${radius}m`);
      emit('update-search-area', {
        latitude: center.lat,
        longitude: center.lng,
        radiusKm: radius / 1000 // convert meters to kilometers
      });
    }
  });

  // For the EDITED event: use a generic event parameter and cast it inside.
  map.value.on(L.Draw.Event.EDITED, (event: any) => {
    const editedEvent = event as L.DrawEvents.Edited;
    editedEvent.layers.eachLayer(layer => {
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

  map.value.on(L.Draw.Event.DELETED, (event: any) => {
    console.log("Drawn search area deleted");
    // Optionally, emit an event to notify parent that the search area has been cleared.
  });

  map.value.whenReady(() => {
    map.value?.invalidateSize();
  });

  loadItemsAndAddMarkers(); // Initial load of markers
}

// --- Data Loading and Marker Logic ---
async function loadItemsAndAddMarkers() {
  if (!map.value) return;
  isLoading.value = true;
  error.value = null;
  console.log("MapComponent: Loading items with props:", props);

  // Remove existing markers (but not the drawn circle).
  markers.value.forEach(marker => marker.remove());
  markers.value = [];

  // Convert null values from props into undefined as needed.
  const params: ItemSearchParams = {
    searchTerm: props.searchTerm ?? undefined,
    minPrice: props.minPrice ?? undefined,
    maxPrice: props.maxPrice ?? undefined,
    status: 'ACTIVE',
    categoryName: categoryName.value ?? undefined,
    userLatitude: props.latitude ?? undefined,
    userLongitude: props.longitude ?? undefined,
    maxDistance: (props.latitude !== null && props.longitude !== null) ? (props.maxDistance ?? undefined) : undefined,
    page: 0,
    size: 200,
    sort: backendSortParam.value ?? undefined
  };

  try {
    const response = await searchItems(params);
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
              ${linkText}</a>
          </div>
          `;

        const marker = L.marker([item.latitude, item.longitude])
        .addTo(map.value as L.Map)
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
// Watch for changes in search parameters to reload items.
watch(
  [
    categoryName,
    () => props.searchTerm,
    () => props.minPrice,
    () => props.maxPrice,
    () => props.latitude,
    () => props.longitude,
    () => props.maxDistance,
    backendSortParam,
  ],
  () => {
    console.log("MapComponent: Search parameters changed, reloading items.");
    loadItemsAndAddMarkers();
  },
  { deep: true }
);

// Watcher to clear the drawn circle when triggered by parent.
watch(() => props.clearDrawnAreaTrigger, () => {
  if (drawnItems.value) {
    console.log("MapComponent: Clearing drawn search area via trigger.");
    drawnItems.value.clearLayers();
  }
});

// --- Lifecycle Hooks ---
onMounted(async () => {
  await nextTick();
  try {
    categories.value = await fetchCategories();
    initializeMap();
  } catch (err) {
    console.error("MapComponent: Failed to fetch categories or initialize map on mount:", err);
    error.value = "Failed to load initial map data.";
  }

  // Resize Observer to handle container size changes.
  let resizeObserver: ResizeObserver | null = null;
  if (mapContainer.value) {
    resizeObserver = new ResizeObserver(() => {
      map.value?.invalidateSize();
    });
    resizeObserver.observe(mapContainer.value);
  }
});

onUnmounted(() => {
  // Cleanup map on component unmount.
  map.value?.remove();
  map.value = null;
  console.log("MapComponent unmounted, map removed.");
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
.map-component-wrapper {
  position: relative;
  height: 100%;
  width: 100%;
}
.map-container {
  min-height: 400px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid var(--color-border, #ddd);
  z-index: 0;
}
.loading-overlay,
.error-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(255, 255, 255, 0.8);
  z-index: 1000;
  font-size: 1.2em;
  color: #333;
  border-radius: 8px;
  pointer-events: none;
  text-align: center;
  padding: 20px;
}
.error-overlay {
  color: red;
  font-weight: bold;
}
:deep(.leaflet-control-container) {
  z-index: 10 !important;
}
:deep(.leaflet-popup-content-wrapper) {
  border-radius: 6px;
  padding: 1px;
}
:deep(.leaflet-popup-content) {
  margin: 0 !important;
  padding: 0;
  line-height: initial;
}
:deep(.popup-content) {
  text-align: center;
  padding: 10px;
  min-width: 150px;
}
:deep(.popup-content img) {
  max-width: 120px;
  max-height: 100px;
  display: block;
  margin: 5px auto 10px auto;
  border-radius: 4px;
}
:deep(.popup-content a) {
  display: block;
  margin-top: 8px;
  text-decoration: none;
  color: #007bff;
  font-weight: bold;
  font-size: 0.95em;
}
:deep(.popup-content a:hover) {
  text-decoration: underline;
}
</style>
