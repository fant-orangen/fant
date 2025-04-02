<template>
  <div class="item-preview" @click="handleClick">
    <div class="image-container">
      <img :src="item.imageUrl" :alt="item.title" class="item-image" />
      <HeartIcon class="heart-icon"/>
      <div class="price-overlay">
        <p class="item-price">{{ item.price + " kr" }}</p>
      </div>
    </div>
    <h2 class="item-title">{{ item.title }}</h2>
  </div>
</template>

<script setup lang="ts">
import { defineProps } from 'vue';
import type { ItemPreviewType, ItemDetailsType } from '@/models/Item';
import HeartIcon from '@/components/toggle/HeartIcon.vue'; // Ensure the correct path
import { fetchItem } from '@/services/ItemService.ts';
import router from "@/router";


const props = defineProps<{ item: ItemPreviewType }>();
async function handleClick() {
  try {
    const itemDetails: ItemDetailsType = await fetchItem(props.item.id);
    console.log('Item details:', itemDetails);
    router.push({ name: 'item-detail', params: { id: props.item.id } });
  } catch (error) {
    console.error('Error fetching item details:', error);
  }
}
</script>

<style scoped>
.item-preview {
  border: 1px solid #ccc;
  padding: 10px;
  margin: 10px;
  text-align: center;
  position: relative;
  border-radius: 10px;
  cursor: pointer;
}

.image-container {
  position: relative;
  width: 100%;
  padding-top: 100%; /* This creates a square container */
  overflow: hidden; /* Hide overflow to ensure the image cuts */
}

.item-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover; /* Ensure the image covers the container without stretching */
}

.price-overlay {
  position: absolute;
  bottom: 0;
  width: 100%;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  text-align: right;
  padding: 5px;
}

.item-title {
  font-size: 1.2em;
  margin: 10px 0;
}
.heart-icon {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 1; /* Ensure the heart icon is in front of the image */
}

.item-price {
  margin: 0;
}
</style>
