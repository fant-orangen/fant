<template>
  <div class="admin-layout">
    <h1>{{ t('ADMIN_PANEL_TITLE') }}</h1>

    <nav class="admin-nav">
      <router-link :to="{ name: 'admin-categories' }" active-class="active-link">
        {{ t('ADMIN_NAV_CATEGORIES') }}
      </router-link>
      <router-link :to="{ name: 'admin-users' }" active-class="active-link">
        {{ t('ADMIN_NAV_USERS') }}
      </router-link>
    </nav>

    <div class="admin-content">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
</script>

<style scoped>
.admin-layout {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: var(--vt-c-text-dark-1);
}

.admin-nav {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  border-bottom: 2px solid var(--color-border);
  padding-bottom: 0.8rem;
}

.admin-nav a {
  padding: 0.8rem 1rem;
  text-decoration: none;
  color: var(--vt-c-text-dark-2);
  font-weight: 500;
  border-bottom: 3px solid transparent;
  background: none;
  transition: color 0.2s ease, border-color 0.2s ease;
}

.admin-nav a:hover {
  color: var(--vt-c-teal-soft);
}

.admin-nav a.active-link {
  color: var(--vt-c-teal-dark);
  border-bottom-color: var(--vt-c-teal-dark);
}

.admin-content {
  margin-top: 1rem;
  background-color: var(--color-background);
  padding: 1.5rem;
  border-radius: 5px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}
</style>
