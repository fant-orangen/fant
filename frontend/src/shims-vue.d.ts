// shims-vue.d.ts

import { VueI18n } from 'vue-i18n';

declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $i18n: VueI18n;
    $t: VueI18n['t']; // This explicitly tells TypeScript that `$t` should be the `t` method from `VueI18n`
  }
}
