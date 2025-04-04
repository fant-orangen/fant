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

type LanguageCode = 'no' | 'en';

const i18n = useI18n();

const langs: LanguageCode[] = ['no', 'en'];
const countryImages: Record<LanguageCode, string> = {
  no: NorwayFlag,
  en: UnitedKingdomFlag,
};

function getLanguageCode(): string {
  return i18n.locale.value || 'en';
}

function changeLanguage(): void {
  const currentLang = getLanguageCode();
  const nextLang = currentLang === langs[0] ? langs[1] : langs[0];
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
