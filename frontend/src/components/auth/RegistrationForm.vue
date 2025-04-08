<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/UserStore.ts'
import { isAxiosError } from 'axios'
import TextInput from '@/components/input/TextInput.vue'

const username = ref('')
const password = ref('')
const email = ref('')
const firstName = ref('')
const lastName = ref('')
const phoneNumber = ref('')

const error = ref('')
const isLoading = ref(false)

const userStore = useUserStore()
const router = useRouter()

/**
 * Handles the registration process by sending user data to the backend.
 * On success, the user is automatically logged in and redirected to the homepage.
 */
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
      birthDate: phoneNumber.value
    })
    router.push('/')
  } catch (err: unknown) {
    if (isAxiosError(err)) {
      error.value = err.response?.data?.message ?? 'Registration Failed'
    } else {
      error.value = 'Unknown error during registration'
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="form-container">
    <h1>{{ $t('REGISTRATION_HEADER') }}</h1>
    <form class="form" @submit.prevent="registerUser">
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
      <TextInput
        id="email"
        v-model="email"
        type="email"
        :label="$t('EMAIL')"
        :placeholder="$t('EMAIL')"
      />
      <TextInput
        id="firstName"
        v-model="firstName"
        :label="$t('FIRSTNAME')"
        :placeholder="$t('FIRSTNAME')"
      />
      <TextInput
        id="lastName"
        v-model="lastName"
        :label="$t('LASTNAME')"
        :placeholder="$t('LASTNAME')"
      />
      <TextInput
        id="phoneNumber"
        v-model="phoneNumber"
        type="tel"
        :label="$t('PHONENUMBER')"
        :placeholder="$t('PHONENUMBER')"
      />
      <button type="submit" :disabled="isLoading">{{ $t('REGISTRATION_BUTTON') }}</button>
    </form>
    <p v-if="error" class="error">{{ error }}</p>
    <router-link to="/login">{{ $t('REGISTRATION_ACCOUNT_QUESTION') }}</router-link>
  </div>
</template>

