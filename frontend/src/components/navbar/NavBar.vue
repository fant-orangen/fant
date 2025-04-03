<template>
  <header class="nav">
    <div class="logo">
      <RouterLink to="/">fant<span class="domain">.no</span></RouterLink>
    </div>

    <!-- Navigation container -->
    <div class="nav-container">
      <!-- Wide screen navigation -->
      <WideScreenNavBar class="desktop-nav" />
      <!-- Mobile hamburger menu -->
      <HamburgerDropDownMenu class="mobile-nav" />
    </div>
  </header>
</template>

<script setup lang="ts">
import WideScreenNavBar from './WideScreenNavBar.vue';
import HamburgerDropDownMenu from './HamburgerDropDownMenu.vue';
</script>

<style scoped>
.nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: #fff;
  border-bottom: 1px solid #eee;
  width: 100%;
  box-sizing: border-box;
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

.nav-container {
  flex: 1;
  display: flex;
  justify-content: flex-end;
}

/* Desktop styles */
@media (min-width: 1200px) {
  .mobile-nav {
    display: none;
  }
  .desktop-nav {
    display: flex;
  }
}

/* Tablet styles */
@media (min-width: 810px) and (max-width: 1199px) {
  .mobile-nav {
    display: none;
  }
  .desktop-nav {
    display: flex;
  }
}

/* Mobile styles */
@media (max-width: 810px) {
  .desktop-nav {
    display: none;
  }
  .mobile-nav {
    display: block;
  }

  .nav {
    padding: 1rem;
  }
}

/* Extra small screens */
@media (max-width: 480px) {
  .nav {
    padding: 0.75rem;
  }

  .logo a {
    font-size: 1.25rem;
  }
}
</style>
