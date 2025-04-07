<template>
  <div v-if="item" class="item-details">
    <div class="thumbnail--full-width item-detail-container">
      <ImageGallery v-if="item.imageUrls" :imageUrls="item.imageUrls" />
      <div class="details">
        <div class="header">
          <h1>{{ item.title }}</h1>
          <HeartIcon :itemId="item.id.toString()" />
        </div>
        <p><strong>Price:</strong> {{ item.price }} kr</p>
        <button @click="contactSeller" class="button">Contact Seller</button>
        <p>{{ item.description }}</p>
        <p>{{ "Location would have been here." }}</p>
        <p><strong>Contact:</strong> {{ item.contact }}</p>
      </div>
    </div>
  </div>
  <div v-else>
    Loading...
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, defineProps } from 'vue';
import ImageGallery from '@/components/show/ImageGallery.vue';
import HeartIcon from '@/components/toggle/HeartIcon.vue';
import type { ItemDetailsType } from '@/models/Item';
import { fetchItem, recordItemView } from '@/services/ItemService.ts'

const props = defineProps<{ itemId: string | number }>();
const item = ref<ItemDetailsType | null>(null);

onMounted(async () => {
  if (props.itemId) {
    try {
      const fetchedItem = await fetchItem(props.itemId);
      item.value = Array.isArray(fetchedItem) ? fetchedItem[0] : fetchedItem;
      console.log("item value ", item.value);

      // Call the service directly
      const response = await recordItemView(props.itemId); // Record that the user viewed this item
      console.log(`Response from the backend: ${response.status}`);

    } catch (error) {
      console.error('Error fetching item details:', error);
    }
  }
});

function contactSeller() {
  // Implement contact seller functionality
}
</script>

<style scoped>
.item-details {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  text-align: center;
}

.details {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
</style>
