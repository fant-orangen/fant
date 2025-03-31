import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from "@/views/LoginView.vue";
import RegistrationView from "@/views/RegistrationView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
    },
    {
      path: '/browse',
      name: 'browse',
      component: () => import('../views/ItemView.vue'),
    },
    {
      path: '/language-selector',
      name: 'language-selector',
      component: () => import('../views/NavbarLanguageSelectorView.vue'),
    },
    {

      path: '/login', component: LoginView
    },
    {
      path: '/register', component: RegistrationView},
    {
      path: '/create-listing/start',
      name: 'create-listing-start',
      component: () => import('../views/createListingsView/MarkedListingView.vue'),

    }

  ],
})

export default router
