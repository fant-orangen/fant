<template>
  <header class="nav">
    <div class="logo">
      <RouterLink to="/">fant<span class="domain">.no</span></RouterLink>
    </div>
    <nav class="links">
      <div class="main-links">
        <RouterLink to="/map">
          <IconWithText :icon-src="mapIcon" :text="$t('MAP')" />
        </RouterLink>
        <RouterLink to="/create-listing/start">
          <IconWithText :icon-src="addIcon" :text="$t('APP_LISTING_CREATE_NEW')" />
        </RouterLink>
      </div>
      <div class="user-links">
        <NavbarLanguageSelector />
        <template v-if="loggedIn">
          <RouterLink to="/messages">
            <IconWithText
              :icon-src="hasNewMessages ? notificationNewIcon : notificationIcon"
              :text="$t('INBOX_NAVBAR')"
            />
          </RouterLink>
          <RouterLink to="/profile">
            <IconWithText :icon-src="userIcon" :text="$t('PROFILE_TILE_MY_ACCOUNT_TITLE')" />
          </RouterLink>
          <button class="logout-btn" @click="handleLogout">{{ $t('APP_LOGOUT') }}</button>
        </template>
        <template v-else>
          <RouterLink to="/login">{{ $t('LOGIN_NAVBAR') }}</RouterLink>
        </template>
      </div>
    </nav>
  </header>
</template>

<script setup lang="ts">
import { storeToRefs } from 'pinia';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/UserStore';
import NavbarLanguageSelector from './LanguageSelector.vue';
import IconWithText from '@/components/icons/IconWithText.vue';

// Import icons
import userIcon from '@/assets/icons/user.svg';
import addIcon from '@/assets/icons/add.svg';
import notificationNewIcon from '@/assets/icons/notificationNew.svg';
import notificationIcon from '@/assets/icons/notification.svg';
import mapIcon from '@/assets/icons/map.svg';

const router = useRouter();
const userStore = useUserStore();
const { loggedIn } = storeToRefs(userStore);

// You can add this computed property based on your message store
const hasNewMessages = ref(false);

const handleLogout = () => {
  userStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: #fff;
  border-bottom: 1px solid #eee;
}

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

.domain {
  color: #000000;
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
