<template>
  <div class="form-container">
    <h1>{{ $t('LOGIN') }}</h1>
    <form class="form" @submit.prevent="login">
      <TextInput
        id="username"
        v-model="username"
        :label="$t('EMAIL')"
        :placeholder="$t('EMAIL')"
        required
      />

      <PasswordInput
        id="password"
        v-model="password"
        :label="$t('PASSWORD')"
        :placeholder="$t('PASSWORD')"
        required
        autocomplete="current-password"
      />

      <button type="submit" :disabled="isLoading || !username || !password">
        {{ $t('LOGIN') }}
      </button>

      <button type="button" @click="navigateToRegister">{{ $t('REGISTRATION_BUTTON') }}</button>

    </form>

    <p v-if="error" class="error">{{ error }}</p>

  </div>
</template>

<script setup lang="ts">
/**
 * @fileoverview LoginForm component for user authentication.
 * <p>This component provides functionality for:</p>
 * <ul>
 *   <li>User login with username and password</li>
 *   <li>Form validation and error handling</li>
 *   <li>Navigation to registration page</li>
 * </ul>
 */

import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/UserStore'
import { isAxiosError } from 'axios'
import TextInput from '@/components/input/TextInput.vue'
import PasswordInput from '@/components/input/PasswordInput.vue';

/**
 * Username input field value
 * @type {Ref<string>}
 */
const username = ref('')

/**
 * Password input field value
 * @type {Ref<string>}
 */
const password = ref('')

/**
 * Error message for login failures
 * @type {Ref<string>}
 */
const error = ref('')

/**
 * Loading state indicator during authentication
 * @type {Ref<boolean>}
 */
const isLoading = ref(false)

/**
 * User store for authentication operations
 * @type {ReturnType<typeof useUserStore>}
 */
const userStore = useUserStore()

/**
 * Router instance for navigation
 * @type {ReturnType<typeof useRouter>}
 */
const router = useRouter()

/**
 * Handles the login process
 * <p>Validates inputs and attempts to authenticate the user</p>
 * @async
 * @returns {Promise<void>}
 */
async function login() {
  error.value = ''
  if (!username.value || !password.value) {
    error.value = 'Username and password are required.'; // Use i18n key if available
    return;
  }
  isLoading.value = true

  try {
    await userStore.verifyLogin(username.value, password.value)
    router.push('/')
  } catch (err: unknown) {
    if (isAxiosError(err)) {
      error.value = err.response?.data?.message ?? 'Login Failed'
    } else {
      console.error('Unknown login error:', err);
      error.value = 'Unknown error during login'
    }
  } finally {
    isLoading.value = false
  }
}

/**
 * Navigates to the registration page
 * @returns {void}
 */
function navigateToRegister() {
  router.push('/register')
}
</script>

<style scoped>
.form-container {
  max-width: 500px;
  margin: 2rem auto;
  padding: 1.5rem;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background-color: var(--vt-c-white-mute);
}

.form {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  width: 100%;
}


.form > * {
  width: 100%;
  max-width: 400px;
}

.error {
  color: var(--vt-c-red-dark);
  margin-top: 1rem;
  text-align: center;
  font-size: 0.9em;
}

button {
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.2s ease;
  margin-top: 1rem;
  width: 100%;
  box-sizing: border-box;
  font-weight: 500;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
}

button[type="submit"] {
  background-color: var(--vt-c-teal-dark);
  color: var(--vt-c-white);
}

button[type="submit"]:hover:not(:disabled) {
  filter: brightness(0.9);
}

button[type="submit"]:disabled {
  background-color: var(--vt-c-text-light-2);
  cursor: not-allowed;
  color: var(--vt-c-white-soft);
}

button[type="button"] {
  background-color: var(--color-background-mute);
  color: var(--vt-c-teal-dark);
  border: 1px solid var(--color-border);
  margin-top: 0.8rem;
}

button[type="button"]:hover:not(:disabled) {
  background-color: var(--color-background);
  border-color: var(--vt-c-teal-light);
}

button[type="button"]:disabled {
  background-color: var(--color-background-mute);
  color: var(--vt-c-text-light-2);
  border-color: var(--color-border);
  cursor: not-allowed;
}

h1 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: var(--color-heading);
}
</style>
