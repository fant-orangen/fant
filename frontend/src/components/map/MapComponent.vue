<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';
import { fetchPagedPreviewItems, fetchPagedPreviewItemsByCategory } from '@/services/ItemService';
import type { ItemPreviewType, PaginatedItemPreviewResponse } from '@/models/Item';
import type { MapComponentProps } from '@/models/MapComponent.ts';

const props = defineProps<MapComponentProps & {
  categoryId?: string | null;
}>();

const mapContainer = ref<HTMLElement | null>(null);
const map = ref<L.Map | null>(null);
const markers = ref<L.Marker[]>([]);
const items = ref<ItemPreviewType[]>([]);
const currentPage = ref(0);
const pageSize = 50; // Only items with coordinates will be shown, so fetch a bunch

function initializeMap() {
  if (!mapContainer.value) return;
  const latitude = props.initialLatitude ?? 60.39;
  const longitude = props.initialLongitude ?? 5.32;
  const zoom = props.initialZoom ?? 7;
  map.value = L.map(mapContainer.value).setView([latitude, longitude], zoom);

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
  }).addTo(map.value as L.Map);

  loadItemsWithCoordinates();
}

async function loadItemsWithCoordinates() {
  try {
    let allItems: ItemPreviewType[] = [];
    let hasMore = true;
    let page = 0;

    while (hasMore) {
      let response: PaginatedItemPreviewResponse;
      if (props.categoryId) {
        response = await fetchPagedPreviewItemsByCategory(props.categoryId, page, pageSize);
      } else {
        response = await fetchPagedPreviewItems(page, pageSize);
      }
      allItems.push(...response.content);
      hasMore = !response.last;
      page++;
    }

    items.value = allItems;

    if (map.value) {
      markers.value.forEach(marker => marker.remove());
      markers.value = [];
    }

    if (Array.isArray(items.value)) {
      items.value.forEach(item => {
        if (item && item.id != null && item.latitude != null && item.longitude != null && map.value) {
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
          .addTo(map.value as L.Map)
          .bindPopup(popupContent);

          markers.value.push(marker);
        }
      });
    }
  } catch (error) {
    console.error('Failed to load items for map:', error);
  }
}

watch(() => [props.initialLatitude, props.initialLongitude, props.initialZoom],
  ([newLat, newLng, newZoom]) => {
    if (map.value && newLat && newLng) {
      map.value.setView([newLat, newLng], newZoom || map.value.getZoom());
    }
  }
);

watch(() => props.categoryId, () => {
  loadItemsWithCoordinates();
});

onMounted(() => {
  initializeMap();
  function handleResize() {
    if (map.value) {
      map.value.invalidateSize();
    }
  }
  window.addEventListener('resize', handleResize);
  return () => {
    window.removeEventListener('resize', handleResize);
  };
});
</script>

<template>
  <div
    ref="mapContainer"
    class="map-container"
    :style="{ height: height || '800px', width: width || '100%' }"
  ></div>
</template>

<style scoped>
.map-container {
  min-height: 800px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ddd;
}
</style>
