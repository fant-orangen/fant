import { createRouter, createWebHistory } from 'vue-router'

// Import existing Views
import HomeView from '../views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import RegistrationView from '@/views/RegistrationView.vue'
import ItemDetailView from "@/views/ItemDetailView.vue";
import CategoryEditView from "@/views/Administrator/CategoryEditView.vue";

// --- Import Profile related views ---
import ProfileLayout from "@/views/profile/ProfileLayout.vue";
import ProfileOverview from '@/views/profile/ProfileOverview.vue';
import ProfileAdsView from "@/views/profile/ProfileAdsView.vue";
import FavoritesView from "@/views/profile/FavoritesView.vue";
import MapView from "@/views/MapView.vue";
// --- End Profile imports ---

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
    component: () => import('../views/NewListingView.vue'),
    meta: { title: 'Create Listing - Fant', requiresAuth: true }
  },
  {
    path: '/administrator/category',
    name: 'administrator-category',
    component: CategoryEditView,
    meta: { title: 'Administrator - category - Fant'} // Consider adding requiresAuth, requiresAdmin?
  },
  // Dynamic Route for CategoryView.vue
  {
    path: '/category/:categoryKey',
    name: 'category',
    component: () => import('../views/CategoryView.vue'),
    props: true,
    meta: { title: 'Category - Fant' }
  },
  {
    path: '/map',
    name: 'map',
    component: MapView,
    props: true,
    meta: { title: 'Map - Fant' }
  },

  // --- ADDED Profile Routes ---
  {
    path: '/profile',
    component: ProfileLayout, // Use the layout component for all profile children
    meta: { requiresAuth: true }, // Ensure user must be logged in to access profile pages
    children: [
      {
        path: '', // Default child route, maps to /profile
        name: 'profile-overview',
        component: ProfileOverview,
        meta: { title: 'Profile Overview - Fant' } // Added title
      },
      {
        path: 'listings', // Maps to /profile/listings
        name: 'profile-listings',
        component: ProfileAdsView,
        meta: { title: 'My Listings - Fant' } // Added title
      },
      {
        path: 'favorites', // Maps to /profile/favorites
        name: 'profile-favorites',
        component: FavoritesView,
        meta: { title: 'My Favorites - Fant' } // Added title
      },
      // Add other profile sub-routes like settings here if needed
    ],
  }
  // --- END Profile Routes ---

]

// Scroll to top when route changes
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior() {
    // always scroll to top
    return { top: 0 }
  }
})

// Update the document title based on the route's meta title
router.afterEach((to) => {
  // Use the title from the matched route's meta field
  document.title = to.meta.title as string || 'Fant';
})

export default router
