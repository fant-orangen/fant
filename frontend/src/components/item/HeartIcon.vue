<template>
  <div
    class="heart-icon"
    :class="{ 'is-loading': isLoading || isInitializing }" :data-is-favorite="hasCheckedInitialStatus ? isFavorite : 'loading'" @click="toggleFavorite"
    role="button"
    tabindex="0"
    @keydown.enter.space="toggleFavorite"
  >
    <img
      v-if="!isInitializing && hasCheckedInitialStatus"
      :key="isFavorite ? 'red' : 'empty'"
      :src="isFavorite ? heartRedIcon : heartIcon"
      alt="Heart Icon"
      :class="{ 'icon-hidden-during-toggle': isLoading }" />
    <span v-if="isLoading || isInitializing" class="loading-spinner" aria-hidden="true">...</span>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue';
import heartIcon from '@/assets/icons/heart.svg';
import heartRedIcon from '@/assets/icons/heart-red.svg';
import { addFavorite, removeFavorite, checkIsFavorite } from '@/services/FavoriteService.ts'; // [cite: project V e2e 9/frontend/src/services/FavoriteService.ts]
import { useUserStore } from '@/stores/UserStore.ts'; // [cite: project V e2e 9/frontend/src/stores/UserStore.ts]
import router from '@/router'; // [cite: project V e2e 9/frontend/src/router/index.ts]

const props = defineProps<{
  itemId: string;
}>();

const isFavorite = ref(false);
const isLoading = ref(false); // Tracks toggle action loading state ONLY
const isInitializing = ref(true); // Tracks initial onMounted loading state ONLY
const hasCheckedInitialStatus = ref(false); // Tracks if initial check completed

// --- Utility Function for API Calls ---
async function callApi<T>(apiCall: () => Promise<T>): Promise<T | null> {
  try {
    return await apiCall();
  } catch (error) {
    console.error("HeartIcon API Error:", error);
    return null; // Indicate failure
  }
}

// --- Initialization ---
async function fetchInitialStatus() {
  isInitializing.value = true; // Start initialization
  hasCheckedInitialStatus.value = false;
  isLoading.value = false; // Ensure toggle loading is false initially

  const userStore = useUserStore(); // [cite: project V e2e 9/frontend/src/stores/UserStore.ts]
  if (!props.itemId || !userStore.isLoggedInUser) {
    isFavorite.value = false;
    isInitializing.value = false; // End initialization
    hasCheckedInitialStatus.value = true;
    if (!props.itemId) console.error('HeartIcon: ItemId missing on mount');
    return;
  }

  console.log(`HeartIcon: Checking status for item ${props.itemId}`);
  const favoriteStatus = await callApi(() => checkIsFavorite(parseInt(props.itemId))); // [cite: project V e2e 9/frontend/src/services/FavoriteService.ts]

  isFavorite.value = favoriteStatus ?? false;
  isInitializing.value = false; // End initialization
  hasCheckedInitialStatus.value = true;
  console.log(`HeartIcon: Initial status for item ${props.itemId} set to ${isFavorite.value}`);
}

onMounted(fetchInitialStatus);

// Watch for itemId changes
watch(() => props.itemId, (newItemId, oldItemId) => {
  if (newItemId && newItemId !== oldItemId) {
    console.log(`HeartIcon: itemId changed to ${newItemId}, re-fetching status.`);
    fetchInitialStatus();
  }
});

// --- Toggle Function ---
async function toggleFavorite(event?: Event) {
  event?.stopPropagation();

  // --- FIX: Check for guest *before* checking loading states ---
  const userStore = useUserStore(); // [cite: project V e2e 9/frontend/src/stores/UserStore.ts]
  if (!userStore.isLoggedInUser) {
    console.log("HeartIcon: User not logged in, redirecting to login.");
    await router.push('/login'); // [cite: project V e2e 9/frontend/src/router/index.ts]
    return;
  }
  // --- End Fix ---

  // Prevent action if still initializing or already toggling
  if (isInitializing.value || isLoading.value) {
    console.warn("HeartIcon: Toggle prevented - already loading or initializing.");
    return;
  }

  if (!props.itemId) {
    console.error('HeartIcon: Cannot toggle favorite: itemId is missing');
    return;
  }

  isLoading.value = true; // Set loading ONLY for the toggle action
  const itemId = parseInt(props.itemId);
  const currentActionIsRemove = isFavorite.value;

  const apiCall = currentActionIsRemove
    ? () => removeFavorite(itemId) // [cite: project V e2e 9/frontend/src/services/FavoriteService.ts]
    : () => addFavorite(itemId); // [cite: project V e2e 9/frontend/src/services/FavoriteService.ts]

  const success = await callApi(apiCall);

  if (success !== null) {
    isFavorite.value = !currentActionIsRemove;
    console.log(`HeartIcon: ${currentActionIsRemove ? 'Removed' : 'Added'} favorite: ${itemId}`);
  } else {
    console.error(`HeartIcon: Failed to ${currentActionIsRemove ? 'remove' : 'add'} favorite for item ${itemId}. State remains ${isFavorite.value}.`);
  }

  isLoading.value = false; // Clear toggle loading state
}


</script>

<style scoped>
.heart-icon {
  cursor: pointer;
  width: 30px; /* Maintain size */
  height: 30px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: opacity 0.2s ease-in-out;
  overflow: hidden;
  /* --- FIX: Removed background and border-radius --- */
  /* background-color: rgba(255, 255, 255, 0.5); */ /* Removed */
  /* border-radius: 50%; */ /* Removed */
}
.heart-icon:hover:not(.is-loading):not([aria-disabled='true']) {
  /* background-color: rgba(200, 200, 200, 0.5); */ /* Removed hover background */
  filter: brightness(1.1); /* Add alternative hover feedback */
}

/* Style when loading or disabled */
.heart-icon.is-loading,
.heart-icon[aria-disabled='true']:not(.is-loading) {
  cursor: default;
  opacity: 0.5; /* Make it look disabled/loading */
}

.heart-icon img {
  width: 100%; /* Image takes full container space now */
  height: 100%;
  display: block;
  object-fit: contain;
}

/* Class to hide image during toggle operation */
.icon-hidden-during-toggle {
  opacity: 0;
  transition: opacity 0.1s ease-in-out;
}


.loading-spinner {
  /* Basic spinner */
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 1.2em; /* Adjust size if needed */
  line-height: 1;
  color: var(--vt-c-teal-dark);
  /* Simple text spinner */
  /* Consider using a CSS animation */
}

/* Hide image and show spinner based on state */
.heart-icon img { visibility: visible; } /* Visible by default */
.heart-icon.is-loading img { visibility: hidden; } /* Hide during toggle */
.heart-icon:not([data-is-favorite*='true']):not([data-is-favorite*='false']) img { visibility: hidden; } /* Hide during init */

.loading-spinner { display: none; } /* Hidden by default */
.heart-icon.is-loading .loading-spinner { display: inline-block; } /* Show during toggle */
.heart-icon:not([data-is-favorite*='true']):not([data-is-favorite*='false']) .loading-spinner { display: inline-block; } /* Show during init */

</style>
