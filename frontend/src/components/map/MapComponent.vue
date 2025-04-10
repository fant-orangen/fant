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

<script setup lang="ts">
/**
 * @fileoverview MapComponent for interactive item location display and area-based searching.
 * <p>This component provides functionality for:</p>
 * <ul>
 *   <li>Interactive map display with item markers</li>
 *   <li>Circle-based area search with drawing tools</li>
 *   <li>Dynamic marker updates based on search parameters</li>
 *   <li>Item previews in popup windows</li>
 *   <li>Integration with search filters</li>
 * </ul>
 */
import { ref, onMounted, watch, computed, nextTick, onUnmounted } from 'vue';
import 'leaflet/dist/leaflet.css';
import 'leaflet-draw/dist/leaflet.draw.css'; // Import leaflet-draw CSS
import L from 'leaflet';
import 'leaflet-draw'; // Import leaflet-draw JS

import { searchItems, type ItemSearchParams } from '@/services/ItemService'; // [cite: uploaded:frontend 6/frontend/src/services/ItemService.ts]
import type { MapComponentProps } from '@/models/MapComponent';
import { fetchCategories } from '@/services/CategoryService'; // [cite: uploaded:frontend 6/frontend/src/services/CategoryService.ts]
import type { Category } from '@/models/Category';

/**
 * Event emitters definition
 */
const emit = defineEmits<{
  /**
   * Emitted when user draws or modifies search area on the map
   * @param {string} e - Event name
   * @param {{ latitude: number; longitude: number; radiusKm: number }} payload - Search area details
   */
  (e: 'update-search-area', payload: { latitude: number; longitude: number; radiusKm: number }): void
}>();

/**
 * Component props definition
 */
const props = defineProps<MapComponentProps & {
  /**
   * Category ID to filter items by
   * @type {string|null}
   */
  categoryId?: string | null;

  /**
   * Search term to filter items by
   * @type {string|null}
   */
  searchTerm?: string | null;

  /**
   * Search center latitude
   * @type {number|null}
   */
  latitude?: number | null;

  /**
   * Search center longitude
   * @type {number|null}
   */
  longitude?: number | null;

  /**
   * Search radius in kilometers
   * @type {number|null}
   */
  maxDistance?: number | null;

  /**
   * Minimum price filter
   * @type {number|null}
   */
  minPrice?: number | null;

  /**
   * Maximum price filter
   * @type {number|null}
   */
  maxPrice?: number | null;

  /**
   * Sort option for item listing
   * @type {string}
   */
  sortOption?: string;

  /**
   * Trigger value to clear drawn area
   * @type {number}
   */
  clearDrawnAreaTrigger?: number; // Increment this number in parent to clear
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
 * Array of marker references for cleanup
 * @type {Ref<L.Marker[]>}
 */
const markers = ref<L.Marker[]>([]);

/**
 * Flag indicating whether data is being loaded
 * @type {Ref<boolean>}
 */
const isLoading = ref(false);

/**
 * Error message to display if loading fails
 * @type {Ref<string|null>}
 */
const error = ref<string | null>(null);

/**
 * Available item categories
 * @type {Ref<Category[]>}
 */
const categories = ref<Category[]>([]);

/**
 * Leaflet feature group for managing drawn items
 * @type {Ref<L.FeatureGroup|null>}
 */
const drawnItems = ref<L.FeatureGroup | null>(null);

/**
 * Draw control for circle-based area search
 * @type {Ref<L.Control.Draw|null>}
 */
const drawControl = ref<L.Control.Draw | null>(null);

/**
 * Resolved category name based on categoryId prop
 * @type {ComputedRef<string|null>}
 */
const categoryName = computed(() => {
  if (!props.categoryId) return null;
  const foundCategory = categories.value.find(cat => cat.id?.toString() === props.categoryId);
  return foundCategory ? foundCategory.name : null;
});

/**
 * Converts UI sort option to backend format
 * @type {ComputedRef<string|null>}
 */
const backendSortParam = computed(() => {
  if (!props.sortOption) return null;
  switch (props.sortOption) {
    case 'price_asc': return 'price,asc';
    case 'price_desc': return 'price,desc';
    default: return null;
  }
});

/**
 * Initializes the map and drawing tools
 * <p>Sets up map view, layers, and drawing controls</p>
 */
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

/**
 * Loads items based on search parameters and adds markers to map
 * <p>Fetches items from API and creates popups with item previews</p>
 */
async function loadItemsAndAddMarkers() {
  if (!map.value) return;
  isLoading.value = true;
  error.value = null;

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
  } catch (err) {
    error.value = 'Failed to load items for the map.';
  } finally {
    isLoading.value = false;
  }
}

/**
 * Watches for changes in search parameters to reload map markers
 * <p>Automatically refreshes markers when any search criteria changes</p>
 */
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
    loadItemsAndAddMarkers();
  },
  { deep: true }
);

/**
 * Watches for clear area trigger from parent component
 * <p>Removes all drawn shapes when triggered</p>
 */
watch(() => props.clearDrawnAreaTrigger, () => {
  if (drawnItems.value) {
    drawnItems.value.clearLayers();
  }
});

/**
 * Initializes component when mounted
 * <p>Loads categories, initializes map, and sets up resize observer</p>
 */
onMounted(async () => {
  await nextTick();
  try {
    categories.value = await fetchCategories();
    initializeMap();
  } catch (err) {
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

/**
 * Cleans up resources when component is unmounted
 * <p>Removes map instance to prevent memory leaks</p>
 */
onUnmounted(() => {
  // Cleanup map on component unmount.
  map.value?.remove();
  map.value = null;
});
</script>

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
