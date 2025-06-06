<template>
  <div class="admin-user-edit">
    <h2>{{ t('ADMIN_EDIT_USER_TITLE', { id: props.id }) }}</h2>

    <div v-if="isLoading" class="loading-indicator">{{ t('LOADING_USER_DATA') }}</div>
    <div v-else-if="error" class="error-message">
      <h3>{{ t('ERROR_LOADING_USER_TITLE') }}</h3>
      <p>{{ error }}</p>
      <button @click="fetchUserData">{{ t('RETRY') }}</button>
    </div>

    <form v-else-if="userData" @submit.prevent="handleSubmit" class="edit-form">
      <div class="form-group">
        <label for="email">{{ t('EMAIL') }}:</label>
        <input id="email" type="email" v-model="formData.email" required />
      </div>
      <div class="form-group">
        <label for="displayName">{{ t('ADMIN_TABLE_DISPLAY_NAME') }}:</label>
        <input id="displayName" type="text" v-model="formData.displayName" required />
      </div>
      <div class="form-group">
        <label for="firstName">{{ t('FIRSTNAME') }}:</label>
        <input id="firstName" type="text" v-model="formData.firstName" />
      </div>
      <div class="form-group">
        <label for="lastName">{{ t('LASTNAME') }}:</label>
        <input id="lastName" type="text" v-model="formData.lastName" />
      </div>
      <div class="form-group">
        <label for="phone">{{ t('PHONENUMBER') }}:</label>
        <input id="phone" type="tel" v-model="formData.phone" :placeholder="t('ADMIN_PHONE_PLACEHOLDER')" />
      </div>

      <div class="form-group">
        <label for="role">{{ t('ADMIN_TABLE_ROLE') }}:</label>
        <select id="role" v-model="formData.role" required>
          <option value="USER">USER</option>
          <option value="ADMIN">ADMIN</option>
        </select>
      </div>

      <div class="form-group">
        <label for="password">{{ t('ADMIN_NEW_PASSWORD_LABEL') }}:</label>
        <input id="password" type="password" v-model="formData.password" :placeholder="t('ADMIN_PASSWORD_PLACEHOLDER')" />
        <p class="info">{{ t('ADMIN_PASSWORD_INFO') }}</p>
      </div>

      <div class="form-actions">
        <button type="submit" :disabled="isSubmitting" class="btn-submit">
          {{ isSubmitting ? t('UPDATING') : t('UPDATE_USER_BUTTON') }}
        </button>
        <button type="button" @click="cancelEdit" class="btn-cancel">{{ t('CANCEL') }}</button>
      </div>

      <p v-if="submitError" class="error-message submit-error">{{ submitError }}</p>

    </form>

    <div v-else>{{ t('ADMIN_USER_DATA_UNAVAILABLE') }}</div>
  </div>
</template>

<script setup lang="ts">
/**
 * Admin User Edit View component.
 *
 * Provides an interface for administrators to edit existing user accounts in the system.
 * This component handles fetching user data, displaying a form with the current values,
 * validating input, and submitting changes to the backend.
 *
 * Features:
 * - Asynchronous loading of user data by ID
 * - Form for editing user profile information:
 *   - Email address
 *   - Display name
 *   - Personal information (first/last name)
 *   - Phone number with international format validation
 *   - User role (USER/ADMIN) selection
 *   - Optional password reset functionality
 * - Validation for all input fields
 * - Error handling for API operations
 * - Responsive design
 * - Cancel option to return to user list
 * - Full i18n support for all text content
 *
 * The component communicates with the UserService to fetch and update user data,
 * and uses the router to navigate back to the user list on completion or cancellation.
 *
 * @component AdminUserEditView
 * @requires vue
 * @requires vue-router
 * @requires vue-i18n
 * @requires @/services/UserService
 */
