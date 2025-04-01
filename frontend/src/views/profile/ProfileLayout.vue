<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useUserStore } from '@/stores/UserStore';
import { useI18n } from 'vue-i18n';

// --- Import your actual icon components here ---
import IconHeart from '@/components/icons/IconHeart.vue';
import IconProfile from '@/components/icons/IconProfile.vue'; // Import the new icon
import IconListings from '@/components/icons/IconListings.vue'; // Import the new icon
import IconVipps from '@/components/icons/IconVipps.vue';
// import IconVerified from '@/components/icons/IconVerified.vue'; // Keep if needed for Vipps verified state

const userStore = useUserStore();
const { t } = useI18n();
const loadingProfile = ref(false);
const profileError = ref('');

// --- Placeholder Data ---
const userRating = computed(() => '9,7');
const ratingCount = computed(() => 5);
const vippsVerified = computed(() => false);
// --- End Placeholder Data ---

// Define navigation tiles - Updated 'icon' properties
const profileTiles = ref([
  {
    titleKey: 'MY_ACCOUNT_TITLE',
    descriptionKey: 'PROFILE_TILE_MY_ACCOUNT_DESC',
    routeName: 'profile-overview',
    icon: IconProfile, // Use the imported IconProfile component
  },
  {
    titleKey: 'MY_LISTINGS',
    descriptionKey: 'PROFILE_TILE_MY_LISTINGS_DESC',
    routeName: 'profile-listings',
    icon: IconListings, // Use the imported IconListings component
  },
  {
    titleKey: 'MY_FAVORITES',
    descriptionKey: 'PROFILE_TILE_FAVORITES_DESC',
    routeName: 'profile-favorites',
    icon: IconHeart, // Keep using IconHeart
  },
]);

// Fetch profile data when the layout mounts (no changes needed here)
async function loadInitialProfile() {
  if (!userStore.profile?.email && userStore.loggedIn) {
    loadingProfile.value = true;
    profileError.value = '';
    try {
      await userStore.fetchProfile();
    } catch (err) {
      console.error('Failed to load profile:', err);
      profileError.value = t('PROFILE_LOAD_ERROR');
    } finally {
      loadingProfile.value = false;
    }
  }
}

onMounted(() => {
  loadInitialProfile();
});

// Vipps function (no changes needed here)
function verifyWithVipps() {
  console.log('Initiating Vipps verification...');
  // TODO: Add actual Vipps integration logic here
}
</script>

<template>
  <section class="profile-layout">
    <header class="profile-header">
      <div class="user-details">
        <div class="avatar-placeholder"></div> {/* TODO: Replace with real avatar */}
        <div class="user-info">
          <h2 v-if="userStore.username">{{ userStore.username }}</h2>
          <p v-if="userStore.profile?.email">{{ userStore.profile.email }}</p>
          <p class="rating">
            {{ userRating }} ({{ ratingCount }} {{ t('PROFILE_RATINGS_LABEL') }})
          </p>
        </div>
      </div>
    </header>

    <section class="vipps-section" v-if="!vippsVerified">
      <component :is="IconVipps" class="vipps-icon" />
      <div class="vipps-text">
        <h3>{{ t('PROFILE_VIPPS_PROMPT_TITLE') }}</h3>
        <p>{{ t('PROFILE_VIPPS_PROMPT_DESC') }}</p>
      </div>
      <button @click="verifyWithVipps" class="vipps-button">
        {{ t('PROFILE_VERIFY_VIPPS_BUTTON') }}
      </button>
    </section>
    <section class="vipps-section verified" v-else>
      {/* <component :is="IconVipps" class="vipps-icon" /> */} {/* TODO: Use actual Verified icon */}
      <div class="vipps-icon">✅</div> {/* Placeholder */}
      <div class="vipps-text">
        <h3>{{ t('PROFILE_VIPPS_VERIFIED_TITLE') }}</h3>
      </div>
    </section>

    <div v-if="loadingProfile" class="loading-message">{{ t('LOADING') }}</div>
    <div v-if="profileError" class="error-message">{{ profileError }}</div>

    <nav class="profile-tiles" v-if="!loadingProfile && !profileError">
      <router-link
        v-for="tile in profileTiles"
        :key="tile.routeName"
        :to="{ name: tile.routeName }"
        class="tile-link"
        active-class="active-tile"
      >
        <div class="profile-tile">
          <component v-if="tile.icon" :is="tile.icon" class="tile-icon" aria-hidden="true" />
          <h3>{{ t(tile.titleKey) }}</h3>
          <p>{{ t(tile.descriptionKey) }}</p>
        </div>
      </router-link>
    </nav>

    <main class="profile-content" v-if="!loadingProfile && !profileError">
      <router-view />
    </main>

  </section>
</template>

<style scoped>
/* Styles remain the same */
.profile-layout {
  max-width: 960px;
  margin: 2rem auto;
  padding: 1rem;
}

.profile-header {
  display: flex;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #eee;
}

.user-details {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.avatar-placeholder {
  width: 60px;
  height: 60px;
  background-color: #ccc;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  color: white;
}

.user-info h2 {
  margin: 0 0 0.25rem 0;
  font-size: 1.5rem;
}

.user-info p {
  margin: 0;
  color: #555;
}

.user-info .rating {
  font-weight: bold;
  color: #333;
}

.vipps-section {
  background-color: #fff0f0; /* Vipps-like color background */
  border: 1px solid #ffccd1;
  border-radius: 8px;
  padding: 1rem 1.5rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2rem;
  gap: 1rem;
}
.vipps-section.verified {
  background-color: #eafaf1;
  border-color: #b8e9c9;
}

.vipps-icon {
  font-size: 1.5rem;
}

.vipps-text h3 {
  margin: 0 0 0.25rem 0;
  font-size: 1rem;
  font-weight: bold;
}

.vipps-text p {
  margin: 0;
  font-size: 0.875rem;
  color: #333;
}

.vipps-button {
  background-color: #FF5B24; /* Vipps color */
  color: white;
  border: none;
  padding: 0.6rem 1.2rem;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  white-space: nowrap;
}
.vipps-button:hover {
  background-color: #e64a19; /* Darker Vipps */
}


.loading-message, .error-message {
  text-align: center;
  padding: 2rem;
  font-size: 1.1rem;
}
.error-message {
  color: red;
}

.profile-tiles {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  margin-bottom: 2rem;
}

.profile-tile {
  background: #fff;
  border: 1px solid #ddd;
  padding: 1.5rem 1rem;
  border-radius: 8px;
  text-align: center;
  transition: box-shadow 0.2s, border-color 0.2s;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.tile-link {
  text-decoration: none;
  color: inherit;
}
.tile-link:hover .profile-tile {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-color: #ccc;
}
.tile-link.active-tile .profile-tile {
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.3);
}

/* Style for actual icon components */
.tile-icon {
  width: 32px;
  height: 32px;
  margin-bottom: 0.5rem;
  color: #555; /* Example default color */
}
/* Fallback placeholder style - can likely remove if all icons are implemented */
/* .tile-icon-placeholder { ... } */

.profile-tile h3 {
  margin: 0;
  font-size: 1.1rem;
}
.profile-tile p {
  margin: 0;
  font-size: 0.85rem;
  color: #666;
}

.profile-content {
  margin-top: 1rem;
}
</style>
