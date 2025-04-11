<template>
  <div class="admin-user-management">
    <h2>{{ t('ADMIN_USER_MGMT_TITLE') }}</h2>

    <div v-if="isLoading" class="loading-indicator">
      {{ t('LOADING_USERS') }}
    </div>

    <div v-else-if="error" class="error-message">
      <p>{{ t('ERROR_LOADING_USERS') }}: {{ error }}</p>
      <button @click="fetchUsers(currentPage)">{{ t('RETRY') }}</button>
    </div>

    <div v-else-if="users && users.content.length > 0">
      <table class="user-table">
        <thead>
        <tr>
          <th>{{ t('ADMIN_TABLE_ID') }}</th>
          <th>{{ t('ADMIN_TABLE_DISPLAY_NAME') }}</th>
          <th>{{ t('EMAIL') }}</th>
          <th>{{ t('ADMIN_TABLE_ROLE') }}</th>
          <th>{{ t('ADMIN_TABLE_ACTIONS') }}</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="user in users.content" :key="user.id">
          <td>{{ user.id }}</td>
          <td>{{ user.displayName }}</td>
          <td>{{ user.email }}</td>
          <td>{{ user.role }}</td>
          <td>
            <button @click="handleEditUser(user.id)" class="edit-button">
              {{ t('EDIT') }}
            </button>
            <button @click="confirmDeleteUser(user.id, user.displayName)" class="delete-button">
              {{ t('DELETE') }}
            </button>
          </td>
        </tr>
        </tbody>
      </table>

      <div class="pagination-controls">
        <button @click="goToPage(currentPage - 1)" :disabled="!canGoPrevious">
          {{ t('PAGINATION_PREVIOUS') }}
        </button>
        <span>{{ t('PAGINATION_INFO', { current: currentPage + 1, total: totalPages }) }}</span>
        <button @click="goToPage(currentPage + 1)" :disabled="!canGoNext">
          {{ t('PAGINATION_NEXT') }}
        </button>
      </div>
    </div>

    <div v-else class="no-users-message">
      {{ t('ADMIN_NO_USERS_FOUND') }}
    </div>

  </div>
</template>

<script setup lang="ts">
/**
 * Admin User Management View component.
 *
 * Provides an interface for administrators to view, edit, and delete users in the system.
 * This component displays a paginated table of all users with key information and action buttons.
 *
 * Features:
 * - Paginated display of user accounts
 * - Sortable columns for easy data navigation
 * - Quick access to edit individual user details
 * - User deletion with confirmation
 * - Error handling for API operations
 * - Loading states for asynchronous operations
 * - Responsive design for various screen sizes
 * - Full i18n support for all text content
 *
 * The component communicates with UserService to fetch user data and perform deletion operations,
 * and uses the router to navigate to the edit view for specific users.
 *
 * @component AdminUserManagementView
 * @requires vue
 * @requires vue-router
 * @requires vue-i18n
 * @requires @/services/UserService
 */
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { fetchAdminUsers, deleteAdminUser } from '@/services/UserService';

const { t } = useI18n();

/**
 * Represents a user entity as received from the backend.
 *
 * @interface BackendUser
 * @property {number|string} id - Unique identifier for the user
 * @property {string} email - User's email address
 * @property {string} displayName - User's display name
 * @property {'USER'|'ADMIN'} role - User's role in the system
 * @property {string|null} firstName - User's first name, if provided
 * @property {string|null} lastName - User's last name, if provided
 * @property {string|null} phone - User's phone number, if provided
 * @property {string} createdAt - ISO timestamp of when the user was created
 * @property {string} updatedAt - ISO timestamp of when the user was last updated
 */
interface BackendUser {
  id: number | string; email: string; displayName: string; role: 'USER' | 'ADMIN';
  firstName: string | null; lastName: string | null; phone: string | null;
  createdAt: string; updatedAt: string;
}

/**
 * Represents a paginated response from Spring Boot backend.
 *
 * @interface SpringPage
 * @template T - The type of items contained in the page
 * @property {T[]} content - Array of items in the current page
 * @property {number} totalPages - Total number of pages available
 * @property {number} totalElements - Total number of items across all pages
 * @property {number} size - Maximum number of items per page
 * @property {number} number - Current page number (zero-based)
 * @property {boolean} first - Whether this is the first page
 * @property {boolean} last - Whether this is the last page
 * @property {boolean} empty - Whether this page contains no items
 */
interface SpringPage<T> {
  content: T[]; totalPages: number; totalElements: number; size: number;
  number: number; first: boolean; last: boolean; empty: boolean;
}

type PaginatedUserResponse = SpringPage<BackendUser>;

const router = useRouter();
const users = ref<PaginatedUserResponse | null>(null);
const isLoading = ref<boolean>(true);
const error = ref<string | null>(null);
const currentPage = ref<number>(0);
const itemsPerPage = ref<number>(10);

