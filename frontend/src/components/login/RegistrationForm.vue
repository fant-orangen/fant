<template>
  <div class="form-container">
    <h1>{{ $t('REGISTRATION_HEADER') }}</h1>
    <form class="form" @submit.prevent="registerUser">
      <TextInput id="displayName" v-model="displayName" :label="$t('USERNAME')" :placeholder="$t('USERNAME')" required />
      <TextInput id="email" v-model="email" type="email" :label="$t('EMAIL')" :placeholder="$t('EMAIL')" required />
      <PasswordInput id="password" v-model="password" :label="$t('PASSWORD')" :placeholder="$t('PASSWORD')" required @update:isValid="isValid => isPasswordComponentValid = isValid" />
      <TextInput id="firstName" v-model="firstName" :label="$t('FIRSTNAME')" :placeholder="$t('FIRSTNAME')" />
      <TextInput id="lastName" v-model="lastName" :label="$t('LASTNAME')" :placeholder="$t('LASTNAME')" />

      <PhoneNumberInput
        id="phoneNumber"
        v-model="phoneNumber"
        :label="$t('PHONENUMBER')"
        :placeholder="$t('INTERNATIONAL_FORMAT_PHONENUMBER_REQUIREMENT')"
        @update:isValid="isValid => isPhoneNumberValid = isValid" />
      <button type="submit" :disabled="isLoading || !isPasswordComponentValid"> {{ $t('REGISTRATION_BUTTON') }}
      </button>
      <p v-if="error" class="error">{{ error }}</p>
      <button type="button" @click="navigateToLogin" class="button-secondary">
        {{ $t('GO_TO_LOGIN_BUTTON') }}
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/UserStore';
import { isAxiosError } from 'axios';
import TextInput from '@/components/input/TextInput.vue';
import PasswordInput from '@/components/input/PasswordInput.vue';
import PhoneNumberInput from '@/components/input/PhoneNumberInput.vue'; // Import PhoneNumberInput

// Form fields refs
const displayName = ref('');
const password = ref('');
const email = ref('');
const firstName = ref('');
const lastName = ref('');
const phoneNumber = ref('');

// State refs
const error = ref('');
const isLoading = ref(false);
const isPasswordComponentValid = ref(false);
const isPhoneNumberValid = ref(true); // Tracks phone validity, defaults true (empty is valid)

const userStore = useUserStore();
const router = useRouter();

async function registerUser() {
  error.value = '';
  // Only block submission based on required fields (password)
  if (!isPasswordComponentValid.value) {
    error.value = 'Please ensure the password meets the requirements.';
    return;
  }

  isLoading.value = true;
  try {
    await userStore.registerUser({
      email: email.value,
      password: password.value,
      displayName: displayName.value,
      firstName: firstName.value,
      lastName: lastName.value,
      phone: phoneNumber.value
    });
    router.push('/');
  } catch (err: unknown) {
    if (isAxiosError(err)) {
      error.value = err.response?.data?.message ?? 'Registration Failed';
    } else {
      console.error('Unknown registration error:', err);
      error.value = 'Unknown error during registration';
    }
  } finally {
    isLoading.value = false;
  }
}

function navigateToLogin() {
  router.push('/login');
}
</script>

<style scoped>
.form-container { max-width: 500px; margin: 2rem auto; padding: 1.5rem; border: 1px solid #eee; border-radius: 8px; background-color: #fff; }
.form { display: flex; flex-direction: column; gap: 1rem; }
.error { color: red; margin-top: 1rem; text-align: center; font-size: 0.9em; }
button { padding: 0.8rem 1.5rem; border: none; border-radius: 6px; cursor: pointer; font-size: 1rem; transition: background-color 0.2s ease, border-color 0.2s ease, color 0.2s ease; margin-top: 0.5rem; width: 100%; box-sizing: border-box; }
.button-primary { background-color: #007bff; color: white; }
.button-primary:hover:not(:disabled) { background-color: #0056b3; }
.button-primary:disabled { background-color: #ccc; cursor: not-allowed; }
.button-secondary { background-color: #f8f9fa; color: #007bff; border: 1px solid #dee2e6; margin-top: 0.8rem; }
.button-secondary:hover:not(:disabled) { background-color: #e2e6ea; border-color: #ced4da; }
.button-secondary:disabled { background-color: #e9ecef; color: #6c757d; border-color: #dee2e6; cursor: not-allowed; }
h1 { text-align: center; margin-bottom: 1.5rem; color: #333; }
</style>
