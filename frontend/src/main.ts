/**
 * Global window polyfill.
 *
 * This file provides a polyfill that assigns the window object to window.global.
 * It's needed to fix compatibility issues between browser environments and
 * certain libraries that expect the global object to be available.
 *
 * This polyfill is particularly useful when working with libraries that were
 * originally designed for Node.js environments where 'global' is available,
 * but need to run in browser environments where 'window' is the global object.
 */
// Import global styles
import './assets/main.css'
import '@/assets/styles/buttons/categoryButtons.css'
import '@/assets/styles/cards/categoryGrid.css'
import '@/assets/styles/login/loginRegistration.css'
import 'leaflet/dist/leaflet.css';
import './global.js'

// Import Vue and plugins
import { createApp } from 'vue'
import { createPinia } from 'pinia'

// Import root component and configurations
import App from './App.vue'
import router from './router'
import i18n from './i18n'

/**
 * Vue application instance.
 *
 * Creates the main Vue application with the App component as its root.
 */
const app = createApp(App)

// Register plugins
app.use(createPinia())
app.use(router)
app.use(i18n)

// Mount the application to the DOM element with id 'app'
app.mount('#app')
