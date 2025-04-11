/**
 * <h1>Import necessary modules for internationalization (i18n) in Vue</h1>
 * This file sets up i18n for the Vue application.
 */
import { createI18n } from 'vue-i18n';

/**
 * <h2>Import language files</h2>
 * Imports the language settings for Norwegian (no), English (en), and Nepali (ne).
 */
import no from './locales/no.json';
import en from './locales/en.json';
import ne from './locales/ne.json';

/**
 * <h2>Get the current language setting</h2>
 * <p>
 * This code tries to get the language setting from three sources:
 * <ul>
 *   <li>localStorage</li>
 *   <li>navigator.language</li>
 *   <li>environment variable <code>VITE_I18N_LOCALE</code></li>
 * </ul>
 * If none of these sources have a language, the default is set to 'no'.
 * </p>
 */
let locale =
  localStorage.getItem('language') ||
  navigator.language ||
  import.meta.env.VITE_I18N_LOCALE ||
  'no';

if (locale.toLowerCase().includes('no')) {
  locale = 'no';
} else if (locale.toLowerCase().includes('en')) {
  locale = 'en';
} else if (locale.toLowerCase().includes('ne')) {
  locale = 'ne';
}

/**
 * <h2>Create i18n instance</h2>
 * <p>
 * Creates an i18n instance for the Vue application and sets up the languages and fallback language.
 * <ul>
 *   <li><b>locale:</b> Set to the selected language (either 'no', 'en', or 'ne')</li>
 *   <li><b>fallbackLocale:</b> The fallback language if the selected language is not available, defaulting to 'no'.</li>
 *   <li><b>messages:</b> Contains translations for Norwegian, English, and Nepali</li>
 * </ul>
 * </p>
 *
 * @returns {import('vue-i18n').I18n} - The Vue i18n instance with configured language options.
 */
export default createI18n({
  locale,
  fallbackLocale: import.meta.env.VITE_I18N_FALLBACK_LOCALE || 'no',
  messages: {
    no,
    en,
    ne,
  },
  globalInjection: true,
  legacy: false,
});
