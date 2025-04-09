<script setup lang="ts">
import { useUserStore } from '@/stores/UserStore';
import ProfileFormView from '@/views/profile/ProfileFormView.vue';
import { useI18n } from 'vue-i18n';

const userStore = useUserStore();
const { t } = useI18n();

</script>

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

<style scoped>
.profile-overview {
  /* Add specific styles if needed, or rely on layout */
  padding: 1rem;
  background-color: #f9f9f9;
  border-radius: 8px;
  border: 1px solid #eee;
}
h1 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  border-bottom: 1px solid #eee;
  padding-bottom: 0.5rem;
}
</style>
