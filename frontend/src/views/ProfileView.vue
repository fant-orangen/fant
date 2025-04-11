<script setup lang="ts">
/**
 * Profile View component.
 *
 * This component displays the user's profile information and provides
 * a form interface for viewing and editing profile details. It handles
 * loading the profile data from the backend via the UserStore.
 *
 * @component
 */
import { onMounted, ref } from "vue";
import { useUserStore } from '@/stores/UserStore'
import ProfileFormView from '@/views/profile/ProfileFormView.vue'
import { useI18n } from 'vue-i18n';

const userStore = useUserStore();
const { t } = useI18n();
const loading = ref(false);
const error = ref('');

/**
 * Loads the user's profile from backend by calling fetchProfile from UserStore.
 * Handles loading states and error conditions.
 *
 * @returns {Promise<void>}
 */
async function loadProfile(){
  try {
    loading.value = true;
    await userStore.fetchProfile();
  } catch (err: unknown) {
    if (err instanceof Error) {
      error.value = t('PROFILE_LOAD_ERROR') + ': ' + err.message;
    }
  } finally {
    loading.value = false;
  }
}
/**
 * Lifecycle hook that triggers profile data loading when the component mounts.
 */
onMounted(() => {
  loadProfile();
});
</script>
<template>
  <section class="profile-view">
    <h1>{{ t('PROFILE_TITLE') }}</h1>
    <div v-if="loading">{{ t('LOADING') }}</div>
    <ProfileFormView :profile="userStore.profile"/>
    <p v-if="error" class="error">{{ error }}</p>
  </section>
</template>
