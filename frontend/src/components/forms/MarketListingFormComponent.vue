<template>
  <div class="listing-form">
    <form @submit.prevent="submitListing" class="form-container">
      <h1>{{ $t('APP_LISTING_CREATE_NEW') }}</h1>
      <hr class="divider" />
      <!-- Headline -->
      <TextInput
        id="headline"
        :label="$t('APP_LISTING_CREATE_NEW_HEADER_LABEL')"
        v-model="listing.headline"
        required
        :placeholder="$t('APP_LISTING_CREATE_NEW_HEADER_PLACEHOLDER')"
        class="thumbnail thumbnail--full-width"
      />

      <!-- Description -->
      <TextInput
        id="description"
        :label="$t('APP_LISTING_CREATE_NEW_DESCRIPTION_LABEL')"
        v-model="listing.description"
        required
        :placeholder="$t('APP_LISTING_CREATE_NEW_DESCRIPTION_PLACEHOLDER')"
        class="thumbnail thumbnail--full-width"
        multiline
        :rows="3"
      />

      <!-- Category -->
      <SelectInput
        id="category"
        :label="$t('APP_LISTING_CREATE_NEW_CATEGORY_LABEL')"
        v-model="listing.category"
        :options="categoryLabels"
        required
        :placeholder="$t('APP_LISTING_CREATE_NEW_CATEGORY_PLACEHOLDER')"
        class="thumbnail thumbnail--full-width"
      />

      <!-- Price -->
      <TextInput
        id="price"
        :label="$t('APP_LISTING_CREATE_NEW_PRICE_LABEL')"
        v-model="listing.price"
        required
        :placeholder="$t('APP_LISTING_CREATE_NEW_PRICE_PLACEHOLDER')"
        class="thumbnail thumbnail--half-width"
      />

      <!-- Postal Code -->
      <TextInput
        id="postalCode"
        :label="$t('APP_LISTING_CREATE_NEW_POSTAL_CODE_LABEL')"
        v-model="listing.postalCode"
        required
        :placeholder="$t('APP_LISTING_CREATE_NEW_POSTAL_CODE_PLACEHOLDER')"
        class="thumbnail thumbnail--quarter-width"
      />

      <!-- Images -->
      <FileInput
        id="images"
        :label="$t('APP_LISTING_CREATE_NEW_IMAGES_LABEL')"
        @update:files="handleImageUpload"
        multiple
        class="thumbnail thumbnail--full-width"
      />

      <button type="submit" class="button">{{ $t('APP_LISTING_CREATE_NEW_SUBMIT_BUTTON') }}</button>
    </form>
  </div>
</template>

<script lang="ts">
import axios from 'axios';
import { ref, onMounted } from 'vue';
import TextInput from '@/components/input/TextInput.vue';
import SelectInput from '@/components/input/SelectInput.vue';
import FileInput from '@/components/input/FileInput.vue';
import { fetchCategories } from '@/services/CategoryService.ts';

export default {
  components: {
    TextInput,
    SelectInput,
    FileInput,
  },
  setup() {
    const listing = ref({
      headline: '',
      description: '',
      category: '',
      price: '',
      postalCode: '',
      images: [] as File[],
    });
    const categories = ref<{ id: string, label: string, icon: string }[]>([]);
    const categoryLabels = ref<string[]>([]);

    async function loadCategories() {
      categories.value = await fetchCategories();
      categoryLabels.value = categories.value.map(category => category.label);
    }

    onMounted(loadCategories);

    function handleImageUpload(files: File[]) {
      listing.value.images = files;
    }

    async function submitListing() {
      const formData = new FormData();
      formData.append('headline', listing.value.headline);
      formData.append('description', listing.value.description);
      formData.append('category', listing.value.category);
      formData.append('price', listing.value.price);
      formData.append('postalCode', listing.value.postalCode);
      listing.value.images.forEach((file, index) => {
        formData.append(`images[${index}]`, file);
      });

      try {
        const response = await axios.post('/api/listings', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
        console.log('Listing Submitted:', response.data);
        resetForm();
      } catch (error) {
        console.error('Error submitting listing:', error);
      }
    }

    function resetForm() {
      listing.value.headline = '';
      listing.value.description = '';
      listing.value.category = '';
      listing.value.price = '';
      listing.value.postalCode = '';
      listing.value.images = [];
    }

    return {
      listing,
      categoryLabels,
      handleImageUpload,
      submitListing,
    };
  },
};
</script>

<style>
@import '@/assets/styles/responsiveStyles.css'; /* This will still be global */
</style>

<style scoped>
.listing-form {
  justify-content: center;
  align-items: center;
}
.form-container {
  display: flex;
  align-items: center;
  flex-direction: column; /* Stack items vertically */
  flex-wrap: wrap;
  gap: 10px;
}
.divider {
  width: 100%;
  border: 1px solid #ccc;
  margin: 10px 0;
}
</style>
