import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import RegistrationView from '@/views/RegistrationView.vue'
import ItemDetailView from "@/views/ItemDetailView.vue";
import CategoryEditView from "@/views/Administrator/CategoryEditView.vue";

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: { title: 'Home - Fant' }
  },
  {
    path: '/item-detail/:id',
    name: 'item-detail',
    component: ItemDetailView,
    props: true,
    meta: { title: 'Item - Fant' }
  },
  {
    path: '/about',
    name: 'about',
    component: () => import('../views/AboutView.vue'),
    meta: { title: 'About - Fant' }
  },
  {
    path: '/browse',
    name: 'browse',
    component: () => import('../views/ItemView.vue'),
    meta: { title: 'Browse Listings - Fant' }
  },
  {
    path: '/language-selector',
    name: 'language-selector',
    component: () => import('../views/NavbarLanguageSelectorView.vue'),
    meta: { title: 'Select Language - Fant' }
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { title: 'Login - Fant' }
  },
  {
    path: '/register',
    name: 'register',
    component: RegistrationView,
    meta: { title: 'Register - Fant' }
  },
  {
    path: '/create-listing/start',
    name: 'create-listing-start',
    component: () => import('../views/createListingsView/MarketListingView.vue'),
    meta: { title: 'Create Listing - Fant', requiresAuth: true }
  },

  {
    path: '/administrator/category',
    name: 'administrator-category',
    component: CategoryEditView,
    meta: { title: 'Administrator - category - Fant'}
  },

  // Dynamic Route for CategoryView.vue
  {
    path: '/category/:categoryKey',
    name: 'category',
    component: () => import('../views/CategoryView.vue'),
    props: true,
    meta: { title: 'Category - Fant' }
  }

]
// Scroll to top when route changes
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// Update the document title based on the route's meta title
router.afterEach((to) => {
  document.title = to.meta.title as string || 'Fant'
})

export default router
