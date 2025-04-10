<template>
  <div class="listing-form-container">
    <form @submit.prevent="handleSubmit" class="listing-form">
      <h1 class="form-title">
        {{ isEditMode ? $t('APP_LISTING_EDIT_HEADER') : $t('APP_LISTING_CREATE_NEW') }}
      </h1>
      <div class="form-divider"></div>

      <!-- Title -->
      <TextInput
        id="briefDescription"
        :label="$t('APP_LISTING_CREATE_NEW_HEADER_LABEL')"
        v-model="formData.briefDescription"
        required
        :placeholder="$t('APP_LISTING_CREATE_NEW_HEADER_PLACEHOLDER')"
        class="form-field"
      />

      <!-- Description -->
      <TextInput
        id="fullDescription"
        :label="$t('APP_LISTING_CREATE_NEW_DESCRIPTION_LABEL')"
        v-model="formData.fullDescription"
        required
        :placeholder="$t('APP_LISTING_CREATE_NEW_DESCRIPTION_PLACEHOLDER')"
        multiline
        :rows="3"
        class="form-field"
      />

      <div class="form-row">
        <!-- Category -->
        <SelectInput
          id="category"
          :label="$t('APP_LISTING_CREATE_NEW_CATEGORY_LABEL')"
          v-model="selectedCategoryName"
          :options="categoryOptions"
          required
          :placeholder="$t('APP_LISTING_CREATE_NEW_CATEGORY_PLACEHOLDER')"
          class="form-field-half"
        />

        <!-- Price -->
        <TextInput
          id="price"
          :label="$t('APP_LISTING_CREATE_NEW_PRICE_LABEL')"
          v-model="priceInput"
          type="number"
          required
          :placeholder="$t('APP_LISTING_CREATE_NEW_PRICE_PLACEHOLDER')"
          class="form-field-half"
        />
      </div>

      <!-- Location -->
      <div class="form-field">
        <label class="location-label">{{ $t('APP_LISTING_CREATE_NEW_LOCATION') }}</label>

        <div class="location-toggle">
          <button type="button" :class="['toggle-btn', isMapMode ? 'active' : '']" @click="isMapMode = true">{{ $t('MAP') }}</button>
          <button type="button" :class="['toggle-btn', !isMapMode ? 'active' : '']" @click="isMapMode = false">{{ $t('COORDINATES') }}</button>
        </div>

        <div v-if="isMapMode" class="map-mode">
          <ClickSelectMap
            height="300px"
            :initialLatitude="60.39"
            :initialLongitude="5.32"
            :initialZoom="12"
            :selectedLatitude="formData.latitude"
            :selectedLongitude="formData.longitude"
            @location-selected="(coords) => {
              formData.latitude = coords.lat;
              formData.longitude = coords.lng;
            }"
          />
          <div v-if="formData.latitude && formData.longitude" class="location-display">
            Selected: {{ formData.latitude.toFixed(6) }}, {{ formData.longitude.toFixed(6) }}
          </div>
        </div>

        <div v-else class="coordinates-mode">
          <div class="form-row">
            <TextInput
              id="latitude"
              :label="$t('LATITUDE')"
              v-model="latitudeInput"
              type="number"
              step="0.0001"
              placeholder="60.3900"
              class="form-field-half"
            />
            <TextInput
              id="longitude"
              :label="$t('LONGITUDE')"
              v-model="longitudeInput"
              type="number"
              step="0.0001"
              placeholder="5.3200"
              class="form-field-half"
            />
          </div>
        </div>
      </div>

      <!-- Image Upload -->
      <FileInput
        id="images"
        :label="$t('APP_LISTING_CREATE_NEW_IMAGES_UPLOAD_LABEL')"
        :multiple="true"
        :initialUrls="props.existingItem?.images ?? []"
        @update:images="handleFileUpload"
        class="form-field"
      />

      <!-- Submit -->
      <button type="submit" class="submit-button thumbnail--half-width-centered">
        {{ isEditMode ? $t('APP_LISTING_EDIT_SUBMIT') : $t('APP_LISTING_CREATE_NEW_SUBMIT_BUTTON') }}
      </button>
    </form>
  </div>
</template>

<script lang="ts" setup>
import '@/assets/styles/buttons/buttons.css';
import '@/assets/styles/input/input.css'
import '@/assets/styles/responsiveStyles.css';
import { ref, watch, onMounted } from 'vue';
import TextInput from '@/components/input/TextInput.vue';
import SelectInput from '@/components/input/SelectInput.vue';
import FileInput from '@/components/input/FileInput.vue';
import ClickSelectMap from '@/components/map/ClickSelectMap.vue';
import { fetchCategories } from '@/services/CategoryService';
import ImageService from '@/services/ImageService';

import type { CreateItemType } from '@/models/Item';
import type { Category } from '@/models/Category';

const props = defineProps<{
  existingItem?: Partial<CreateItemType>;
  isEditMode?: boolean;
  onSubmit: (item: CreateItemType) => Promise<number>;
}>();

const emit = defineEmits(['success']);

const formData = ref<CreateItemType>({
  categoryId: 0,
  briefDescription: '',
  fullDescription: '',
  price: 0,
  latitude: undefined,
  longitude: undefined,
  images: [],
  ...(props.existingItem || {})
});

const selectedCategoryName = ref('');
const priceInput = ref(formData.value.price.toString());
const latitudeInput = ref(formData.value.latitude?.toString() || '');
const longitudeInput = ref(formData.value.longitude?.toString() || '');
const isMapMode = ref(true);
const keptExistingUrls = ref<string[]>([]);
const newImageFiles = ref<File[]>([]);

