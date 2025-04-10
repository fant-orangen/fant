<template>
  <nav class="links">
    <div class="main-links">
      <RouterLink to="/map">
        <IconWithText :icon-src="mapIcon" :text="$t('MAP')" />
      </RouterLink>
    </div>
    <div class="user-links">
      <template v-if="loggedIn">
        <RouterLink to="/create-listing/start">
          <IconWithText :icon-src="addIcon" :text="$t('APP_LISTING_CREATE_NEW')" />
        </RouterLink>
        <RouterLink to="/messages">
          <IconWithText
            :icon-src="hasNewMessages ? notificationNewIcon : notificationIcon"
            :text="$t('INBOX_NAVBAR')"
          />
        </RouterLink>
        <RouterLink to="/profile">
          <IconWithText :icon-src="userIcon" :text="$t('PROFILE_TILE_MY_ACCOUNT_TITLE')" />
        </RouterLink>
        <RouterLink
          v-if="userStore.role === 'ADMIN'"
          to="/admin"
          class="nav-link">
          {{ $t('ADMIN') }}
        </RouterLink>
        <button class="logout-btn" @click="handleLogout">{{ $t('APP_LOGOUT') }}</button>
      </template>
      <template v-else>
        <RouterLink to="/login">{{ $t('LOGIN_NAVBAR') }}</RouterLink>
      </template>
    </div>
  </nav>
</template>

<script setup lang="ts">
/**
 * @fileoverview NavBar component for application-wide navigation header.
 * <p>This component provides functionality for:</p>
 * <ul>
 *   <li>Application header with logo and branding</li>
 *   <li>Responsive layout with different navigation modes</li>
 *   <li>Language selection integration</li>
 *   <li>Adaptive presentation for mobile and desktop viewports</li>
 * </ul>
 */
import { storeToRefs } from 'pinia';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/UserStore';
import IconWithText from '@/components/icons/IconWithText.vue';

// Import icons
import userIcon from '@/assets/icons/user.svg';
import addIcon from '@/assets/icons/add.svg';
import notificationNewIcon from '@/assets/icons/notificationNew.svg';
import notificationIcon from '@/assets/icons/notification.svg';
import mapIcon from '@/assets/icons/map.svg';

/**
 * Router instance for navigation
 * @type {Router}
 */
const router = useRouter();

/**
 * User store for authentication state and operations
 * @type {UserStore}
 */
const userStore = useUserStore();

/**
 * Reactive reference to user authentication state
 * @type {Ref<boolean>}
 */
const { loggedIn } = storeToRefs(userStore);

/**
 * Flag indicating whether user has unread messages
 * @type {Ref<boolean>}
 */
const hasNewMessages = ref(false);

/**
 * Handles user logout action
 * <p>Logs out user and redirects to login page</p>
 */
const handleLogout = () => {
  userStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.logo a {
  text-decoration: none;
  font-size: 1.5rem;
  font-weight: bold;
  color: #000000;
  position: relative;
  background-color: white;
}

.logo a::after {
  content: '';
  position: absolute;
  width: 0;
  height: 2px;
  bottom: -4px;
  left: 0;
  background-color: #333;
  transition: width 0.2s ease-in-out;
}

.logo a:hover::after {
  width: 100%;
}

.links {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.main-links, .user-links {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

nav.links a {
  text-decoration: none;
  color: #333;
  font-size: 1rem;
  font-weight: 500;
  position: relative;
  background-color: white;
}

nav.links a::after {
  content: '';
  position: absolute;
  width: 0;
  height: 2px;
  bottom: -4px;
  left: 0;
  background-color: #333;
  transition: width 0.2s ease-in-out;
}

nav.links a:hover::after,
nav.links a.router-link-active::after {
  width: 100%;
}

.logout-btn {
  background: none;
  border: none;
  color: #333;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  padding: 0;
  margin: 0;
  font-family: inherit;
}
</style>
