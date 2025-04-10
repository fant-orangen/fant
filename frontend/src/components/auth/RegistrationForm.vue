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

.button-secondary {
  background-color: var(--color-background-mute);
  color: var(--vt-c-teal-dark);
  border: 1px solid var(--color-border);
  margin-top: 0.8rem;
}

.button-secondary:hover:not(:disabled) {
  background-color: var(--color-background);
  border-color: var(--vt-c-teal-light);
}

.button-secondary:disabled {
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
