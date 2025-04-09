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
import { useUserStore } from '@/stores/UserStore.ts'
import router from '@/router'

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
  } catch (error) {
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
    // Consider redirecting to login instead of register if user exists but isn't logged in
    await router.push('/login');
    return;
  }

  const itemId = parseInt(props.itemId);
  let desiredState = !isFavorite.value; // What we want the state to become

  try {
    if (isFavorite.value) {
      // Try to remove
      await removeFavorite(itemId);
      console.log(`Removed favorite: ${itemId}`);
      isFavorite.value = false; // Update state only on success
    } else {
      // Try to add
      await addFavorite(itemId);
      console.log(`Added favorite: ${itemId}`);
      isFavorite.value = true; // Update state only on success
    }
  } catch (error: any) {
    console.error(`Error trying to set favorite status to ${desiredState}:`, error);

    // Attempt to re-sync state based on common errors
    if (error.response?.status === 400 && error.message?.includes('already has favorited')) {
      console.warn('Syncing state: Item was already favorited.');
      isFavorite.value = true; // Correct the local state if add failed because it exists
    } else if (error.response?.status === 404 && !desiredState) { // If remove failed because it wasn't there
      console.warn('Syncing state: Item was not favorited to begin with.');
      isFavorite.value = false; // Correct the local state if remove failed because it doesn't exist
    }
    // Optionally show a user-friendly error message here
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
