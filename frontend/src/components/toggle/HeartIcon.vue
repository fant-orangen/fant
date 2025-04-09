<template>
  <div class="heart-icon" @click="toggleFavorite">
    <img :src="isFavorite ? heartRedIcon : heartIcon" alt="Heart Icon" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import heartIcon from '@/assets/icons/heart.svg';
import heartRedIcon from '@/assets/icons/heart-red.svg';
import { addFavorite, removeFavorite, checkIsFavorite } from '@/services/FavoriteService.ts';
import { useUserStore } from '@/stores/UserStore.ts';
import router from '@/router';
import { AxiosError } from 'axios';

const props = defineProps<{
  itemId: string;
}>();

const isFavorite = ref(false);

onMounted(async () => {
  if (!props.itemId) {
    console.error('ItemId is undefined or empty');
    return;
  }

  try {
    const favoriteStatus = await checkIsFavorite(parseInt(props.itemId));
    isFavorite.value = favoriteStatus;
  } catch (error: unknown) {
    console.error('Error checking favorite status:', error);
  }
});

async function toggleFavorite(event: Event) {
  event.stopPropagation();

  if (!props.itemId) {
    console.error('Cannot toggle favorite: itemId is missing');
    return;
  }

  const userStore = useUserStore();
  if (!userStore.getUserId || userStore.getUserId === '0') {
    // If the user is not logged in, redirect to login.
    await router.push('/login');
    return;
  }

  const itemId = parseInt(props.itemId);
  const desiredState = !isFavorite.value; // The state we want to reach

  try {
    if (isFavorite.value) {
      // Remove favorite
      await removeFavorite(itemId);
      console.log(`Removed favorite: ${itemId}`);
      isFavorite.value = false; // Update state only on success
    } else {
      // Add favorite
      await addFavorite(itemId);
      console.log(`Added favorite: ${itemId}`);
      isFavorite.value = true; // Update state only on success
    }
  } catch (error: unknown) {
    if (error instanceof AxiosError) {
      if (error.response?.status === 400 && error.message.includes('already has favorited')) {
        console.warn('Syncing state: Item was already favorited.');
        isFavorite.value = true;
      } else if (error.response?.status === 404 && !desiredState) {
        console.warn('Syncing state: Item was not favorited to begin with.');
        isFavorite.value = false;
      } else {
        console.error(`Error trying to set favorite status to ${desiredState}:`, error);
      }
    } else if (error instanceof Error) {
      console.error('Error:', error.message);
    } else {
      console.error('An unknown error occurred', error);
    }
  }
}
</script>

<style scoped>
.heart-icon {
  cursor: pointer;
}

.heart-icon img {
  width: 24px;
  height: 24px;
}
</style>
