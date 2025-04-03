<template>
  <div class="hamburger-menu">
    <button
      class="hamburger-button"
      @click="toggleMenu"
      :class="{ 'is-active': isOpen }"
      aria-label="Menu"
    >
      <img :src="hamburgerIcon" alt="Menu" class="hamburger-icon" v-if="!isOpen" />
      <span></span>
      <span></span>
      <span></span>
    </button>

    <nav class="dropdown-menu" :class="{ 'is-open': isOpen }">
      <button class="close-button" @click="closeMenu" aria-label="Close menu">
        <img :src="hamburgerIcon" alt="Close" />
      </button>

      <div class="menu-content">
        <div class="main-links">
          <RouterLink to="/map" @click="closeMenu">
            <IconWithText :icon-src="mapIcon" :text="$t('MAP')" />
          </RouterLink>
          <RouterLink to="/create-listing/start" @click="closeMenu">
            <IconWithText :icon-src="addIcon" :text="$t('APP_LISTING_CREATE_NEW')" />
          </RouterLink>
        </div>

        <div class="user-links">
          <NavbarLanguageSelector />
          <template v-if="loggedIn">
            <RouterLink to="/messages" @click="closeMenu">
              <IconWithText
                :icon-src="hasNewMessages ? notificationNewIcon : notificationIcon"
                :text="$t('INBOX_NAVBAR')"
              />
            </RouterLink>
            <RouterLink to="/profile" @click="closeMenu">
              <IconWithText :icon-src="userIcon" :text="$t('PROFILE_TILE_MY_ACCOUNT_TITLE')" />
            </RouterLink>
            <button class="logout-btn" @click="handleLogout">
              {{ $t('APP_LOGOUT') }}
            </button>
          </template>
          <template v-else>
            <RouterLink to="/login" @click="closeMenu">
              {{ $t('LOGIN_NAVBAR') }}
            </RouterLink>
          </template>
        </div>
      </div>
    </nav>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { storeToRefs } from 'pinia';
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
import hamburgerIcon from '@/assets/icons/hamburger.svg';

const router = useRouter();
const userStore = useUserStore();
const { loggedIn } = storeToRefs(userStore);
const hasNewMessages = ref(false);
const isOpen = ref(false);

const toggleMenu = () => {
  isOpen.value = !isOpen.value;
  if (isOpen.value) {
    document.body.style.overflow = 'hidden';
  } else {
    document.body.style.overflow = '';
  }
};

const closeMenu = () => {
  isOpen.value = false;
  document.body.style.overflow = '';
};

const handleLogout = () => {
  closeMenu();
  userStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.hamburger-menu {
  position: relative;
  z-index: 1000;
}
.dropdown-menu {
  position: fixed;
  z-index: 999;
}

.hamburger-button {
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  width: 30px;
  height: 24px;
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 0;
}

.hamburger-button span {
  width: 30px;
  height: 2px;
  background: #333;
  border-radius: 10px;
  transition: all 0.3s linear;
  position: relative;
  transform-origin: 1px;
}

.hamburger-button.is-active span:first-child {
  transform: rotate(45deg);
}

.hamburger-button.is-active span:nth-child(2) {
  opacity: 0;
  transform: translateX(20px);
}

.hamburger-button.is-active span:nth-child(3) {
  transform: rotate(-45deg);
}

.dropdown-menu {
  position: fixed;
  top: 0;
  right: -100%;
  width: 80%;
  max-width: 300px;
  height: 100vh;
  background: white;
  padding: 5rem 2rem 2rem;
  transition: right 0.3s ease-in-out;
  box-shadow: -2px 0 10px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
}

.dropdown-menu.is-open {
  right: 0;
}

.menu-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.main-links,
.user-links {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.dropdown-menu a {
  text-decoration: none;
  color: #333;
  font-size: 1rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  padding: 0.5rem 0;
  transition: color 0.2s ease;
}

.dropdown-menu a:hover {
  color: #000;
}

.logout-btn {
  background: none;
  border: none;
  color: #333;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  padding: 0.5rem 0;
  text-align: left;
  width: 100%;
  font-family: inherit;
}
.close-button{
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: none;
  border: none;
  padding: 0.5rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}
.close-button img {
  width: 30px;
  height: 30px;
}
</style>
