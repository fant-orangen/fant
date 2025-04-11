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

<script setup lang="ts">
/**
 * @fileoverview ProfileCard component for displaying user profile navigation options.
 * <p>This component provides functionality for:</p>
 * <ul>
 *   <li>Grid-based layout of profile section cards</li>
 *   <li>Clickable navigation to profile subsections</li>
 *   <li>Internationalized card titles and descriptions</li>
 *   <li>Visual feedback on hover with elevation effects</li>
 * </ul>
 */
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';

/**
 * I18n instance for internationalization
 * @type {I18n}
 */
const { t } = useI18n();

/**
 * Router instance for navigation
 * @type {Router}
 */
const router = useRouter();

/**
 * Define an array of card objects. Each object represents a section of the profile,
 * such as "My Favorites" or "My Listings". Each card has a title, description,
 * and the route name to navigate to when clicked.
 * @type {Ref<CardItem[]>}
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
]);

/**
 * Navigates to the route associated with the clicked card
 * @param {string} routeName - The name of the route to navigate to
 */
function navigateTo(routeName: string) {
  router.push({ name: routeName });
}
</script>

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