const categories = ref<Category[]>([]);
const categoryOptions = ref<string[]>([]);

onMounted(async () => {
  try {
    const cats = await fetchCategories();
    categories.value = cats;
    categoryOptions.value = cats.map(c => c.name);
    if (props.existingItem?.categoryId) {
      const matched = cats.find(c => c.id === props.existingItem!.categoryId);
      if (matched) selectedCategoryName.value = matched.name;
    }
  } catch (err) {
    console.error('Error loading categories', err);
  }
});

// Watchers
watch(selectedCategoryName, (name) => {
  const matched = categories.value.find((c) => c.name === name);
  formData.value.categoryId = matched ? Number(matched.id) : 0;
});
watch(priceInput, (val) => {
  formData.value.price = val ? Number(val) : 0;
});
watch(latitudeInput, (val) => {
  formData.value.latitude = val ? Number(val) : undefined;
});
watch(longitudeInput, (val) => {
  formData.value.longitude = val ? Number(val) : undefined;
});
watch(() => formData.value.latitude, (val) => {
  latitudeInput.value = val !== undefined ? val.toString() : '';
});
watch(() => formData.value.longitude, (val) => {
  longitudeInput.value = val !== undefined ? val.toString() : '';
});

// File handler
function handleFileUpload({ files, existingUrls }: { files: File[], existingUrls: string[] }) {
  newImageFiles.value = files;
  keptExistingUrls.value = existingUrls;
}

async function handleSubmit() {
  try {
    // Create a copy of the form data for submission
    const submissionData = { ...formData.value };

    // Set images to null initially when sending to backend
    submissionData.images = [];

    // Find deleted images by comparing original with kept
    const deletedImages = (props.existingItem?.images || []).filter(
      url => !keptExistingUrls.value.includes(url)
    );

    // Submit the form data to create/update the item
    const itemId = await props.onSubmit(submissionData);

    // Track if we need to update the form data
    let hasChanges = false;

    // Only process deletions if there are images to delete
    if (deletedImages.length > 0) {
      for (const url of deletedImages) {
        try {
          await ImageService.deleteImage(itemId, url);
          hasChanges = true;
        } catch (error) {
          console.error(`Failed to delete image ${url}:`, error);
        }
      }
    }

    // Only upload if there are new files
    if (newImageFiles.value.length > 0) {
      const uploadedUrls = await ImageService.uploadImages(newImageFiles.value, itemId);

      if (Array.isArray(uploadedUrls) && uploadedUrls.length > 0) {

        // Add the new URLs to the kept existing ones
        formData.value.images = [...keptExistingUrls.value, ...uploadedUrls];
      }
    } else if (keptExistingUrls.value.length > 0) {
      // If no new uploads but we still have kept URLs, update form data
      formData.value.images = keptExistingUrls.value;
    }

    alert(props.isEditMode ? 'Item updated!' : 'Item created!');
    emit('success');
  } catch (err) {
    console.error('Submission failed:', err);
    alert('Submission failed.');
  }
}
</script>

<style scoped>
.listing-form-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background-color: var(--color-background-soft, #f8f9fa);
}

.listing-form {
  width: 100%;
  max-width: 800px;
  background: var(--color-background, white);
  border-radius: 12px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.08);
  padding: 35px;
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.form-title {
  color: var(--color-heading, #2c3e50);
  font-size: 26px;
  font-weight: 600;
  margin: 0 0 10px 0;
  text-align: center;
}

.form-divider {
  width: 100%;
  height: 2px;
  background-color: var(--color-border, #e0e0e0);
  margin-bottom: 15px;
}

.form-field {
  width: 100%;
  margin-bottom: 5px;
}

.location-label {
  font-weight: 600;
  margin-bottom: 8px;
  display: block;
  color: var(--color-heading, #2c3e50);
  font-size: 14px;
}

.form-row {
  display: flex;
  gap: 20px;
  width: 100%;
}

.form-field-half {
  flex: 1;
  min-width: 0;
}

.location-toggle {
  display: flex;
  margin-bottom: 15px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid var(--color-border, #e0e0e0);
  width: fit-content;
  position: relative;
  z-index: 10;
}

.toggle-btn {
  padding: 10px 20px;
  background: var(--color-background-mute, #f5f5f5);
  border: none;
  cursor: pointer;
  flex: 1;
  min-width: 110px;
  font-weight: 500;
  transition: all 0.2s ease;
  font-size: 14px;
}

.toggle-btn.active {
  background: var(--vt-c-teal-dark);
  color: white;
}

.toggle-btn:first-child {
  border-right: 1px solid var(--color-border, #e0e0e0);
}

.map-mode, .coordinates-mode {
  margin-top: 10px;
}

.location-display {
  background-color: var(--color-background-mute, #e6f0fa);
  padding: 8px 15px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  color: #3498db;;
  display: inline-flex;
  align-items: center;
  margin-top: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.add-to-list-option input[type="checkbox"] {
  width: 18px;
  height: 18px;
  accent-color: #3498db;
  cursor: pointer;
}

.add-to-list-option label {
  cursor: pointer;
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 15px;
  }

  .listing-form {
    padding: 25px;
    gap: 18px;
  }

  .toggle-btn {
    min-width: 100px;
    padding: 8px 15px;
  }
}
</style>
