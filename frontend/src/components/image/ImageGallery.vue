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
/**
 * @fileoverview ImageGallery component for displaying paginated images.
 * <p>This component provides functionality for:</p>
 * <ul>
 *   <li>Displaying a gallery of images with pagination controls</li>
 *   <li>Navigation between images with previous/next buttons</li>
 *   <li>Configurable number of images per page</li>
 *   <li>Responsive display with automatic sizing</li>
 *   <li>Page counter showing current position in gallery</li>
 * </ul>
 */

import { ref, computed } from 'vue';

/**
 * Component props definition
 */
const props = defineProps<{
  /**
   * Array of image URLs to display in the gallery
   * @type {string[]}
   */
  imageUrls: string[],

  /**
   * Number of images to show per page (defaults to 1)
   * @type {number}
   * @default 1
   */
  imagesPerPage?: number
}>();

/**
 * Current active page in the gallery
 * @type {Ref<number>}
 */
const currentPage = ref(0);

/**
 * Computed property for images per page value
 * <p>Returns the provided prop value or defaults to 1</p>
 * @type {ComputedRef<number>}
 */
const imagesPerPage = computed(() => props.imagesPerPage || 1);

/**
 * Calculated total number of pages in the gallery
 * @type {ComputedRef<number>}
 */
const totalPages = computed(() => Math.ceil(props.imageUrls.length / imagesPerPage.value));

/**
 * Current subset of images to display on the active page
 * @type {ComputedRef<string[]>}
 */
const paginatedImages = computed(() => {
  const start = currentPage.value * imagesPerPage.value;
  return props.imageUrls.slice(start, start + imagesPerPage.value);
});

/**
 * Navigates to the previous page of images
 * <p>Will not navigate before the first page</p>
 */
function prevPage() {
  if (currentPage.value > 0) {
    currentPage.value--;
  }
}

/**
 * Navigates to the next page of images
 * <p>Will not navigate past the last page</p>
 */
function nextPage() {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++;
  }
}
</script>

<style scoped>
.gallery-container {
  width: 100%;
  position: relative;
  /* Remove the fixed aspect-ratio */
  max-width: 100%; /* Half width */
  margin: 0 auto; /* Center horizontally */
}

.gallery {
  display: flex;
  position: relative; /* Change from absolute */
  width: 100%;
  min-height: 300px; /* Minimum height */
  max-height: 60vh; /* Maximum height relative to viewport */
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
}

.gallery-image {
  object-fit: contain;
  width: 100%;
  height: 100%;
  max-height: 60vh; /* Ensure image doesn't exceed viewport */
  margin: auto; /* Center the image */
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
