import './assets/main.css'
import '@/assets/styles/categoryButtons.css'
import '@/assets/styles/categoryGrid.css'
import '@/assets/styles/login/loginRegistration.css'
import 'leaflet/dist/leaflet.css';
import './global.js'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import i18n from './i18n'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(i18n)

app.mount('#app')
