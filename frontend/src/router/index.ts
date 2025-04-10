import { createRouter, createWebHistory } from 'vue-router'

// Import existing Views
import HomeView from '../views/HomeView.vue'
import LoginView from '@/views/auth/LoginView.vue'
import RegistrationView from '@/views/auth/RegistrationView.vue'
import ItemDetailView from "@/views/ItemDetailView.vue";
import CategoryEditView from "@/views/Administrator/CategoryEditView.vue";
import AdminUserManagementView from '@/views/Administrator/AdminUserManagementView.vue';

import ProfileLayout from "@/views/profile/ProfileLayout.vue";
import ProfileAdsView from "@/views/profile/ProfileAdsView.vue";
import FavoritesView from "@/views/profile/FavoritesView.vue";
import ManageMyItemView from "@/views/profile/ManageMyItemView.vue";
import ProfileMyBidsView from "@/views/profile/ProfileMyBidsView.vue";
import MapView from "@/views/MapView.vue";
import ConversationView from "@/views/messaging/ConversationView.vue";
import InboxView from "@/views/messaging/InboxView.vue";
import ProfileFormView from "@/views/profile/ProfileFormView.vue";
import AdminUserEditView from "@/views/Administrator/AdminUserEditView.vue";
import CategoryEdit from "@/components/administrator/CategoryEdit.vue";
import AdminLayout from "@/views/Administrator/AdminLayout.vue";
import type { RouteLocationNormalized } from 'vue-router';

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
    component: AdminLayout,
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        redirect: { name: 'admin-categories' }
      },
      {
        path: 'categories',
        name: 'admin-categories',
        component: CategoryEdit,
        meta: { title: 'Admin - Categories' }
      },
      {
        path: 'users',
        name: 'admin-users',
        component: AdminUserManagementView,
        meta: { title: 'Admin - Users' }
      },
      {
        path: 'users/edit/:id',
        name: 'admin-user-edit',
        component: AdminUserEditView,
        props: true,
        meta: { title: 'Admin - Edit User' }
      }
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
    props: (route: RouteLocationNormalized) => ({
      highlightItemId: route.query.highlightItem || null
    }),
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
  {
    path: '/profile',
    component: ProfileLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'profile-overview',
        component: ProfileFormView,
        meta: { title: 'Profile Overview - Fant' }
      },
      {
        path: 'listings',
        name: 'profile-listings',
        component: ProfileAdsView,
        meta: { title: 'My Listings - Fant' }
      },
      {
        path: 'listings/manage/:id',
        name: 'manage-my-item',
        component: ManageMyItemView,
        props: true,
        meta: { title: 'Manage Item - Fant' }
      },
      {
        path: 'favorites',
        name: 'profile-favorites',
        component: FavoritesView,
        meta: { title: 'My Favorites - Fant' }
      },
      {
        path: 'my-bids',
        name: 'profile-my-bids',
        component: ProfileMyBidsView,
        meta: { title: 'My Bids - Fant' }
      }
    ],
  }
]

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
