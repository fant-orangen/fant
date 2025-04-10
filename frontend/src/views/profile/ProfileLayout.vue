<template>
  <div class="profile-layout">
    <aside class="profile-nav-container">
      <nav>
        <RouterLink
          v-for="tile in profileTiles"
          :key="tile.name"
          :to="tile.route"
          class="tile-link"
          exact-active-class="router-link-active"
          :title="$t(tile.label)"
        >
          <component :is="tile.icon" class="tile-icon" aria-hidden="true" />
          <span class="tile-label">{{ $t(tile.label) }}</span>
        </RouterLink>
      </nav>
    </aside>

    <main class="profile-content">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, markRaw } from 'vue';
import { RouterLink, RouterView } from 'vue-router';
import { useI18n } from 'vue-i18n';

// Import your existing icon components
import IconProfile from '@/components/icons/IconProfile.vue';
import IconListings from '@/components/icons/IconListings.vue';
import IconBid from '@/components/icons/IconBid.vue';
import IconHeart from '@/components/icons/IconHeart.vue';


const { t } = useI18n();

// Define the reactive structure holding tile data - removed description, fixed icon
const profileTiles = ref([
  {
    name: 'profile-overview',
    route: { name: 'profile-overview' },
    icon: markRaw(IconProfile),
    label: 'PROFILE_TILE_MY_ACCOUNT_TITLE',
  },
  {
    name: 'profile-listings',
    route: { name: 'profile-listings' },
    icon: markRaw(IconListings),
    label: 'MY_LISTINGS',
  },
  {
    name: 'profile-my-bids',
    route: { name: 'profile-my-bids' },
    icon: markRaw(IconBid),
    label: 'MY_BIDS_TITLE',
  },
  {
    name: 'profile-favorites',
    route: { name: 'profile-favorites' },
    icon: markRaw(IconHeart),
    label: 'MY_FAVORITES',
  }
]);
</script>

<style scoped>
.profile-layout {
  display: flex;
  gap: 1.5rem;
  padding: 1rem;
  max-width: 1400px;
  margin: 0 auto;
}

/* Sidebar styles for desktop */
.profile-nav-container {
  flex: 0 0 220px; /* Fixed width */
  border-right: 1px solid var(--color-border);
  padding-right: 1.5rem;
}

.profile-nav-container nav {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  position: sticky;
  top: 1rem;
}

.tile-link {
  display: flex;
  align-items: center;
  padding: 0.75rem 1rem;
  border-radius: 6px;
  text-decoration: none;
  color: var(--color-text);
  transition: background-color 0.2s ease, color 0.2s ease, border-color 0.2s ease;
  gap: 0.8rem;
  border: 1px solid transparent;
}

.tile-link:hover {
  background-color: var(--color-background-mute);
}

.tile-link.router-link-active {
  background-color: var(--vt-c-teal-light);
  color: var(--vt-c-black-soft);
  font-weight: 600;
  border-color: var(--vt-c-teal-soft);
}

.tile-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
  opacity: 0.8;
}

.tile-link.router-link-active .tile-icon {
  opacity: 1;
}

.tile-label {
  font-size: 0.95em;
  line-height: 1.3;
  white-space: nowrap; /* Prevent wrapping in sidebar */
}

.profile-content {
  flex-grow: 1;
  min-width: 0;
}

/* --- Responsive Styles for Mobile (Top Navigation Bar) --- */
@media (max-width: 768px) {
  .profile-layout {
    flex-direction: column; /* Stack nav and content */
    padding: 0.5rem; /* Reduce padding on mobile */
    gap: 1rem;
  }

  .profile-nav-container {
    flex-basis: auto; /* Allow height to be determined by content */
    width: 100%;
    border-right: none;
    border-bottom: 1px solid var(--color-border);
    padding: 0; /* Remove padding */
    overflow-x: auto; /* Enable horizontal scrolling if items overflow */
    background-color: var(--color-background-soft); /* Optional: Slight background */
  }

  .profile-nav-container nav {
    flex-direction: row; /* Horizontal layout */
    justify-content: space-around; /* Distribute items evenly */
    align-items: center; /* Center items vertically */
    gap: 0; /* Remove gap, use padding on links instead */
    position: static; /* Not sticky on mobile */
    padding: 0.25rem 0; /* Add a little vertical padding */
    width: 100%; /* Ensure nav takes full width */
  }

  .tile-link {
    flex-direction: column; /* Stack icon and label */
    align-items: center;
    justify-content: center;
    padding: 0.5rem; /* Padding inside each link */
    gap: 0.2rem;
    border: none;
    border-radius: 4px;
    flex-grow: 1; /* Allow links to grow */
    flex-basis: 0; /* Allow links to shrink */
    min-width: 75px; /* Ensure tap target size */
    text-align: center;
  }

  .tile-link.router-link-active {
    background-color: hsla(160, 100%, 37%, 0.1);
    border-color: transparent; /* Ensure no border on active */
    color: hsla(160, 100%, 37%, 1); /* Keep active color */
  }

  .tile-icon {
    width: 24px;
    height: 24px;
    margin-bottom: 0.1rem;
  }

  .tile-label {
    font-size: 0.7rem; /* Even smaller label on mobile */
    line-height: 1.1;
    white-space: normal; /* Allow wrapping if needed */
    max-width: 100%;
  }
}

</style>