import { ref, onMounted, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { updateAdminUser, fetchAdminUserById } from '@/services/UserService';

const { t } = useI18n();

/**
 * Represents a user entity as received from the backend.
 *
 * @interface BackendUser
 */
interface BackendUser {
  id: number | string; email: string; displayName: string; role: 'USER' | 'ADMIN';
  firstName: string | null; lastName: string | null; phone: string | null;
  createdAt?: string; updatedAt?: string;
}

/**
 * Represents the data payload sent to the backend when updating a user.
 *
 * @interface AdminUserUpdatePayload
 */
interface AdminUserUpdatePayload {
  email: string; password?: string; displayName: string;
  firstName: string | null; lastName: string | null; phone: string | null;
  role?: 'USER' | 'ADMIN';
}

const props = defineProps<{ id: string; }>();
const router = useRouter();

const userData = ref<BackendUser | null>(null);
const isLoading = ref<boolean>(true);
const error = ref<string | null>(null);
const isSubmitting = ref<boolean>(false);
const submitError = ref<string | null>(null);
const formData = reactive<AdminUserUpdatePayload>({
  email: '', displayName: '', firstName: '', lastName: '', phone: '', role: 'USER', password: ''
});

/**
 * Fetches user data from the backend API based on the provided ID.
 * Populates the form fields with the retrieved user data.
 * Sets error state if the API request fails.
 *
 * @async
 * @function fetchUserData
 * @returns {Promise<void>}
 */
async function fetchUserData() {
  isLoading.value = true;
  error.value = null;
  userData.value = null;
  try {
    const user = await fetchAdminUserById(props.id);
    userData.value = user;
    formData.email = user.email || '';
    formData.displayName = user.displayName || '';
    formData.firstName = user.firstName || '';
    formData.lastName = user.lastName || '';
    formData.phone = user.phone || '';
    formData.role = user.role || 'USER';
    formData.password = '';
  } catch (err) {
    error.value = err instanceof Error ? err.message : t('ERROR_FETCH_USER_DETAILS');
  } finally {
    isLoading.value = false;
  }
}

/**
 * Handles the form submission event.
 * Validates the input data and submits it to the backend API.
 * Redirects to the user list view on successful update.
 * Sets error state if the API request fails.
 *
 * @async
 * @function handleSubmit
 * @returns {Promise<void>}
 */
async function handleSubmit() {
  isSubmitting.value = true;
  submitError.value = null;
  console.log('Submitting update for user ID:', props.id);
  const payload: AdminUserUpdatePayload = {
    email: formData.email,
    displayName: formData.displayName,
    firstName: formData.firstName || null,
    lastName: formData.lastName || null,
    phone: formData.phone || null,
    role: formData.role, // <-- Add this line
  };
  if (!payload.password || payload.password.trim() === '') {
    delete payload.password;
    console.log("Password field is empty, sending update without it.");
  }
  console.log('Submitting payload:', JSON.stringify(payload, null, 2));

  try {
    await updateAdminUser(props.id, payload);
    alert(t('ADMIN_USER_UPDATE_SUCCESS'));
    router.push({ name: 'admin-users' });
  } catch (err) {
    console.error("Failed to update user:", err);
    const errorMsg = err instanceof Error ? err.message : t('ERROR_UNKNOWN');
    submitError.value = t('ADMIN_USER_UPDATE_FAILURE', { error: errorMsg });
  } finally {
    isSubmitting.value = false;
  }
}

/**
 * Cancels the edit operation and redirects back to the admin users list.
 * No data is submitted when this function is called.
 *
 * @function cancelEdit
 * @returns {void}
 */
function cancelEdit() {
  router.push({ name: 'admin-users' });
}

onMounted(fetchUserData);

</script>

<style scoped>
/* Styles remain the same */
.admin-user-edit { max-width: 700px; margin: 2rem auto; padding: 2.5rem; border: 1px solid #dee2e6; border-radius: 8px; background-color: #ffffff; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05); }
h2 { text-align: center; margin-bottom: 2rem; color: #343a40; font-weight: 600; }
.edit-form { display: flex; flex-direction: column; gap: 1rem; }
.form-group { margin-bottom: 0.5rem; }
.form-group label { display: block; margin-bottom: 0.5rem; font-weight: 500; color: #495057; }
.form-group input, .form-group select { width: 100%; padding: 12px 15px; border: 1px solid #ced4da; border-radius: 4px; font-size: 1rem; line-height: 1.5; transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out; }
.form-group input:focus, .form-group select:focus { border-color: #80bdff; outline: 0; box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25); }
.form-group .info { font-size: 0.85em; color: #6c757d; margin-top: 6px; }
.form-actions { margin-top: 1.5rem; display: flex; justify-content: flex-end; gap: 0.8rem; }
.form-actions button { padding: 10px 25px; border: none; border-radius: 4px; cursor: pointer; font-size: 1rem; font-weight: 500; transition: background-color 0.2s ease, opacity 0.2s ease; }
.btn-submit { background-color: #28a745; color: white; }
.btn-submit:hover:not(:disabled) { background-color: #218838; }
.btn-submit:disabled { background-color: #a3d9b1; cursor: not-allowed; opacity: 0.7; }
.btn-cancel { background-color: #6c757d; color: white; }
.btn-cancel:hover { background-color: #5a6268; }
.loading-indicator, .error-message { text-align: center; padding: 3rem 1rem; margin-top: 1rem; font-size: 1.1em; border-radius: 5px; }
.loading-indicator p { color: #6c757d; }
.error-message { color: #721c24; background-color: #f8d7da; border: 1px solid #f5c6cb; }
.error-message button { margin-top: 1rem; padding: 0.5rem 1rem; cursor: pointer; background-color: #dc3545; color: white; border: none; border-radius: 4px; }
.error-message button:hover { background-color: #c82333; }
.submit-error { margin-top: 1.5rem; padding: 0.8rem; font-size: 0.95em; text-align: center; }
</style>
