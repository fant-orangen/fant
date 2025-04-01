<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useUserStore } from '@/stores/UserStore';
import { useI18n } from 'vue-i18n';
// Import your icon components or library methods
// import IconProfile from '@/components/icons/IconProfile.vue';
// import IconListings from '@/components/icons/IconListings.vue';
// import IconHeart from '@/components/icons/IconHeart.vue';

const userStore = useUserStore();
const { t } = useI18n();
const loadingProfile = ref(false);
const profileError = ref('');

// --- Placeholder Data ---
// TODO: Replace with real data fetching/logic
const userRating = computed(() => '9,7'); // Placeholder rating from image
const ratingCount = computed(() => 5); // Placeholder count from image
const bankIdVerified = computed(() => false); // Placeholder verification status
// --- End Placeholder Data ---

// Define navigation tiles based on the reference image
const profileTiles = ref([
  {
    titleKey: 'PROFILE_TILE_MY_ACCOUNT_TITLE', // e.g., "Min konto"
    descriptionKey: 'PROFILE_TILE_MY_ACCOUNT_DESC', // e.g., "Se dine opplysninger..."
    routeName: 'profile-overview', // Route name for the overview page
    icon: 'IconProfile', // Placeholder icon component name
  },
  {
    titleKey: 'PROFILE_TILE_MY_LISTINGS_TITLE', // e.g., "Mine annonser"
    descriptionKey: 'PROFILE_TILE_MY_LISTINGS_DESC', // e.g., "Se alle dine annonser..."
    routeName: 'profile-listings', // Route name for user's listings page
    icon: 'IconListings', // Placeholder icon component name
  },
  {
    titleKey: 'PROFILE_TILE_FAVORITES_TITLE', // e.g., "Favoritter"
    descriptionKey: 'PROFILE_TILE_FAVORITES_DESC', // e.g., "Se alle annonsene du liker..."
    routeName: 'profile-favorites', // Route name for favorites page
    icon: 'IconHeart', // Placeholder icon component name
  },
]);

// Fetch profile data when the layout mounts
async function loadInitialProfile() {
  // Only fetch if profile hasn't been loaded yet (simple check)
  if (!userStore.profile?.email && userStore.loggedIn) {
    loadingProfile.value = true;
    profileError.value = '';
    try {
      await userStore.fetchProfile();
    } catch (err) {
      console.error('Failed to load profile:', err);
      profileError.value = t('PROFILE_LOAD_ERROR'); // Ensure this key exists in locales
    } finally {
      loadingProfile.value = false;
    }
  }
}

onMounted(() => {
  loadInitialProfile();
});

// TODO: Implement BankID verification logic
function verifyWithBankId() {
  console.log('Initiating BankID verification...');
  // Add actual BankID integration logic here
}
</script>

<template>
  <section class="profile-layout">
    <header class="profile-header">
      <div class="user-details">
        <div class="avatar-placeholder"></div>
        <div class="user-info">
          <h2 v-if="userStore.username">{{ userStore.username }}</h2>
          <p v-if="userStore.profile?.email">{{ userStore.profile.email }}</p>
          <p class="rating">
            {{ userRating }} ({{ ratingCount }} {{ t('PROFILE_RATINGS_LABEL') }})
          </p>
        </div>
      </div>
    </header>

    <section class="bankid-section" v-if="!bankIdVerified">
      <div class="bankid-icon">🛡️</div> <div class="bankid-text">
      <h3>{{ t('PROFILE_BANKID_PROMPT_TITLE') }}</h3>
      <p>{{ t('PROFILE_BANKID_PROMPT_DESC') }}</p>
    </div>
      <button @click="verifyWithBankId" class="bankid-button">
        {{ t('PROFILE_VERIFY_VIPPS_BUTTON') }}
      </button>
    </section>
    <section class="bankid-section verified" v-else>
      <div class="bankid-icon">✅</div> <div class="bankid-text">
      <h3>{{ t('PROFILE_BANKID_VERIFIED_TITLE') }}</h3>
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
          <div class="tile-icon-placeholder"></div> <h3>{{ t(tile.titleKey) }}</h3>
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
.profile-layout {
  max-width: 960px; /* Adjust max-width as needed */
  margin: 2rem auto;
  padding: 1rem;
}

.profile-header {
  display: flex;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #eee; /* Subtle separator */
}

.user-details {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.avatar-placeholder {
  width: 60px; /* Adjust size */
  height: 60px;
  background-color: #ccc; /* Placeholder color */
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem; /* For potential initials */
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

/* .view-profile-button { ... } */

.bankid-section {
  background-color: #e7f3ff; /* Light blue background */
  border: 1px solid #b3d7ff;
  border-radius: 8px;
  padding: 1rem 1.5rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2rem;
  gap: 1rem;
}
.bankid-section.verified {
  background-color: #eafaf1; /* Light green */
  border-color: #b8e9c9;
}


.bankid-icon {
  font-size: 1.5rem; /* Adjust icon size */
}

.bankid-text h3 {
  margin: 0 0 0.25rem 0;
  font-size: 1rem;
  font-weight: bold;
}

.bankid-text p {
  margin: 0;
  font-size: 0.875rem;
  color: #333;
}

.bankid-button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 0.6rem 1.2rem;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  white-space: nowrap; /* Prevent button text wrapping */
}
.bankid-button:hover {
  background-color: #0056b3;
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
  /* Adjust columns based on image - seems like 3 */
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  margin-bottom: 2rem;
}

.profile-tile {
  background: #fff;
  border: 1px solid #ddd;
  padding: 1.5rem 1rem; /* More padding */
  border-radius: 8px;
  text-align: center;
  transition: box-shadow 0.2s, border-color 0.2s;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem; /* Space between icon, title, text */
}

.tile-link {
  text-decoration: none;
  color: inherit;
}
.tile-link:hover .profile-tile {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-color: #ccc;
}
/* Style for the active route's tile */
.tile-link.active-tile .profile-tile {
  border-color: #007bff; /* Highlight active tile */
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.3);
}


.tile-icon-placeholder { /* Placeholder style */
  width: 32px;
  height: 32px;
  background-color: #eee;
  border-radius: 4px;
  margin-bottom: 0.5rem;
}
/* Style your actual .tile-icon class based on your icon implementation */
/* .tile-icon { width: 32px; height: 32px; margin-bottom: 0.5rem; } */


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
  /* Add padding/margin if needed to separate from tiles */
  margin-top: 1rem;
}

</style>
