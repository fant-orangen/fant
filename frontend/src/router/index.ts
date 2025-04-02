import { createRouter, createWebHistory } from 'vue-router'

// Import Views used in multiple places directly
import HomeView from '../views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import RegistrationView from '@/views/RegistrationView.vue'
import ItemDetailView from "@/views/ItemDetailView.vue"; // Added from version 1
import CategoryEditView from "@/views/Administrator/CategoryEditView.vue"; // Added from version 1

// Import Profile related views (consider importing ProfileLayout directly too if preferred)
import ProfileLayout from "@/views/profile/ProfileLayout.vue"; // Added from version 2
import ProfileOverview from '@/views/profile/ProfileOverview.vue'; // Added from version 2
import ProfileAdsView from "@/views/profile/ProfileAdsView.vue"; // Added from version 2
import FavoritesView from "@/views/profile/FavoritesView.vue"; // Added from version 2

const routes = [
  // --- Common Routes (from both versions) ---
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: { title: 'Home - Fant' }
  },
  {
    path: '/about',
    name: 'about',
    // Using dynamic import for less common views
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
    path: '/category/:categoryKey', // Dynamic Route for CategoryView.vue
    name: 'category',
    component: () => import('../views/CategoryView.vue'),
    props: true,
    meta: { title: 'Category - Fant' }
  },

  // --- Routes added from Version 1 ---
  {
    path: '/item-detail/:id',
    name: 'item-detail',
    component: ItemDetailView,
    props: true,
    meta: { title: 'Item - Fant' }
  },
  {
    path: '/administrator/category',
    name: 'administrator-category',
    component: CategoryEditView,
    meta: { title: 'Administrator - category - Fant'} // Consider adding requiresAuth: true, requiresAdmin: true ?
  },

  // --- Routes added from Version 2 ---
  {
    path: '/profile',
    component: ProfileLayout, // Use the layout component
    meta: { requiresAuth: true }, // Ensure user must be logged in
    children: [
      {
        path: '', // Default child route (e.g., /profile) maps to /profile
        name: 'profile-overview',
        component: ProfileOverview,
      },
      {
        path: 'listings', // Maps to /profile/listings
        name: 'profile-listings',
        component: ProfileAdsView,
      },
      {
        path: 'favorites', // Maps to /profile/favorites
        name: 'profile-favorites',
        component: FavoritesView,
      },
      // Add other profile sub-routes like settings, etc. here if needed
    ],
  }
]

// Scroll to top when route changes
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes, // Use the combined routes array
  scrollBehavior() {
    // always scroll to top
    return { top: 0 }
  }
})

// Update the document title based on the route's meta title
router.afterEach((to) => {
  document.title = to.meta.title as string || 'Fant'
})

export default router
