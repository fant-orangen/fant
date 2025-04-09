<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useUserStore } from '@/stores/UserStore'
import ProfileFormView from '@/views/profile/ProfileFormView.vue'
import { useI18n } from 'vue-i18n';

const userStore = useUserStore();
const { t } = useI18n();
const loading = ref(false);
const error = ref('');

/**
 * Loads the user's profile from backend by calling fetchProfile from UserStore
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
