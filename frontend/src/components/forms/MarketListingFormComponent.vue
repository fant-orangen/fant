<template>
  <div class="listing-form-container">
    <form @submit.prevent="submitForm" class="listing-form">
      <h1 class="form-title">Create New Item</h1>
      <div class="form-divider"></div>

      <!-- Title -->
      <TextInput
        id="briefDescription"
        label="Title"
        v-model="formData.briefDescription"
        required
        placeholder="Enter item title"
        class="form-field"
      />

      <!-- Description -->
      <TextInput
        id="fullDescription"
        label="Description"
        v-model="formData.fullDescription"
        required
        placeholder="Enter detailed description"
        multiline
        :rows="3"
        class="form-field"
      />

      <div class="form-row">
        <!-- Category -->
        <SelectInput
          id="category"
          label="Category"
          v-model="selectedCategoryName"
          :options="categoryOptions"
          required
          placeholder="Select a category"
          class="form-field-half"
        />

        <!-- Price -->
        <TextInput
          id="price"
          label="Price"
          v-model="priceInput"
          type="number"
          required
          placeholder="Enter price"
          class="form-field-half"
        />
      </div>

      <!-- Location -->
      <div class="form-field">
        <label class="location-label">Location</label>

        <div class="location-toggle">
          <button type="button" :class="['toggle-btn', isMapMode ? 'active' : '']" @click="isMapMode = true">Map</button>
          <button type="button" :class="['toggle-btn', !isMapMode ? 'active' : '']" @click="isMapMode = false">Coordinates</button>
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
              label="Latitude"
              v-model="latitudeInput"
              type="number"
              step="0.0001"
              placeholder="e.g. 60.3900"
              class="form-field-half"
            />
            <TextInput
              id="longitude"
              label="Longitude"
              v-model="longitudeInput"
              type="number"
              step="0.0001"
              placeholder="e.g. 5.3200"
              class="form-field-half"
            />
          </div>
        </div>
      </div>

      <!-- Image Upload -->
      <FileInput
        id="images"
        label="Upload Images"
        :multiple="true"
        @update:files="handleFileUpload"
        class="form-field"
      />

      <!-- Submit -->
      <button type="submit" class="submit-button">Create Item</button>
    </form>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, watch } from 'vue';
import TextInput from '@/components/input/TextInput.vue';
import SelectInput from '@/components/input/SelectInput.vue';
import FileInput from '@/components/input/FileInput.vue';
import ClickSelectMap from '@/components/map/ClickSelectMap.vue';
import { fetchCategories } from '@/services/CategoryService';
import { createItem } from '@/services/ItemService';
import type { CreateItemType } from '@/models/Item';
import type { Category } from '@/models/Category';

const formData = ref<CreateItemType>({
  categoryId: 0,
  briefDescription: '',
  fullDescription: '',
  price: 0,
  latitude: undefined,
  longitude: undefined,
  images: null,
});

// UI-controlled values
const selectedCategoryName = ref('');
const priceInput = ref('');
const latitudeInput = ref('');
const longitudeInput = ref('');
const isMapMode = ref(true);
const imageFiles = ref<File[]>([]);

// Backend-fetched categories
const categories = ref<Category[]>([]);
const categoryOptions = ref<string[]>([]);

// Load categories from backend
onMounted(async () => {
  try {
    const cats = await fetchCategories();
    categories.value = cats;
    categoryOptions.value = cats.map(cat => cat.name);
  } catch (error) {
    console.error('Failed to fetch categories:', error);
  }
});

// Sync selected name to categoryId
watch(selectedCategoryName, (newName) => {
  const match = categories.value.find((c) => c.name === newName);
  formData.value.categoryId = match ? Number(match.id) : 0;
});

// Sync numeric inputs
watch(priceInput, (val) => {
  formData.value.price = val ? Number(val) : 0;
});
watch(latitudeInput, (val) => {
  formData.value.latitude = val ? Number(val) : undefined;
});
watch(longitudeInput, (val) => {
  formData.value.longitude = val ? Number(val) : undefined;
});

// Reflect back values from formData to inputs (when using map picker)
watch(() => formData.value.latitude, (val) => {
  latitudeInput.value = val !== undefined ? val.toString() : '';
});
watch(() => formData.value.longitude, (val) => {
  longitudeInput.value = val !== undefined ? val.toString() : '';
});

// File upload handler
function handleFileUpload(files: File[]) {
  imageFiles.value = files;
  formData.value.images = null; // or send files separately
}

// Submit handler
async function submitForm() {
  try {
    const payload: CreateItemType = { ...formData.value };
    console.log('Submitting payload:', payload);
    await createItem(payload);
    alert('Item created successfully!');
  } catch (error) {
    console.error('Error creating item:', error);
    alert('Failed to create item.');
  }
}
</script>

<style scoped>
.listing-form-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background-color: #f8f9fa;
}

.listing-form {
  width: 100%;
  max-width: 800px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 30px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-title {
  color: #333;
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 10px 0;
  text-align: center;
}

.form-divider {
  width: 100%;
  height: 1px;
  background-color: #e0e0e0;
  margin-bottom: 15px;
}

.form-field {
  width: 100%;
  margin-bottom: 5px;
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

.location-section {
  margin-top: 5px;
}

.location-label {
  font-weight: 600;
  margin-bottom: 5px;
  display: block;
  color: #333;
}

.location-helper {
  font-size: 14px;
  color: #666;
  margin: 4px 0 10px;
}

.map-container {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 10px;
}

.location-display {
  background-color: #f0f8ff;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  color: #0066cc;
  display: inline-block;
  margin-top: 5px;
}

.location-toggle {
  display: flex;
  margin-bottom: 15px;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #e0e0e0;
  width: fit-content;
  position: relative;
  z-index: 1000; /* Ensure buttons stay above map */
}

.map-container {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 10px;
  position: relative; /* Contain the map */
}

/* Add this new style */
.location-section .leaflet-container {
  z-index: 1; /* Lower z-index than the toggle buttons */
}

.toggle-btn {
  padding: 8px 16px;
  background: #f5f5f5;
  border: none;
  cursor: pointer;
  flex: 1;
  min-width: 100px;
  font-weight: 500;
  transition: all 0.2s;
}

.toggle-btn.active {
  background: #4CAF50;
  color: white;
}

.toggle-btn:first-child {
  border-right: 1px solid #e0e0e0;
}

.map-mode, .coordinates-mode {
  margin-top: 10px;
}

.preview-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.submit-button {
  background-color: #4CAF50;
  color: white;
  font-size: 16px;
  font-weight: 600;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;
  align-self: center;
  margin-top: 10px;
  width: auto;
  min-width: 200px;
}

.submit-button:hover {
  background-color: #45a049;
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 5px;
  }

  .listing-form {
    padding: 20px;
  }
}
</style>
