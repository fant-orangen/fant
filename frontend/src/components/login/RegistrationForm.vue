<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/UserStore.ts'
import { isAxiosError } from 'axios'

const userStore = useUserStore()
const router = useRouter()

const username = ref('')
const email = ref('')
const password = ref('')
const firstName = ref('')
const lastName = ref('')
const birthDate = ref('')
const error = ref('')
const isLoading = ref(false)

async function registerUser() {
  error.value = ''
  isLoading.value = true

  try {
    await userStore.registerUser({
      username: username.value,
      password: password.value,
      email: email.value,
      firstName: firstName.value,
      lastName: lastName.value,
      birthDate: birthDate.value,
    })
    router.push('/')
  } catch (err: unknown) {
    if (isAxiosError(err)) {
      error.value = err.response?.data?.message ?? 'Registration Failed'
    } else {
      error.value = 'Unknown error under registration'
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="form-container">
    <h1>Registration</h1>
    <form @submit.prevent="registerUser">
      <input v-model="username" placeholder="$t('REGISTRATION_USERNAME')" />
      <input v-model="password" placeholder="$t('REGISTRATION_PASSWORD')" />
      <input v-model="email" placeholder="$t('REGISTRATION_EMAIL')" />
      <input v-model="firstName" placeholder="$t('REGISTRATION_FIRSTNAME')" />
      <input v-model="lastName" placeholder="$t('REGISTRATION_LASTNAME')" />
      <input v-model="birthDate" placeholder="$t('REGISTRATION_BIRTHDATE')" />
      <button type="submit">{{ 'REGISTRATION_BUTTON' }}}</button>
    </form>
    <p v-if="error" class="error">{{ error }}</p>
    <router-link to="/login">{{'REGISTRATION_ACCOUNT_QUESTION'}}</router-link>
  </div>
</template>
