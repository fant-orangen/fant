<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
// Currently unused
// Initialize i18n and router
const { t } = useI18n();
const router = useRouter();

/**
 * Define an array of card objects. Each object represents a section of the profile,
 * such as "My Favorites" or "My Listings". Each card has a title, description,
 * and the route name to navigate to when clicked.
 */
const cards = ref([
  {
    title: t('MY_FAVORITES'),
    description: t('PROFILE_MY_FAVORITES_DESC'),
    routeName: 'profile-favorites'
  },
  {
    title: t('PROFILE_MY_LISTINGS'),
    description: t('PROFILE_MY_LISTINGS_DESC'),
    routeName: 'profile-listings'
  },
  // Add additional cards as needed:
  // {
  //   title: t('PROFILE_SETTINGS'),
  //   description: t('PROFILE_SETTINGS_DESC'),
  //   routeName: 'profile-settings'
  // },
]);

/**
 * Navigates to the route associated with the clicked card.
 * @param routeName - The name of the route to navigate to.
 */
function navigateTo(routeName: string) {
  router.push({ name: routeName });
}
</script>

<template>
  <section class="profile-cards">
    <h2>{{ t('PROFILE_CARDS_TITLE') }}</h2>
    <div class="card-grid">
      <!-- Loop over the cards array and display each as a clickable card -->
      <div
        v-for="card in cards"
        :key="card.routeName"
        class="card"
        @click="navigateTo(card.routeName)"
      >
        <h3>{{ card.title }}</h3>
        <p>{{ card.description }}</p>
      </div>
    </div>
  </section>
</template>

<style scoped>
.profile-cards {
  margin-top: 2rem;
  padding: 1rem;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1rem;
}

.card {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1rem;
  text-align: center;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}
</style>
