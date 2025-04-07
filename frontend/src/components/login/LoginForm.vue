<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/UserStore'
import { isAxiosError } from 'axios'
import TextInput from '@/components/input/TextInput.vue'

const username = ref('')
const password = ref('')
const error = ref('')
const isLoading = ref(false)

const userStore = useUserStore()
const router = useRouter()

/**
 * Handles the login process.
 * It calls the verifyLogin action from the UserStore with the username and password.
 * On successful login, the user is redirected to the homepage.
 * Errors are caught and displayed to the user.
 */
async function login() {
  error.value = ''
  isLoading.value = true

  try {
    await userStore.verifyLogin(username.value, password.value)
    router.push('/')
  } catch (err: unknown) {
    if (isAxiosError(err)) {
      error.value = err.response?.data?.message ?? 'Login Failed'
    } else {
      error.value = 'Unknown error during login'
    }
  } finally {
    isLoading.value = false
  }
}

function navigateToRegister() {
  router.push('/register')
}
</script>

<template>
  <div class="form-container">
    <h1>{{ $t('LOGIN') }}</h1>
    <form class="form" @submit.prevent="login">
      <!-- Use TextInput component for username -->
      <TextInput
        id="username"
        v-model="username"
        :label="$t('USERNAME')"
        :placeholder="$t('USERNAME')"
      />

      <TextInput
        id="password"
        v-model="password"
        type="password"
        :label="$t('PASSWORD')"
        :placeholder="$t('PASSWORD')"
      />
      <button type="submit" :disabled="isLoading">{{ $t('LOGIN') }}</button>
      <button type="button" @click="navigateToRegister">{{ $t('REGISTRATION_BUTTON') }}</button>
    </form>
    <p v-if="error" class="error">{{ error }}</p>
    <router-link to="/register">{{ $t('NEW_USER_QUESTION') }}</router-link>
  </div>
</template>

