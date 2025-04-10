<template>
  <div class="heart-icon" @click="toggleFavorite">
    <img :src="isFavorite ? heartRedIcon : heartIcon" alt="Heart Icon" />
  </div>
</template>

<script setup lang="ts">
/**
 * @fileoverview HeartIcon component for item favorite toggling.
 * <p>This component provides functionality for:</p>
 * <ul>
 *   <li>Visual indication of favorite status</li>
 *   <li>Adding/removing items from user favorites</li>
 *   <li>Authentication check before favorite actions</li>
 *   <li>State synchronization with backend</li>
 *   <li>Error handling for favorite operations</li>
 * </ul>
 */
import { ref, onMounted } from 'vue';
import heartIcon from '@/assets/icons/heart.svg';
import heartRedIcon from '@/assets/icons/heart-red.svg';
import { addFavorite, removeFavorite, checkIsFavorite } from '@/services/FavoriteService.ts';
import { useUserStore } from '@/stores/UserStore.ts';
import router from '@/router';

/**
 * Component props definition
 */
const props = defineProps<{
  /**
   * ID of the item to favorite/unfavorite
   * @type {string}
   */
  itemId: string;
}>();

/**
 * Current favorite status of the item
 * @type {Ref<boolean>}
 */
const isFavorite = ref(false);

/**
 * Check initial favorite status on component mount
 * <p>Fetches the current favorite status from the server</p>
 */
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

/**
 * Toggles the favorite status of an item
 * <p>Handles login state verification and API communication</p>
 * @param {Event} event - Click event
 */
async function toggleFavorite(event: Event) {
  event.stopPropagation();

  if (!props.itemId) {
    console.error('Cannot toggle favorite: itemId is missing');
    return;
  }

  const userStore = useUserStore();
  if (!userStore.getUserId || userStore.getUserId === '0') {
    await router.push('/login');
    return;
  }

  const itemId = parseInt(props.itemId);
  console.log('UserId:' + userStore.getUserId); // Log UserID for debugging

  try {
    if (isFavorite.value) {
      // If currently favorite, try to remove
      await removeFavorite(itemId);
      isFavorite.value = false; // Update state only if removeFavorite succeeds
      console.log(`Removed favorite: ${itemId}`);
    } else {
      // If not currently favorite, try to add
      await addFavorite(itemId);
      isFavorite.value = true; // Update state only if addFavorite succeeds
      console.log(`Added favorite: ${itemId}`);
    }
  } catch (error: unknown) {
    // Use a type guard to check if the error is an Error with an optional response property
    if (error instanceof Error) {
      const err = error as Error & { response?: { status?: number } };
      if (err.response?.status === 400 && err.message.includes('already has favorited')) {
        console.warn('Syncing state: Item was already favorited.');
        isFavorite.value = true;
      } else if (err.response?.status === 404 && isFavorite.value) {
        console.warn('Syncing state: Item was not favorited to begin with.');
        isFavorite.value = false;
      } else {
        console.error('Error toggling favorite:', err.message);
      }
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
  width: 30px;
  height: 30px;
}
</style>
