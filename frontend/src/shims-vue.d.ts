// shims-vue.d.ts

import { VueI18n } from 'vue-i18n';

declare module '@vue/runtime-core' {
  /**
   * Augments the Vue component instance to include i18n properties.
   *
   * This declaration extends Vue's component custom properties to include i18n-related methods,
   * allowing components to access `$i18n` and `$t` for internationalization and translation.
   */
  interface ComponentCustomProperties {
    /**
     * The VueI18n instance used for managing internationalization (i18n) in the app.
     * This provides access to methods such as `$i18n.locale` and `$i18n.setLocaleMessage`.
     * @type {VueI18n}
     */
    $i18n: VueI18n;
    /**
     * The translation function used to fetch translated strings.
     * This is the same as calling `$i18n.t()`, allowing for easy text translation in Vue components.
     * @param key The key of the string to be translated.
     * @returns {string} The translated string based on the current locale.
     */
    $t: VueI18n['t']; // This explicitly tells TypeScript that `$t` should be the `t` method from `VueI18n`
  }
}
