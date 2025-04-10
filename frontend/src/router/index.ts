/**
 * Main router configuration for the Fant marketplace application.
 *
 * This module provides:
 * - Route definitions for all application views
 * - Authentication and authorization guards
 * - Nested routing for profile and admin sections
 * - Route metadata for dynamic page titles
 *
 * @fileoverview Router configuration and navigation guards
 */

import { createRouter, createWebHistory } from 'vue-router'

// Import existing Views
import HomeView from '../views/HomeView.vue'
import LoginView from '@/views/auth/LoginView.vue'
import RegistrationView from '@/views/auth/RegistrationView.vue'
import ItemDetailView from "@/views/ItemDetailView.vue";
import CategoryEditView from "@/views/Administrator/CategoryEditView.vue";
import AdminUserManagementView from '@/views/Administrator/AdminUserManagementView.vue';


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
import AdminUserEditView from "@/views/Administrator/AdminUserEditView.vue";
import CategoryEdit from "@/components/administrator/CategoryEdit.vue";
import AdminLayout from "@/views/Administrator/AdminLayout.vue";
// --- End Profile imports ---

/**
 * Route configuration array defining the application's navigation structure.
 * Each route object specifies component mapping, metadata, and optional nested routes.
 */
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
    path: '/admin',
    component: AdminLayout, // Use the new layout component for /admin path
    meta: { requiresAuth: true, requiresAdmin: true }, // Protect the whole section
    children: [
      {
        path: '', // Default admin view (optional, can redirect or show dashboard)
        redirect: { name: 'admin-categories' } // Redirect /admin to /admin/categories by default
      },
      {
        path: 'categories', // Path becomes /admin/categories
        name: 'admin-categories',
        // Point directly to the component used inside CategoryEditView.vue
        component: CategoryEdit,
        meta: { title: 'Admin - Categories' } // Titles set here apply to child routes
      },
      {
        path: 'users', // Path becomes /admin/users
        name: 'admin-users',
        component: AdminUserManagementView,
        meta: { title: 'Admin - Users' }
      },
      {
        // This can remain a top-level admin route, or be nested if preferred
        path: 'users/edit/:id', // Path becomes /admin/users/edit/:id
        name: 'admin-user-edit',
        component: AdminUserEditView,
        props: true,
        meta: { title: 'Admin - Edit User' }
      }
      // Add more admin child routes here (e.g., item moderation)
    ]
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

/**
 * Router instance configured with HTML5 history mode.
 * Includes scroll behavior that restores position for back/forward
 * navigation and scrolls to top for new navigations.
 */
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

/**
 * Navigation guard that runs before each route change.
 * Enforces authentication requirements by checking for a valid token.
 * Redirects unauthenticated users to login with return path preserved.
 * Enforces admin-only access to protected routes based on user role.
 */
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

/**
 * Hook that runs after each successful navigation.
 * Updates the document title based on route metadata.
 * Falls back to default title when route-specific title is not defined.
 */
router.afterEach((to) => {
  setTimeout(() => {
    document.title = to.meta.title as string || 'Fant';
  }, 0);
});

export default router;
