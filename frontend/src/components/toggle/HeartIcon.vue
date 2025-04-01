<template>
  <div class="heart-icon" @click="toggleFavorite">
    <img :src="isFavorite ? heartRedIcon : heartIcon" alt="Heart Icon" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import heartIcon from '@/assets/icons/heart.svg';
import heartRedIcon from '@/assets/icons/heart-red.svg';

const isFavorite = ref(false);

function toggleFavorite() {
  isFavorite.value = !isFavorite.value;
}
</script>

<style scoped>
.heart-icon {
  cursor: pointer;
}

.heart-icon img {
  width: 24px;
  height: 24px;
}
</style>
