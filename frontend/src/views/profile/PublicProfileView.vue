<template>
  <div class="public-profile-page">
    <div v-if="loading" class="loading-state">
      <p>Loading profile...</p>
    </div>

    <div v-else-if="error" class="error-state">
      <p>{{ error }}</p>
      <router-link to="/" class="home-link">Go Home</router-link>
    </div>

    <div v-else-if="userProfile" class="profile-content">
      <div class="profile-header">
        <h1>{{ userProfile.displayName }}</h1>

        <p v-if="userProfile.email" class="user-email">
          <span class="icon">📧</span> {{ userProfile.email }}
        </p>

        <p class="user-join-date">
          <span class="icon">📅</span> Member since: {{ formatDate(userProfile.createdAt) }}
        </p>
      </div>

      <h2>Listings by {{ userProfile.displayName }}</h2>
      <div class="user-listings">
        <ItemList
          :items="userItems"
          :is-loading="itemsLoading"
          :error="itemsError"
          :current-page="currentPage"
          :total-pages="totalPages"
          :pagination-enabled="true"
          :empty-message="`${userProfile.displayName} has no active listings.`"
          @change-page="handlePageChange"
        />
      </div>
    </div>
    <div v-else class="not-found-state">
      <h2>User Not Found</h2>
      <p>The requested user profile could not be found.</p>
      <router-link to="/" class="home-link">Go Home</router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import ItemList from '@/components/item/ItemList.vue'; // [cite: project V e2e 13/frontend/src/components/item/ItemList.vue]
// Import the updated UserResponseDto type
import { fetchUserById, type UserResponseDto } from '@/services/UserService'; // [cite: project V e2e 13/frontend/src/services/UserService.ts]
import { searchItems } from '@/services/ItemService'; // [cite: project V e2e 13/frontend/src/services/ItemService.ts]
import type { ItemPreviewType, PaginatedItemPreviewResponse } from '@/models/Item'; // [cite: project V e2e 13/frontend/src/models/Item.ts]

// Define props
const props = defineProps<{
  userId: string | number;
}>();

// Router instance
const route = useRoute();

// Reactive state variables
const userProfile = ref<UserResponseDto | null>(null); // Uses the updated DTO type
const userItems = ref<ItemPreviewType[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);
const itemsLoading = ref(false);
const itemsError = ref<string | null>(null);
const currentPage = ref(1);
const totalPages = ref(1);
const pageSize = ref(12); // Or your desired page size

// --- Helper Functions ---

/**
 * Formats an ISO date string into a more readable format.
 * @param {string} dateString - The ISO date string.
 * @returns {string} Formatted date string (e.g., "Jan 1, 2024") or 'N/A'.
 */
function formatDate(dateString?: string): string {
  if (!dateString) return 'N/A';
  try {
    const date = new Date(dateString);
    // Adjust options for locale-specific formatting if needed
    return date.toLocaleDateString(undefined, { year: 'numeric', month: 'long', day: 'numeric' });
  } catch (e) {
    console.error("Failed to parse date:", dateString, e);
    return 'Invalid date';
  }
}

// --- Data Loading Functions ---

/**
 * Fetches the public profile data for the user using fetchUserById.
 */
async function loadUserProfile() {
  loading.value = true;
  error.value = null;
  userProfile.value = null;
  try {
    // Fetch public user data (now includes email if backend was updated)
    userProfile.value = await fetchUserById(props.userId); // [cite: project V e2e 13/frontend/src/services/UserService.ts]
  } catch (err) {
    console.error('Failed to load user profile:', err);
    error.value = 'Could not load user profile. The user may not exist.';
    if ((err as any).response?.status === 404) {
      error.value = 'User not found.';
    }
  } finally {
    loading.value = false;
  }
}

/**
 * Fetches the items listed by the specified user, handling pagination.
 * Uses the modified searchItems service function.
 * @param {number} page - The page number to fetch (1-based).
 */
