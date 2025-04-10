<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';

const props = defineProps<{
  id: string;
  label: string;
  multiple?: boolean;
  initialUrls?: string[];
}>();

const emit = defineEmits<{
  (e: 'update:images', payload: { files: File[]; existingUrls: string[] }): void;
}>();

const files = ref<File[]>([]);
const existingImageUrls = ref<string[]>(props.initialUrls || []);
const previews = ref<string[]>([]);

// Generate previews
onMounted(() => {
  previews.value = [...existingImageUrls.value];
});

watch([files, existingImageUrls], () => {
  emit('update:images', {
    files: files.value,
    existingUrls: existingImageUrls.value
  });
});

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
<style scoped>
</style>
