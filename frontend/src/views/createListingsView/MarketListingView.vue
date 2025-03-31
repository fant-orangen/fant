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
      />

      <!-- Category -->
      <SelectInput
        id="category"
        :label="$t('APP_LISTING_CREATE_NEW_CATEGORY_LABEL')"
        v-model="listing.category"
        :options="categories"
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
        class="thumbnail thumbnail--full-width"
      />

      <!-- Postal Code -->
      <TextInput
        id="postalCode"
        :label="$t('APP_LISTING_CREATE_NEW_POSTAL_CODE_LABEL')"
        v-model="listing.postalCode"
        required
        :placeholder="$t('APP_LISTING_CREATE_NEW_POSTAL_CODE_PLACEHOLDER')"
        class="thumbnail thumbnail--full-width"
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
import TextInput from '@/components/input/TextInput.vue';
import SelectInput from '@/components/input/SelectInput.vue';
import FileInput from '@/components/input/FileInput.vue';

export default {
  components: {
    TextInput,
    SelectInput,
    FileInput,
  },
  data() {
    return {
      listing: {
        headline: '',
        description: '',
        category: '',
        price: '',
        postalCode: '',
        images: [] as File[],
      },
      categories: ['Electronics', 'Furniture', 'Vehicles', 'Clothing', 'Real Estate'],
    };
  },
  methods: {
    handleImageUpload(files: File[]) {
      this.listing.images = files;
    },
    async submitListing() {
      const formData = new FormData();
      formData.append('headline', this.listing.headline);
      formData.append('description', this.listing.description);
      formData.append('category', this.listing.category);
      formData.append('price', this.listing.price);
      formData.append('postalCode', this.listing.postalCode);
      this.listing.images.forEach((file, index) => {
        formData.append(`images[${index}]`, file);
      });

      try {
        const response = await axios.post('/api/listings', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
        console.log('Listing Submitted:', response.data);
        this.resetForm();
      } catch (error) {
        console.error('Error submitting listing:', error);
      }
    },
    resetForm() {
      this.listing.headline = '';
      this.listing.description = '';
      this.listing.category = '';
      this.listing.price = '';
      this.listing.postalCode = '';
      this.listing.images = [];
    },
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
