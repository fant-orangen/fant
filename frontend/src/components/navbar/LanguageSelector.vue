<template>
  <div class="language-selector" @click="changeLanguage" :tabindex="0" role="button" aria-label="Change language">
    <img :src="countryImages[getLanguageCode() as LanguageCode]" :alt="`${getLanguageCode()} flag`" />
    <span>{{ getLanguageCode().toUpperCase() }}</span>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import NorwayFlag from '@/assets/images/flags/no-flag.png';
import UnitedKingdomFlag from '@/assets/images/flags/uk-flag.png';
import NepaliFlag from '@/assets/images/flags/ne-flag.png';

type LanguageCode = 'no' | 'en' | 'ne' ;

const i18n = useI18n();

const langs: LanguageCode[] = ['no', 'en', 'ne'];
const countryImages: Record<LanguageCode, string> = {
  no: NorwayFlag,
  en: UnitedKingdomFlag,
  ne: NepaliFlag,
};

function getLanguageCode(): string {
  return i18n.locale.value || 'en';
}

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
