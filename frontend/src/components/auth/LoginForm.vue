<template>
  <div class="form-container">
    <h1>{{ $t('LOGIN') }}</h1>
    <form class="form" @submit.prevent="login">
      <TextInput
        id="username"
        v-model="username"
        :label="$t('USERNAME')"
        :placeholder="$t('USERNAME')"
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/UserStore'
import { isAxiosError } from 'axios'
import TextInput from '@/components/input/TextInput.vue'
import PasswordInput from '@/components/input/PasswordInput.vue';

const username = ref('')
const password = ref('')
const error = ref('') // For login API errors
const isLoading = ref(false)

const userStore = useUserStore()
const router = useRouter()

/**
 * Handles the login process.
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

// Kept the navigateToRegister function for the button
function navigateToRegister() {
  router.push('/register')
}
</script>

<style scoped>
/* Styles remain the same as the previous version */
.form-container {
  max-width: 500px;
  margin: 2rem auto;
  padding: 1.5rem;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background-color: var(--color-background-soft);
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
