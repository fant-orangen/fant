<template>
  <div class="item-preview">
    <div class="image-container">
      <img :src="item.imageUrl" :alt="item.title" class="item-image" />
      <div class="heart-icon" @click="toggleFavorite">
        <img :src="isFavorite ? heartRedIcon : heartIcon" alt="Heart Icon" />
      </div>
      <div class="price-overlay">
        <p class="item-price">{{ item.price + " kr" }}</p>
      </div>
    </div>
    <h2 class="item-title">{{ item.title }}</h2>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { defineProps } from 'vue';
import type { ItemPreviewType } from '@/models/Item';
import heartIcon from '@/assets/icons/heart.svg';
import heartRedIcon from '@/assets/icons/heart-red.svg';

const props = defineProps<{ item: ItemPreviewType }>();
const isFavorite = ref(false);

function toggleFavorite() {
  isFavorite.value = !isFavorite.value;
}
</script>

<style scoped>
.item-preview {
  border: 1px solid #ccc;
  padding: 10px;
  margin: 10px;
  text-align: center;
  position: relative;
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

.heart-icon {
  position: absolute;
  top: 10px;
  right: 10px;
  cursor: pointer;
}

.heart-icon img {
  width: 24px;
  height: 24px;
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
  color: black;
}

.item-price {
  margin: 0;
}
</style>
