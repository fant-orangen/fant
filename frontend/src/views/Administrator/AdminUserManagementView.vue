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
            <button @click="handleEditUser(user.id)" class="btn-edit">
              {{ t('EDIT') }}
            </button>
            <button @click="confirmDeleteUser(user.id, user.displayName)" class="btn-delete">
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
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { fetchAdminUsers, deleteAdminUser } from '@/services/UserService';

const { t } = useI18n();

interface BackendUser {
  id: number | string; email: string; displayName: string; role: 'USER' | 'ADMIN';
  firstName: string | null; lastName: string | null; phone: string | null;
  createdAt: string; updatedAt: string;
}
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

async function fetchUsers(page: number = 0) {
  isLoading.value = true;
  error.value = null;
  console.log(`Workspaceing users for page: ${page}`);
  try {
    const response = await fetchAdminUsers(page, itemsPerPage.value);
    users.value = response;
    currentPage.value = response.number;
    console.log('Users fetched:', response);
  } catch (err) {
    console.error("Failed to fetch users:", err);
    error.value = err instanceof Error ? err.message : t('ERROR_UNKNOWN');
    users.value = null;
  } finally {
    isLoading.value = false;
  }
}

function goToPage(pageNumber: number) {
  if (pageNumber >= 0 && pageNumber < totalPages.value) {
    fetchUsers(pageNumber);
  }
}

function handleEditUser(userId: number | string) {
  console.log(`Edit user clicked: ${userId}`);
  router.push({ name: 'admin-user-edit', params: { id: userId.toString() } });
}

async function confirmDeleteUser(userId: number | string, displayName: string) {
  console.log(`Attempting to delete user: ${userId} (${displayName})`);
  const confirmDelete = confirm(
    t('ADMIN_USER_DELETE_CONFIRM', { name: displayName, id: userId })
  );

  if (confirmDelete) {
    try {
      await deleteAdminUser(userId);
      alert(t('ADMIN_USER_DELETE_SUCCESS', { name: displayName }));
      fetchUsers(currentPage.value);
    } catch (err) {
      console.error(`Failed to delete user ${userId}:`, err);
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
/* Styles remain the same */
.admin-user-management { padding: 2rem; max-width: 1000px; margin: 0 auto; }
h2 { text-align: center; margin-bottom: 1.5rem; color: #333; }
.loading-indicator, .error-message, .no-users-message { text-align: center; padding: 2rem; color: #666; font-size: 1.1em; }
.error-message { color: #dc3545; background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 5px; }
.error-message button { margin-top: 1rem; padding: 0.5rem 1rem; cursor: pointer; }
.user-table { width: 100%; border-collapse: collapse; margin-top: 1rem; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); }
.user-table th, .user-table td { border: 1px solid #ddd; padding: 10px 12px; text-align: left; }
.user-table thead { background-color: #f8f9fa; }
.user-table th { font-weight: 600; color: #495057; }
.user-table tbody tr:nth-child(even) { background-color: #f2f2f2; }
.user-table tbody tr:hover { background-color: #e9ecef; }
.user-table td:last-child { text-align: center; white-space: nowrap; }
.btn-edit, .btn-delete { padding: 6px 12px; border: none; border-radius: 4px; cursor: pointer; font-size: 0.9em; margin: 0 4px; transition: background-color 0.2s ease; }
.btn-edit { background-color: #007bff; color: white; }
.btn-edit:hover { background-color: #0056b3; }
.btn-delete { background-color: #dc3545; color: white; }
.btn-delete:hover { background-color: #c82333; }
.pagination-controls { margin-top: 1.5rem; text-align: center; }
.pagination-controls button { padding: 8px 16px; margin: 0 10px; cursor: pointer; border: 1px solid #ddd; background-color: #fff; border-radius: 4px; }
.pagination-controls button:disabled { cursor: not-allowed; opacity: 0.6; background-color: #e9ecef; }
.pagination-controls span { margin: 0 10px; font-weight: 500; color: #495057; }
</style>
