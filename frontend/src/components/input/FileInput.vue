<template>
  <div class="file-upload">
    <label :for="id" class="upload-label">{{ label }}</label>

    <div class="upload-area">
      <label :for="id" class="upload-button">
        <span class="button-icon">+</span>
        {{ multiple ? $t('APP_LISTING_CREATE_NEW_IMAGE_PLACEHOLDER') : $t('APP_LISTING_CREATE_NEW_IMAGES_UPLOAD_LABEL') }}
        <input
          type="file"
          :id="id"
          class="hidden-input"
          @change="handleFileChange"
          :multiple="multiple"
          accept="image/*"
        />
      </label>
    </div>

    <div v-if="previews.length" class="preview-container">
      <div v-for="(src, index) in previews" :key="index" class="preview-image">
        <img :src="src" :alt="'Preview ' + (index + 1)" />
        <button type="button" class="remove-btn" @click="removeImage(index)">✕</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * @fileoverview FileInput component for image uploads with previews.
 * <p>This component provides functionality for:</p>
 * <ul>
 *   <li>Single or multiple image file uploads</li>
 *   <li>Displaying preview thumbnails of selected images</li>
 *   <li>Removing selected images before submission</li>
 *   <li>Supporting existing images from server</li>
 *   <li>Emitting update events with files and URLs</li>
 * </ul>
 */
import { ref, onMounted, watch } from 'vue';

/**
 * Component props definition
 */
const props = defineProps<{

  /**
   * HTML ID for the input element
   * @type {string}
   */
  id: string;

  /**
   * Label text for the upload input
   * @type {string}
   */
  label: string;

  /**
   * Whether multiple file selection is allowed
   * @type {boolean}
   * @default false
   */
  multiple?: boolean;

  /**
   * Initial image URLs to display (typically from server)
   * @type {string[]}
   */
  initialUrls?: string[];
}>();

/**
 * Emitted events definition
 */
const emit = defineEmits<{
  /**
   * Update event for selected images
   * @param {string} e - Event name
   * @param {Object} payload - Contains files and existing URLs
   * @param {File[]} payload.files - Selected File objects
   * @param {string[]} payload.existingUrls - Existing image URLs
   */
  (e: 'update:images', payload: { files: File[]; existingUrls: string[] }): void;
}>();

/**
 * Currently selected files
 * @type {Ref<File[]>}
 */
const files = ref<File[]>([]);

/**
 * Existing image URLs from server
 * @type {Ref<string[]>}
 */
const existingImageUrls = ref<string[]>(props.initialUrls || []);


/**
 * Preview URLs for all images (both existing and newly selected)
 * @type {Ref<string[]>}
 */
const previews = ref<string[]>([]);

/**
 * Initialize preview URLs on component mount
 */
onMounted(() => {
  previews.value = [...existingImageUrls.value];
});

/**
 * Watch for changes to files or existing URLs and emit update event
 */
watch([files, existingImageUrls], () => {
  emit('update:images', {
    files: files.value,
    existingUrls: existingImageUrls.value
  });
});

/**
 * Handles file selection from file input
 * <p>Creates object URLs for previews and updates files array</p>
 * @param {Event} event - File input change event
 */
function handleFileChange(event: Event) {
  const input = event.target as HTMLInputElement;
  if (input.files) {
    const newFiles = Array.from(input.files);
    files.value = props.multiple ? [...files.value, ...newFiles] : newFiles;

    newFiles.forEach(file => {
      previews.value.push(URL.createObjectURL(file));
    });
  }
}

/**
 * Removes an image from selection
 * <p>Handles both existing URLs and newly selected files</p>
 * <p>Revokes object URLs for memory management</p>
 * @param {number} index - Index of image to remove in previews array
 */
function removeImage(index: number) {
  const preview = previews.value[index];

  // Check if this is an existing image URL (from the server)
  const isExistingUrl = existingImageUrls.value.includes(preview);

  if (isExistingUrl) {
    // Remove from existing URLs array
    existingImageUrls.value = existingImageUrls.value.filter(url => url !== preview);
  } else {
    // Find the corresponding file and remove it
    const fileIndex = files.value.findIndex(
      (_, i) => previews.value[index] === URL.createObjectURL(files.value[i])
    );

    if (fileIndex !== -1) {
      files.value.splice(fileIndex, 1);
    }
    URL.revokeObjectURL(preview);
  }

  // Remove from previews array
  previews.value.splice(index, 1);

  // Emit update event
  emit('update:images', {
    files: files.value,
    existingUrls: existingImageUrls.value
  });
}
</script>

<style scoped>
</style>
