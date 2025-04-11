<template>
  <div class="language-selector" @click="changeLanguage" :tabindex="0" role="button" aria-label="Change language">
    <img :src="countryImages[getLanguageCode() as LanguageCode]" :alt="`${getLanguageCode()} flag`" />
    <span>{{ getLanguageCode().toUpperCase() }}</span>
  </div>
</template>

<script setup lang="ts">
/**
 * @fileoverview LanguageSelector component for switching between application languages.
 * <p>This component provides functionality for:</p>
 * <ul>
 *   <li>Toggling between supported languages (Norwegian, English, Nepali)</li>
 *   <li>Visual indication of current language with country flags</li>
 *   <li>Persistent language selection via localStorage</li>
 *   <li>Keyboard accessibility</li>
 * </ul>
 */
import { useI18n } from 'vue-i18n';
import NorwayFlag from '@/assets/images/flags/no-flag.png';
import UnitedKingdomFlag from '@/assets/images/flags/uk-flag.png';
import NepaliFlag from '@/assets/images/flags/ne-flag.png';

/**
 * Supported language codes
 * @typedef {'no'|'en'|'ne'} LanguageCode
 */
type LanguageCode = 'no' | 'en' | 'ne' ;

/**
 * I18n instance for internationalization
 * @type {I18n}
 */
const i18n = useI18n();

/**
 * Array of supported language codes
 * @type {LanguageCode[]}
 */
const langs: LanguageCode[] = ['no', 'en', 'ne'];

/**
 * Mapping of language codes to their respective flag images
 * @type {Record<LanguageCode, string>}
 */
const countryImages: Record<LanguageCode, string> = {
  no: NorwayFlag,
  en: UnitedKingdomFlag,
  ne: NepaliFlag,
};

/**
 * Gets the current language code from the i18n instance
 * @returns {string} The current language code
 */
function getLanguageCode(): string {
  return i18n.locale.value || 'en';
}

/**
 * Changes the application language to the next available option
 * <p>Cycles through languages in the order defined in the langs array</p>
 * <p>Persists selection to localStorage</p>
 */
function changeLanguage(): void {
  const currentLang = getLanguageCode();
  // Find the index of the current language and move to the next one
  const currentIndex = langs.indexOf(currentLang as LanguageCode);
  const nextIndex = (currentIndex + 1) % langs.length; // Cycle through languages
  const nextLang = langs[nextIndex];

  localStorage.setItem('language', nextLang);
  i18n.locale.value = nextLang;
}
</script>

<style scoped>
.language-selector {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.language-selector:hover {
  background-color: #f5f5f5;
}

.language-selector img {
  width: 20px;
  height: 20px;
  border-radius: 50%;
}

.language-selector span {
  font-weight: 500;
}
</style>
