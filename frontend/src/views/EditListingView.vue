<template>
  <div class="edit-listing-view">
    <div v-if="loading" class="loading">Loading item data...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else>
      <EditAddItem
        :existingItem="itemData"
        :isEditMode="true"
        :onSubmit="handleSubmit"
        @success="onSuccess"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * Edit Listing View component.
 *
 * This component handles the editing of existing item listings.
 * It fetches the item data and renders a form for users to update their listings.
 *
 * Features:
 * - Fetches and populates existing item data
 * - Converts item details format for form compatibility
 * - Handles form submission and updates via API
 * - Shows loading and error states
 * - Navigates user back to listings after successful update
 *
 * @component EditListingView
 * @requires vue
 * @requires vue-router
 * @requires @/components/item/EditAddItem.vue
 * @requires @/services/ItemService
 * @requires @/services/CategoryService
 * @requires @/models/Item
 * @displayName EditListingView
 */
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import EditAddItem from "@/components/item/EditAddItem.vue";
import { updateItem, fetchItem } from "@/services/ItemService";
import type { CreateItemType, ItemDetailsType } from '@/models/Item';
import {fetchCategories} from "@/services/CategoryService.ts";

const route = useRoute();
const router = useRouter();
const itemId = ref<number | null>(null);
const itemData = ref<Partial<CreateItemType> | undefined>(undefined);
const loading = ref(true);
const error = ref('');

onMounted(async () => {
  // Extract itemId from the route
  const id = route.params.itemId;
  if (!id) {
    error.value = 'No item ID provided';
    loading.value = false;
    return;
  }

  itemId.value = Number(id);

  try {
    // Fetch the item details
    const item = await fetchItem(itemId.value);
    itemData.value =  await convertItemDetailsToCreateItem(item);
  } catch (err) {
    console.error('Error loading item:', err);
    error.value = 'Failed to load item details';
  } finally {
    loading.value = false;
  }
});

/**
 * Submits the updated item data to the API.
 *
 * @param {CreateItemType} updatedItem - The updated item data from the form
 * @returns {Promise<number>} The ID of the updated item
 * @throws {Error} If update fails or item ID is missing
 */
async function handleSubmit(updatedItem: CreateItemType): Promise<number> {
  if (!itemId.value) {
    throw new Error('Item ID is missing');
  }

  try {
    await updateItem(itemId.value, updatedItem);
    return itemId.value;
  } catch (error) {
    console.error('Error updating item:', error);
    throw error;
  }
}

/**
 * Converts the ItemDetailsType received from API to CreateItemType needed for the form.
 * Matches the category name to its corresponding ID.
 *
 * @param {ItemDetailsType} item - The item details from the API
 * @returns {Promise<CreateItemType>} The converted item data compatible with the form
 */
async function convertItemDetailsToCreateItem(item: ItemDetailsType): Promise<CreateItemType> {
  const categories = await fetchCategories();
  const matchedCategoryIndex = categories.findIndex((c) => c.name === item.category);
  return {
    categoryId: matchedCategoryIndex !== -1 ? matchedCategoryIndex + 1 : 0, // Sequential ID
    briefDescription: item.title,
    fullDescription: item.description,
    price: item.price,
    latitude: item.latitude,
    longitude: item.longitude,
    images: item.imageUrls, // Explicitly set to null until skybased is done.
  };
}

/**
 * Handles successful item update by redirecting to the user's listings page.
 *
 * @returns {void}
 */
function onSuccess() {
  router.push(`/profile/listings`);
}
</script>

<style scoped>
.edit-listing-view {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.loading, .error {
  padding: 20px;
  text-align: center;
}

.error {
  color: #e74c3c;
}
</style>
