<template>
  <div v-if="isLoading" class="loading-message">
    {{ $t('LOADING') }}
  </div>
  <form v-else class="profile-form" @submit.prevent="handleProfileUpdate">
    <TextInput
      id="email"
      v-model="email"
      :label="$t('EMAIL')"
      :placeholder="$t('EMAIL')"
      type="email"
      required
      :disabled="isUpdating"
    />
    <TextInput
      id="firstName"
      v-model="firstName"
      :label="$t('FIRSTNAME')"
      :placeholder="$t('FIRSTNAME')"
      required
      :disabled="isUpdating"
    />
    <TextInput
      id="lastName"
      v-model="lastName"
      :label="$t('LASTNAME')"
      :placeholder="$t('LASTNAME')"
      required
      :disabled="isUpdating"
    />
    <TextInput
      id="displayName"
      v-model="displayName"
      :label="$t('USERNAME')"
      :placeholder="$t('USERNAME')"
      required
      :disabled="isUpdating"
    />

    <PhoneNumberInput
      id="phone"
      v-model="phone"
      :label="$t('PHONENUMBER')"
      :placeholder="$t('INTERNATIONAL_FORMAT_PHONENUMBER_REQUIREMENT')"
      :disabled="isUpdating"
      @update:isValid="isValid => isPhoneNumberValid = isValid"
    />

    <PasswordInput
      id="password"
      v-model="password"
      :label="$t('PASSWORD')"
      :placeholder="$t('PASSWORD')"
      :disabled="isUpdating"
      autocomplete="new-password"
    />
    <small>{{ t('PASSWORD_OPTIONAL') }}</small>

    <PasswordInput
      id="currentPassword"
      v-model="currentPassword"
      :label="$t('CURRENT_PASSWORD')"
      :placeholder="$t('PASSWORD_CURRENT_REQUIRED')"
      required
      :disabled="isUpdating"
      autocomplete="current-password"
    />
    <small>{{ t('PASSWORD_NEEDED') }}</small>

    <p v-if="updateSuccess" class="success-message">
      {{ $t('PROFILE_UPDATE_SUCCESS') }}
    </p>
    <p v-if="updateError" class="error-message">
      {{ updateError }}
    </p>

    <button type="submit" :disabled="isUpdating">
      {{ isUpdating ? t('SAVING') : t('PROFILE_UPDATE_BUTTON') }}
    </button>
  </form>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useUserStore } from '@/stores/UserStore.ts';
import TextInput from '@/components/input/TextInput.vue';
import PasswordInput from '@/components/input/PasswordInput.vue';
import PhoneNumberInput from '@/components/input/PhoneNumberInput.vue';
import { useI18n } from 'vue-i18n';

const userStore = useUserStore();
const { t } = useI18n();

// Reactive state
const email = ref('');
const firstName = ref('');
const lastName = ref('');
const phone = ref('');
const displayName = ref('');
const password = ref('');
const currentPassword = ref('');

const isUpdating = ref(false);
const updateError = ref('');
const updateSuccess = ref(false);
const isLoading = ref(false);
const isPhoneNumberValid = ref(true);

// Fetch profile data
onMounted(async () => {
  isLoading.value = true;
  try {
    await userStore.fetchProfile();
    email.value = userStore.profile.email;
    firstName.value = userStore.profile.firstName;
    lastName.value = userStore.profile.lastName;
    phone.value = userStore.profile.phone;
    displayName.value = userStore.profile.displayName;
  } catch (err) {
    console.error('Failed to fetch profile:', err);
    updateError.value = t('PROFILE_INFO_UNAVAILABLE');
  } finally {
    isLoading.value = false;
  }
});

// Watch for store profile changes
watch(
  () => userStore.profile,
  (newProfile) => {
    email.value = newProfile.email;
    firstName.value = newProfile.firstName;
    lastName.value = newProfile.lastName;
    phone.value = newProfile.phone;
    displayName.value = newProfile.displayName;
  },
  { deep: true }
);

async function handleProfileUpdate() {
  updateError.value = '';
  updateSuccess.value = false;

  if (!currentPassword.value) {
    updateError.value = t('PASSWORD_NEEDED');
    return;
  }

  isUpdating.value = true;

  const updatedProfileData = {
    email: email.value,
    firstName: firstName.value,
    lastName: lastName.value,
    phone: phone.value,
    displayName: displayName.value,
    password: password.value,
    currentPassword: currentPassword.value
  };

  try {
    await userStore.updateProfile(updatedProfileData);
    updateSuccess.value = true;
    password.value = '';
    currentPassword.value = '';
    setTimeout(() => (updateSuccess.value = false), 3000);
  } catch (err: any) {
    console.error('Failed to update profile:', err);
    updateError.value = err.response?.data?.message || t('PROFILE_UPDATE_ERROR');
  } finally {
    isUpdating.value = false;
  }
}
</script>

<style scoped>
.profile-form {
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
  max-width: 600px;
  margin: 1rem auto;
}

button {
  padding: 0.8rem 1.5rem;
  background-color: var(--vt-c-teal-soft);
  color: var(--vt-c-white);
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  align-self: flex-start;
  transition: background-color 0.2s ease;
}

button:disabled {
  background-color: var(--color-background-mute);
  cursor: not-allowed;
}

button:hover:not(:disabled) {
  background-color: var(--vt-c-teal-dark);
}

.success-message {
  color: var(--vt-c-teal-dark);
  font-weight: bold;
  margin-top: 0.5rem;
  background-color: var(--vt-c-teal-light);
  padding: 0.5rem;
  border-radius: 4px;
  border: 1px solid var(--vt-c-teal-soft);
}

.error-message {
  color: var(--vt-c-red-dark);
  font-weight: bold;
  margin-top: 0.5rem;
  font-size: 0.9em;
  background-color: var(--vt-c-red-light);
  padding: 0.5rem;
  border-radius: 4px;
  border: 1px solid var(--vt-c-red-soft);
}

.loading-message {
  padding: 1rem;
  text-align: center;
  color: var(--vt-c-text-light-1);
}

small {
  font-size: 0.8em;
  color: var(--vt-c-text-light-2);
  margin-top: -0.8rem;
  display: block;
}
</style>
