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

// This function converts ItemDetailsType to CreateItemType as needed
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
