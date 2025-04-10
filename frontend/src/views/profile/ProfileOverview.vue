<template>
  <section class="profile-overview">
    <h1>{{ t('PROFILE_TILE_MY_ACCOUNT_TITLE') }}</h1>

    <div v-if="userStore.profile">
      <ProfileFormView :profile="userStore.profile" />
    </div>
    <div v-else>
      <p>{{ t('PROFILE_INFO_UNAVAILABLE') }}</p>
    </div>
  </section>
</template>

<script setup lang="ts">
/**
 * Profile Overview component.
 *
 * This component serves as the main profile dashboard for users to view and edit their account information.
 * It acts as a container for the ProfileFormView component, providing the necessary layout and styling.
 *
 * Features:
 * - Displays the user's account information in an editable form
 * - Shows a fallback message when profile data is unavailable
 * - Integrates with the user store for data management
 * - Uses i18n for localized text content
 * - Responsive styling with consistent design language
 *
 * @component ProfileOverview
 * @requires vue
 * @requires vue-i18n
 * @requires @/stores/UserStore
 * @requires @/views/profile/ProfileFormView.vue
 * @displayName ProfileOverview
 */
import { useUserStore } from '@/stores/UserStore';
import ProfileFormView from '@/views/profile/ProfileFormView.vue';
import { useI18n } from 'vue-i18n';

/**
 * User store instance to access profile data and authentication state.
 *
 * @type {import('@/stores/UserStore').UserStore}
 */
const userStore = useUserStore();

/**
 * i18n translation function for localized text content.
 *
 * @type {function(string, ...any[]): string}
 */
const { t } = useI18n();

</script>


<style scoped>
.profile-overview {
  padding: 1rem;
  background-color: var(--color-background-soft);
  border-radius: 8px;
  border: 1px solid var(--color-border);
}

h1 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  border-bottom: 1px solid var(--color-border);
  padding-bottom: 0.5rem;
  color: var(--vt-c-text-dark-1);
}
</style>
