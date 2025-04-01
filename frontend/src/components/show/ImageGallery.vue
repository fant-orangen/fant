<template>
  <div class="gallery-container">
    <div class="gallery">
      <button class="nav-button left" @click="prevPage" :disabled="currentPage === 0">&#9664;</button>
      <img v-for="(image, index) in paginatedImages" :key="index" :src="image" :alt="`Image ${index + 1}`" class="gallery-image" />
      <button class="nav-button right" @click="nextPage" :disabled="currentPage === totalPages - 1">&#9654;</button>
      <div class="page-info">Page {{ currentPage + 1 }} of {{ totalPages }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, defineProps } from 'vue';

const props = defineProps<{ imageUrls: string[], imagesPerPage?: number }>();
const currentPage = ref(0);
const imagesPerPage = computed(() => props.imagesPerPage || 1);

const totalPages = computed(() => Math.ceil(props.imageUrls.length / imagesPerPage.value));
const paginatedImages = computed(() => {
  const start = currentPage.value * imagesPerPage.value;
  return props.imageUrls.slice(start, start + imagesPerPage.value);
});

function prevPage() {
  if (currentPage.value > 0) {
    currentPage.value--;
  }
}

function nextPage() {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++;
  }
}
</script>

<style scoped>
.gallery-container {
  width: 100%;
  aspect-ratio: 1 / 1;
  position: relative;
}

.gallery {
  display: flex;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
}

.gallery-image {
  object-fit: contain;
  width: 100%;
  height: 100%;
}

.nav-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  font-size: 1rem;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 50%;
  z-index: 2;
}

.nav-button.right {
  right: 1px;
}

.nav-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  position: absolute;
  bottom: 0;
  width: 100%;
  text-align: center;
  background: rgba(255, 255, 255, 0.1);
  z-index: 2;
}
</style>
