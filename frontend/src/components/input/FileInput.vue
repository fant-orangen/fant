<template>
  <div class="file-upload">
    <label :for="id" class="upload-label">{{ label }}</label>

    <div class="upload-area">
      <label :for="id" class="upload-button">
        <span class="button-icon">+</span>
        {{ multiple ? $t('APP_LISTING_CREATE_NEW_IMAGES_LABEL') : $t('APP_LISTING_CREATE_NEW_IMAGES_LABEL') }}
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
      <div
        v-for="(src, index) in previews"
        :key="index"
        class="preview-image"
      >
        <img :src="src" :alt="'Preview ' + (index + 1)" />
        <button type="button" class="remove-btn" @click="removeImage(index)">✕</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

const props = defineProps<{
  id: string;
  label: string;
  multiple?: boolean;
  initialUrls?: string[];
}>();


const emit = defineEmits<{
  (e: 'update:files', files: File[]): void;
}>();


const existingImageUrls = ref<string[]>(props.initialUrls || []);
const files = ref<File[]>([]);
const previews = ref<string[]>([]);

onMounted(() => {
  if (existingImageUrls.value.length > 0) {
    previews.value.push(...existingImageUrls.value);
  }
});

function handleFileChange(event: Event) {
  const target = event.target as HTMLInputElement;
  if (target.files) {
    const newFiles = Array.from(target.files);
    files.value = props.multiple ? [...files.value, ...newFiles] : newFiles;

    // Generate previews
    newFiles.forEach(file => {
      previews.value.push(URL.createObjectURL(file));
    });

    emit('update:files', files.value);
  }
}

function removeImage(index: number) {
  const preview = previews.value[index];

  if (existingImageUrls.value.includes(preview)) {
    existingImageUrls.value.splice(existingImageUrls.value.indexOf(preview), 1);
  } else {
    const fileIndex = files.value.findIndex((file) =>
      URL.createObjectURL(file) === preview
    );
    if (fileIndex > -1) files.value.splice(fileIndex, 1);
  }
  // Revoke the object URL
  URL.revokeObjectURL(previews.value[index]);

  // Remove the file and preview at that index
  files.value.splice(index, 1);
  previews.value.splice(index, 1);

  emit('update:files', files.value);
}
</script>

<style scoped>
.file-upload {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  font-family: inherit;
  width: 100%;
}

.upload-label {
  display: block;
  font-weight: 500;
  color: #2c3e50;
  font-size: 0.9rem;
}

.upload-area {
  padding: 1.5rem;
  border: 2px dashed #d1d5db;
  border-radius: 6px;
  background-color: #f9fafb;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.2s ease;
}

.upload-area:hover {
  border-color: #3498db;
}

.upload-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background-color: #3498db;
  color: white;
  font-weight: 500;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: background 0.3s ease;
  box-shadow: 0 2px 4px rgba(52, 152, 219, 0.2);
}

.button-icon {
  font-size: 1.2rem;
  font-weight: bold;
}

.upload-button:hover {
  background-color: #2980b9;
}

.hidden-input {
  position: absolute;
  top: 0;
  left: 0;
  opacity: 0;
  height: 100%;
  width: 100%;
  cursor: pointer;
}

.preview-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 1rem;
  margin-top: 0.5rem;
  max-width: 100%;
  overflow-x: auto;
}

.preview-image {
  position: relative;
  aspect-ratio: 1;
  height: 120px;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12);
  border: 1px solid #e5e7eb;
  background-color: #fff;
}

.preview-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  background-color: rgba(0, 0, 0, 0.6);
  border: none;
  color: white;
  font-size: 0.75rem;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  transition: background-color 0.2s;
}

.remove-btn:hover {
  background-color: rgba(52, 152, 219, 0.9);
}
</style>
