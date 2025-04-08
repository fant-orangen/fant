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
  event.stopPropagation(); // Prevent click from bubbling to parent elements

  if (!props.itemId) {
    console.error('Cannot toggle favorite: itemId is missing');
    return;
  }

  try {
    const itemId = parseInt(props.itemId);
    if (isFavorite.value) {
      await removeFavorite(itemId);
    } else {
      await addFavorite(itemId);
    }
    isFavorite.value = !isFavorite.value;
  } catch (error) {
    console.error('Error toggling favorite:', error);
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
