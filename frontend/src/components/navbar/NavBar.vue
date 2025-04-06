<template>
  <header class="nav">
    <div class="logo-area">
      <div class="logo">
        <RouterLink to="/">fant<span class="domain">.no</span></RouterLink>
      </div>
      <NavbarLanguageSelector class="language-selector-desktop" />
    </div>

    <div class="nav-container">
      <WideScreenNavBar class="desktop-nav" />
      <HamburgerDropDownMenu class="mobile-nav" />
    </div>
  </header>
</template>

<script setup lang="ts">
// Import the language selector
import NavbarLanguageSelector from './LanguageSelector.vue'; // Adjust path if needed
import WideScreenNavBar from './WideScreenNavBar.vue';
import HamburgerDropDownMenu from './HamburgerDropDownMenu.vue';
</script>

<style scoped>
.nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 2rem;
  background-color: #fff;
  border-bottom: 1px solid #eee;
  width: 100%;
  box-sizing: border-box;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.logo a {
  text-decoration: none;
  font-size: 1.5rem;
  font-weight: bold;
  color: #000000;
  position: relative;
  background-color: white;
  display: block;
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
  display: flex;
  justify-content: flex-end;
}

.language-selector-desktop {
  display: none;
}

/* Desktop styles */
@media (min-width: 810px) {
  .language-selector-desktop {
    display: block;
  }
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
    padding: 0.5rem 1rem;
  }
  .logo-area {
    gap: 0.75rem;
  }
}

/* Extra small screens */
@media (max-width: 480px) {
  .nav {
    padding: 0.4rem 0.75rem;
  }

  .logo a {
    font-size: 1.25rem;
  }
}
</style>
