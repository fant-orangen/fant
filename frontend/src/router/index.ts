import { createRouter, createWebHistory } from 'vue-router'

// Import existing Views
import HomeView from '../views/HomeView.vue'
import LoginView from '@/views/auth/LoginView.vue'
import RegistrationView from '@/views/auth/RegistrationView.vue'
import ItemDetailView from "@/views/ItemDetailView.vue";
import CategoryEditView from "@/views/Administrator/CategoryEditView.vue";

// --- Import Profile related views ---
import ProfileLayout from "@/views/profile/ProfileLayout.vue";
import ProfileAdsView from "@/views/profile/ProfileAdsView.vue";
import FavoritesView from "@/views/profile/FavoritesView.vue";
import ManageMyItemView from "@/views/profile/ManageMyItemView.vue";
import ProfileMyBidsView from "@/views/profile/ProfileMyBidsView.vue"; // <-- Import new view
import MapView from "@/views/MapView.vue";
import ConversationView from "@/views/messaging/ConversationView.vue";
import InboxView from "@/views/messaging/InboxView.vue";
import ProfileFormView from "@/views/profile/ProfileFormView.vue";
// --- End Profile imports ---

const routes = [
  // ... other routes (home, item-detail, login, etc.) ...
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: { title: 'Home - Fant' }
  },
  {
    path: '/item-detail/:id', // Public item detail view
    name: 'item-detail',
    component: ItemDetailView,
    props: true,
    meta: { title: 'Item - Fant' }
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
    path: '/edit-listing/:itemId',
    name: 'edit-listing',
    component: () => import('@/views/EditListingView.vue'),
  },
  {
    path: '/admin',
    name: 'admin',
    component: CategoryEditView,
    meta: { title: 'Administrator - category - Fant', requiresAuth: true, requiresAdmin: true }
  },
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

  {
    path: '/messages',
    name: 'messages-inbox',
    component: InboxView,
    meta: { title: "Message Overview - Fant", requiresAuth: true }
  },

  {
    path: '/messages/:conversationId',
    name: 'messages-conversation',
    component: ConversationView,
    props: true,
    meta: { title: "Conversation - Fant", requiresAuth: true }
  },


  // Profile Section with nested routes
  {
    path: '/profile',
    component: ProfileLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '', // Overview at /profile
        name: 'profile-overview',
        component: ProfileFormView,
        meta: { title: 'Profile Overview - Fant' }
      },
      {
        path: 'listings', // My Listings at /profile/listings
        name: 'profile-listings',
        component: ProfileAdsView,
        meta: { title: 'My Listings - Fant' }
      },
      {
        path: 'listings/manage/:id', // Manage specific listing at /profile/listings/manage/:id
        name: 'manage-my-item',
        component: ManageMyItemView,
        props: true,
        meta: { title: 'Manage Item - Fant' }
      },
      {
        path: 'favorites', // My Favorites at /profile/favorites
        name: 'profile-favorites',
        component: FavoritesView,
        meta: { title: 'My Favorites - Fant' }
      },
      { // <-- Add this new route object
        path: 'my-bids', // My Bids at /profile/my-bids
        name: 'profile-my-bids',
        component: ProfileMyBidsView,
        meta: { title: 'My Bids - Fant' }
        // requiresAuth is inherited from parent '/profile'
      },
    ],
  }
  // ... fallback route ...
]

// ... rest of router setup (createRouter, scrollBehavior, beforeEach, afterEach) ...
// Ensure your beforeEach guard correctly checks for authentication
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { top: 0 };
    }
  }
});

router.beforeEach((to, from, next) => {
  const isAuthenticated = !!localStorage.getItem('token');
  const userRole = localStorage.getItem('role');

  if (to.matched.some(record => record.meta.requiresAuth) && !isAuthenticated) {
    next({ name: 'login', query: { redirect: to.fullPath } });
  } else if (to.matched.some(record => record.meta.requiresAdmin) && userRole !== 'ADMIN') {
    next({ name: 'home' });
  }
  else {
    next();
  }
});

router.afterEach((to) => {
  setTimeout(() => {
    document.title = to.meta.title as string || 'Fant';
  }, 0);
});

export default router;
