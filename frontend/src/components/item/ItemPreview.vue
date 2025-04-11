<template>
  <div class="item-preview" @click="handleClick">
    <div class="image-container">
      <img
        :src="item.imageUrl || '/assets/images/placeholderItem.jpg'"
        :alt="item.title"
        class="item-image"
        @error="handleImageError"
      />
      <HeartIcon class="heart-icon" :itemId="props.item.id.toString()" />
      <div class="price-overlay">
        <span class="item-price">{{ item.price + " kr" }}</span>
      </div>
    </div>
    <div class="info-container">
      <h2 class="item-title">{{ item.title }}</h2>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * @fileoverview ItemPreview component for displaying item card in grid layouts.
 * <p>This component provides functionality for:</p>
 * <ul>
 *   <li>Displaying item preview cards with image thumbnails</li>
 *   <li>Interactive price overlay and favorite toggle</li>
 *   <li>Navigation to item detail page on click</li>
 *   <li>Responsive layout with hover effects</li>
 * </ul>
 */
import type { ItemPreviewType } from '@/models/Item';
import HeartIcon from '@/components/item/HeartIcon.vue';
import placeholderImage from '@/assets/images/placeholderItem.jpg';
import router from "@/router"; //

/**
 * Component props definition
 */
const props = defineProps<{
  /**
   * Item data to display in the preview card
   * @type {ItemPreviewType}
   */
  item: ItemPreviewType
}>();

/**
 * Handles click on item preview card
 * <p>Navigates to the detailed view of the item</p>
 */
function handleClick() {
  try {
    router.push({ name: 'item-detail', params: { id: props.item.id } });
  } catch (error) {
    console.error('Error during router push:', error);
  }
}

function handleImageError(event: Event) {
  const imgElement = event.target as HTMLImageElement;
  imgElement.src = placeholderImage;
}
</script>

<style scoped>
.item-preview {
  margin: 10px;
  text-align: left;
  position: relative;
  border-radius: 10px;
  cursor: pointer;
  background-color: #fff;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
}

.item-preview:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 10px rgba(118, 183, 178, 0.7);
}

.image-container {
  position: relative;
  width: 100%;
  padding-top: 100%;
}

.item-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.price-overlay {
  position: absolute;
  bottom: 8px;
  left: 8px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 4px 8px;
  border-radius: 5px;
  font-size: 0.9em;
  font-weight: 600;
}

.info-container {
  padding: 10px 12px 12px;
}

.item-title {
  font-size: 1.1em;
  font-weight: 600;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #333;
}

.heart-icon {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 1;
}

.item-price {
  margin: 0;
}
</style>
