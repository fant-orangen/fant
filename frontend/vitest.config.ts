import { defineConfig, mergeConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import viteConfig from './vite.config'
import tsconfigPaths from 'vite-tsconfig-paths'

export default mergeConfig(
  viteConfig,
  defineConfig({
    plugins: [
      vue(),
      tsconfigPaths(), // <-- plugin reads "paths" from tsconfig
    ],
    test: {
      environment: 'jsdom',
      // no need for manual alias if using tsconfigPaths plugin
    },
  })
)