async function loadUserItems(page: number = 1) {
  if (!userProfile.value) return; // Ensure profile is loaded first

  itemsLoading.value = true;
  itemsError.value = null;
  try {
    // Use searchItems with the sellerId parameter
    const response: PaginatedItemPreviewResponse = await searchItems({ // [cite: project V e2e 13/frontend/src/services/ItemService.ts]
      sellerId: props.userId, // Filter by the profile user's ID
      page: page - 1, // API uses 0-based indexing
      size: pageSize.value,
      sort: 'publishedAt,desc', // Default sort order
    });
    userItems.value = response.content;
    totalPages.value = response.totalPages;
    currentPage.value = page; // Update current page
  } catch (err) {
    console.error('Failed to load user items:', err);
    itemsError.value = 'Could not load listings for this user.';
    userItems.value = []; // Clear items on error
    totalPages.value = 1; // Reset pagination
    currentPage.value = 1;
  } finally {
    itemsLoading.value = false;
  }
}

// --- Event Handlers ---

/**
 * Handles page changes triggered by the ItemList component's pagination.
 * @param {number} newPage - The new page number requested (1-based).
 */
function handlePageChange(newPage: number) {
  if (newPage !== currentPage.value && newPage >= 1 && newPage <= totalPages.value) {
    loadUserItems(newPage);
  }
}

// --- Lifecycle Hooks ---

// Load profile and initial items when the component mounts
onMounted(() => {
  loadUserProfile().then(() => {
    if (userProfile.value) {
      loadUserItems(1); // Load first page of items if profile loaded
    }
  });
});

// Watch for changes in the userId prop (if component is reused)
watch(
  () => props.userId,
  (newUserId, oldUserId) => {
    if (newUserId && newUserId !== oldUserId) {
      // Reset state before loading new user data
      userItems.value = [];
      currentPage.value = 1;
      totalPages.value = 1;
      itemsError.value = null;
      // Load new user's profile and items
      loadUserProfile().then(() => {
        if (userProfile.value) {
          loadUserItems(1); // Load first page for the new user
        }
      });
    }
  },
  { immediate: true } // Run when the component mounts as well
);
</script>

<style scoped>
.public-profile-page {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 1rem;
  font-family: Inter, sans-serif; /* Apply consistent font */
}

/* Style the profile header section */
.profile-header {
  margin-bottom: 2.5rem; /* Increased space */
  padding-bottom: 1.5rem; /* Increased padding */
  border-bottom: 1px solid var(--color-border);
  text-align: center;
}

/* Increased size and visual weight for Display Name */
.profile-header h1 {
  margin-bottom: 0.75rem; /* More space below name */
  color: var(--color-heading); /* Use theme heading color */
  font-size: 2.6em; /* Larger font size */
  font-weight: 700; /* Bolder */
  line-height: 1.2;
}

/* Style for Email and Join Date - Larger and clearer */
.user-email,
.user-join-date {
  font-size: 1.1em; /* Larger text */
  color: var(--vt-c-text-light-1); /* Slightly darker than muted */
  margin-bottom: 0.5rem; /* More space between lines */
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem; /* Increased gap */
}

.user-email .icon,
.user-join-date .icon {
  font-size: 1.3em; /* Larger icons */
  color: var(--vt-c-teal-soft); /* Use theme accent color */
}

/* Style the "Listings by..." heading */
.profile-content h2 {
  margin-top: 2.5rem; /* More space above */
  margin-bottom: 1.5rem;
  font-size: 1.8em; /* Larger heading */
  color: var(--color-heading);
  text-align: center;
  font-weight: 600;
}

/* Loading, Error, Not Found states */
.loading-state,
.error-state,
.not-found-state {
  text-align: center;
  padding: 3rem 1rem;
  margin-top: 2rem;
  color: var(--color-text);
  font-size: 1.1em; /* Slightly larger text for states */
}

.error-state p,
.not-found-state p {
  margin-bottom: 1rem;
  line-height: 1.6;
}

.error-state,
.not-found-state {
  background-color: var(--color-background-soft);
  border: 1px solid var(--color-border);
  border-radius: 8px;
}

.home-link {
  display: inline-block;
  background-color: var(--vt-c-teal-soft);
  color: white;
  border: none;
  padding: 0.8rem 1.5rem; /* Larger button */
  border-radius: 5px;
  text-decoration: none;
  transition: background-color 0.2s ease, transform 0.1s ease;
  font-weight: 600; /* Bolder button text */
  font-size: 1rem;
}

.home-link:hover {
  background-color: var(--vt-c-teal-dark);
  transform: translateY(-1px); /* Subtle lift */
}

/* Container for the item list */
.user-listings {
  margin-top: 1rem;
}
</style>
