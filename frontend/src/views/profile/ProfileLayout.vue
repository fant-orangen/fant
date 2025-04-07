// Example: ProfileLayout.vue

<template>
  <div class="profile-layout">
    <aside class="profile-sidebar">
      <nav>
        <RouterLink
          v-for="tile in profileTiles"
          :key="tile.name"
          :to="tile.route"
          class="tile-link"
          active-class="router-link-active"
        >
          <component :is="tile.icon" class="tile-icon" /> <span>{{ $t(tile.label) }}</span>
          <p>{{ $t(tile.description) }}</p>
        </RouterLink>
      </nav>
    </aside>
    <main class="profile-content">
      <RouterView /> </main>
  </div>
</template>

<script setup lang="ts">
import { ref, markRaw } from 'vue'; // <-- Import markRaw
import { RouterLink, RouterView } from 'vue-router';

// Import your icon components
import IconProfile from '@/components/icons/IconProfile.vue'; // Adjust path
import IconListings from '@/components/icons/IconListings.vue'; // Adjust path
import IconHeart from '@/components/icons/IconHeart.vue'; // Adjust path
// Import other components if needed

// Define the reactive structure holding tile data
const profileTiles = ref([
  {
    name: 'profile-overview',
    route: { name: 'profile-overview' },
    icon: markRaw(IconProfile), // <-- Wrap icon component with markRaw
    label: 'PROFILE_TILE_MY_ACCOUNT_TITLE',
    description: 'PROFILE_TILE_MY_ACCOUNT_DESC'
  },
  {
    name: 'profile-listings',
    route: { name: 'profile-listings' },
    icon: markRaw(IconListings), // <-- Wrap icon component with markRaw
    label: 'MY_LISTINGS',
    description: 'PROFILE_TILE_MY_LISTINGS_DESC'
  },
  { // <-- Add this new object for "My Bids" -->
    name: 'profile-my-bids',
    route: { name: 'profile-my-bids' }, // Use the route name defined in router/index.ts
    icon: markRaw(IconListings),          // Use the imported (and markRaw-wrapped) icon
    label: 'MY_BIDS_TITLE',             // Use the new i18n key
    description: 'PROFILE_TILE_MY_BIDS_DESC' // Use the new i18n key
  },
  {
    name: 'profile-favorites',
    route: { name: 'profile-favorites' },
    icon: markRaw(IconHeart), // <-- Wrap icon component with markRaw
    label: 'MY_FAVORITES',
    description: 'PROFILE_TILE_FAVORITES_DESC'
  }
  // Add other tiles if you have them
]);

// Other script setup logic if any...
</script>

<style scoped>
/* Add styles for your layout, sidebar, tiles etc. */
.profile-layout {
  display: flex;
  gap: 1rem; /* Adjust gap as needed */
  padding: 1rem;
  max-width: 1400px;
  margin: 0 auto;
}

.profile-sidebar {
  flex: 0 0 250px; /* Fixed width sidebar, adjust as needed */
  /* Add styles for sidebar appearance */
}

.profile-sidebar nav {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.tile-link {
  display: flex;
  align-items: center;
  padding: 0.8rem 1rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  text-decoration: none;
  color: var(--color-text);
  transition: background-color 0.2s ease, box-shadow 0.2s ease;
  gap: 0.8rem; /* Space between icon and text */
}

.tile-link:hover {
  background-color: var(--color-background-mute);
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.tile-link.router-link-active { /* Style for the active link */
  background-color: hsla(160, 100%, 37%, 0.1);
  border-left: 4px solid hsla(160, 100%, 37%, 1);
  font-weight: bold;
  padding-left: calc(1rem - 4px); /* Adjust padding to account for border */
}

.tile-icon {
  width: 24px; /* Adjust icon size */
  height: 24px;
  flex-shrink: 0; /* Prevent icon from shrinking */
}

.tile-link div { /* Assuming text is wrapped in a div or similar */
  display: flex;
  flex-direction: column;
}

.tile-link span { /* Label style */
  font-weight: 500;
}
.tile-link p { /* Description style */
  font-size: 0.85em;
  color: var(--vt-c-text-light-2);
  margin: 0.2rem 0 0 0;
  line-height: 1.3;
}


.profile-content {
  flex-grow: 1; /* Main content takes remaining space */
  /* Add styles for content area */
}
</style>