const totalPages = computed(() => users.value?.totalPages ?? 0);
const canGoPrevious = computed(() => currentPage.value > 0);
const canGoNext = computed(() => currentPage.value < totalPages.value - 1);

/**
 * Fetches paginated user data from the backend API.
 * Updates the local state with the retrieved user data and pagination information.
 * Sets error state if the API request fails.
 *
 * @async
 * @function fetchUsers
 * @param {number} page - The page number to fetch (zero-based)
 * @returns {Promise<void>}
 */
async function fetchUsers(page: number = 0) {
  isLoading.value = true;
  error.value = null;
  try {
    const response = await fetchAdminUsers(page, itemsPerPage.value);
    users.value = response;
    currentPage.value = response.number;
  } catch (err) {
    error.value = err instanceof Error ? err.message : t('ERROR_UNKNOWN');
    users.value = null;
  } finally {
    isLoading.value = false;
  }
}

/**
 * Navigates to a specific page of the user list.
 * Only navigates if the requested page number is valid.
 *
 * @function goToPage
 * @param {number} pageNumber - The page number to navigate to (zero-based)
 * @returns {void}
 */
function goToPage(pageNumber: number) {
  if (pageNumber >= 0 && pageNumber < totalPages.value) {
    fetchUsers(pageNumber);
  }
}

/**
 * Navigates to the user edit view for a specific user.
 * Uses vue-router to push to the admin-user-edit route.
 *
 * @function handleEditUser
 * @param {number|string} userId - ID of the user to edit
 * @returns {void}
 */
function handleEditUser(userId: number | string) {
  router.push({ name: 'admin-user-edit', params: { id: userId.toString() } });
}

/**
 * Displays a confirmation dialog for user deletion.
 * If confirmed, sends delete request to the backend API.
 * Refreshes the user list after successful deletion.
 * Displays error message if the API request fails.
 *
 * @async
 * @function confirmDeleteUser
 * @param {number|string} userId - ID of the user to delete
 * @param {string} displayName - Display name of the user (for confirmation message)
 * @returns {Promise<void>}
 */
async function confirmDeleteUser(userId: number | string, displayName: string) {
  const confirmDelete = confirm(
    t('ADMIN_USER_DELETE_CONFIRM', { name: displayName, id: userId })
  );

  if (confirmDelete) {
    try {
      await deleteAdminUser(userId);
      alert(t('ADMIN_USER_DELETE_SUCCESS', { name: displayName }));
      fetchUsers(currentPage.value);
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : t('ERROR_UNKNOWN');
      alert(t('ADMIN_USER_DELETE_FAILURE', { error: errorMsg }));
    }
  }
}

onMounted(() => {
  fetchUsers(currentPage.value);
});

</script>

<style scoped>
.admin-user-management { padding: 2rem; max-width: 1000px; margin: 0 auto; }

h2 { text-align: center; margin-bottom: 1.5rem; color: var(--vt-c-text-dark-1); }

.loading-indicator, .no-users-message { text-align: center; padding: 2rem; color: var(--vt-c-text-light-1); font-size: 1.1em; }

.error-message {
  text-align: center;
  padding: 2rem;
  font-size: 1.1em;
  color: var(--vt-c-red-dark);
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  border-radius: 5px;
}

.error-message button {
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  cursor: pointer;
  background-color: var(--vt-c-teal-soft);
  color: var(--vt-c-white);
  border: none;
  border-radius: 4px;
}

.error-message button:hover {
  background-color: var(--vt-c-teal-dark);
}

.user-table { width: 100%; border-collapse: collapse; margin-top: 1rem; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); }
.user-table th, .user-table td { border: 1px solid var(--color-border); padding: 10px 12px; text-align: left; }
.user-table thead { background-color: var(--color-background-soft); }
.user-table th { font-weight: 600; color: var(--vt-c-text-dark-2); }
.user-table tbody tr:nth-child(even) { background-color: var(--color-background-mute); }
.user-table tbody tr:hover { background-color: var(--vt-c-white-mute); }
.user-table td:last-child { text-align: center; white-space: nowrap; }

.pagination-controls { margin-top: 1.5rem; text-align: center; }

.pagination-controls button {
  padding: 8px 16px;
  margin: 0 10px;
  cursor: pointer;
  border: 1px solid var(--color-border);
  background-color: var(--color-background);
  color: var(--vt-c-teal-soft);
  border-radius: 4px;
}

.pagination-controls button:hover:not(:disabled) {
  background-color: var(--vt-c-teal-soft);
  color: var(--vt-c-white);
}

.pagination-controls button:disabled {
  cursor: not-allowed;
  opacity: 0.6;
  background-color: var(--color-background-mute);
}

.pagination-controls span {
  margin: 0 10px;
  font-weight: 500;
  color: var(--vt-c-text-dark-2);
}
</style>
