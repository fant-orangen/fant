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
  border: 1px solid #eee;
  border-radius: 8px;
  background-color: #fff;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.error {
  color: red;
  margin-top: 1rem;
  text-align: center;
  font-size: 0.9em;
}

button {
  padding: 0.8rem 1.5rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.2s ease;
  margin-top: 0.5rem;
}

/* Style for the registration button (type="button") */
button[type="button"] {
  background-color: #6c757d; /* Example: grey color */
}
button[type="button"]:hover:not(:disabled){
  background-color: #5a6268; /* Darker grey on hover */
}


button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

button:hover:not(:disabled) {
  background-color: #0056b3;
}

h1 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #333;
}
</style>
