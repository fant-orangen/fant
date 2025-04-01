<template>
  <div class="gallery-container">
    <button class="nav-button left" @click="prevPage" :disabled="currentPage === 0">&#9664;</button>
    <div class="gallery">
      <img v-for="(image, index) in paginatedImages" :key="index" :src="image" :alt="`Image ${index + 1}`" class="gallery-image" />
    </div>
    <button class="nav-button right" @click="nextPage" :disabled="currentPage === totalPages - 1">&#9654;</button>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, defineProps } from 'vue';

const props = defineProps<{ imageUrls: string[] }>();
const currentPage = ref(0);
const imagesPerPage = 1;

const totalPages = computed(() => Math.ceil(props.imageUrls.length / imagesPerPage));
const paginatedImages = computed(() => {
  const start = currentPage.value * imagesPerPage;
  return props.imageUrls.slice(start, start + imagesPerPage);
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
  display: flex;
}

.nav-button {
  background: none;
  border: none;
  font-size: 2rem;
  cursor: pointer;
  z-index: 1;
}

.nav-button.left {
  margin-right: 1vw;
}

.nav-button.right {
  margin-left: 1vw;
}
</style>
