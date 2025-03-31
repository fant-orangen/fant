<template>
  <li :class="['navbar', { 'mobile-navbar': mobile }]">
    <div @click="changeLanguage" :tabindex="0" role="button" aria-label="Change language">
      <img :src="countryImages[getLanguageCode()]" alt="Language flag" />
      <div>{{ getLanguageCode().toUpperCase() }}</div>
    </div>
  </li>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import NorwayFlag from '@/assets/images/flags/no-flag.png';
import UnitedKingdomFlag from '@/assets/images/flags/uk-flag.png';

/**
 * LanguageChanger Component
 * This component allows users to toggle between Norwegian and English language.
 * It displays the language flag and code, and updates the locale.
 */
export default defineComponent({
  name: 'LanguageChanger',
  props: {
    mobile: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      langs: ['no', 'en'] as string[],
      countryImages: {
        no: NorwayFlag,
        en: UnitedKingdomFlag,
      } as { [key: string]: string },
    };
  },
  methods: {
    /**
     * Retrieves the current language code.
     * Defaults to 'en' if the locale is undefined.
     * @returns {string} Current language code
     */
    getLanguageCode(): string {
      return this.$i18n.locale || 'en';
    },
    /**
     * Toggles the language between Norwegian ('no') and English ('en').
     * Saves the selected language in localStorage and updates the locale.
     * @returns {void}
     */
    changeLanguage(): void {
      const currentLang = this.getLanguageCode();
      const nextLang = currentLang === this.langs[0] ? this.langs[1] : this.langs[0];
      localStorage.setItem('language', nextLang);
      this.$i18n.locale = nextLang;
      console.log(`Language changed to: ${nextLang}`);
    },
  },
});
</script>

<style lang="scss" scoped>
.no_dot {
  list-style-type: none;
}

.choose_lang {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 7px 10px;
  transition: all 0.2s ease-in;
  border-radius: 5px;
  font-size: 16px;

  img {
    width: 20px;
    height: 20px;

    &:hover {
      cursor: pointer;
    }
  }

  div {
    font-family: 'Apple', sans-serif;
    font-weight: 400;
    margin-left: 10px;
    user-select: none;
  }

  &:hover {
    color: #fff;
    cursor: pointer;
    background-color: #000;
  }
}
</style>
