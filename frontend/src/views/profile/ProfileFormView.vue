<template>
  <div v-if="isLoading" class="loading-message">
    {{ $t('LOADING') }}
  </div>
  <form v-else class="profile-form" @submit.prevent="handleProfileUpdate">
    <TextInput id="email" v-model="email" :label="$t('EMAIL')" :placeholder="$t('EMAIL')" type="email" required :disabled="isUpdating" />
    <PasswordInput id="new_password" v-model="newPassword" :label="$t('NEW_PASSWORD')" :placeholder="$t('NEW_PASSWORD')" required :disabled="isUpdating"/>
    <TextInput id="firstName" v-model="firstName" :label="$t('FIRSTNAME')" :placeholder="$t('FIRSTNAME')" :disabled="isUpdating" />
    <TextInput id="lastName" v-model="lastName" :label="$t('LASTNAME')" :placeholder="$t('LASTNAME')" :disabled="isUpdating" />
    <TextInput id="displayName" v-model="displayName" :label="$t('USERNAME')" :placeholder="$t('USERNAME')" required :disabled="isUpdating" />

    <PhoneNumberInput
        id="phone"
        v-model="phone"
        :label="$t('PHONENUMBER')"
        :placeholder="$t('INTERNATIONAL_FORMAT_PHONENUMBER_REQUIREMENT')"
        :disabled="isUpdating"
        @update:isValid="isValid => isPhoneNumberValid = isValid" />
    <PasswordInput id="password" v-model="password" :label="$t('PASSWORD')" :placeholder="$t('PASSWORD_CURRENT_REQUIRED')" required :disabled="isUpdating"/>
    <small>{{ t('PASSWORD_NEEDED') }}</small>

    <p v-if="updateSuccess" class="success-message">{{ $t('PROFILE_UPDATE_SUCCESS') }}</p>
    <p v-if="updateError" class="error-message">{{ updateError }}</p>

    <button type="submit" :disabled="isUpdating">
      {{ isUpdating ? t('SAVING') : t('PROFILE_UPDATE_BUTTON') }}
    </button>
  </form>
</template>

<script setup lang="ts">
/**
 * Profile Form View component.
 *
 * This component provides a form interface for users to view and update their profile information.
 * It handles fetching current profile data, form validation, and submission of updates.
 *
 * Features:
 * - Display and edit profile information (email, name, display name, phone)
 * - Password change functionality
 * - Real-time validation for phone number input
 * - Current password verification for security
 * - Loading states during API operations
 * - Success and error notifications
 * - Reactive form updates based on store changes
 *
 * @component ProfileFormView
 * @requires vue
 * @requires vue-i18n
 * @requires @/stores/UserStore
 * @requires @/components/input/TextInput.vue
 * @requires @/components/input/PasswordInput.vue
 * @requires @/components/input/PhoneNumberInput.vue
 * @displayName ProfileFormView
 */
import { ref, onMounted, watch } from 'vue';
import { useUserStore } from '@/stores/UserStore.ts';
import TextInput from '@/components/input/TextInput.vue';
import PasswordInput from '@/components/input/PasswordInput.vue';
import PhoneNumberInput from '@/components/input/PhoneNumberInput.vue'; // Import PhoneNumberInput
import { useI18n } from 'vue-i18n';

const userStore = useUserStore();
const { t } = useI18n();

// Local reactive copies
const email = ref('');
const newPassword = ref('')
const firstName = ref('');
const lastName = ref('');
const phone = ref(''); // Initialize potentially from store later
const displayName = ref('');
const password = ref(''); // Current password required for update

// State for update process
const isUpdating = ref(false);
const updateError = ref('');
const updateSuccess = ref(false);
const isLoading = ref(false);
const isPhoneNumberValid = ref(true); // Tracks phone validity

/**
 * Lifecycle hook that runs when the component is mounted.
 * Fetches the user's profile data from the store and populates the form fields.
 * Sets loading states and handles potential errors during data fetching.
 *
 * The function:
 * 1. Sets the loading state to true
 * 2. Attempts to fetch profile data from the user store
 * 3. Updates local reactive references with fetched data
 * 4. Handles errors if the profile fetch fails
 * 5. Resets loading state when complete
 *
 * @returns {Promise<void>}
 */
onMounted(async () => {
  isLoading.value = true;
  try {
    await userStore.fetchProfile();
    // Update local refs after fetching
    email.value = userStore.profile.email;
    firstName.value = userStore.profile.firstName;
    lastName.value = userStore.profile.lastName;
    phone.value = userStore.profile.phone; // Update phone ref
    displayName.value = userStore.profile.displayName;
  } catch (err) {
    updateError.value = t('PROFILE_INFO_UNAVAILABLE');
  } finally {
    isLoading.value = false;
  }
});

// Watch the store's profile for external changes
watch(() => userStore.profile, (newProfile) => {
  email.value = newProfile.email;
  firstName.value = newProfile.firstName;
  lastName.value = newProfile.lastName;
  phone.value = newProfile.phone; // Watch phone
  displayName.value = newProfile.displayName;
}, { deep: true });


/**
 * Handles the profile update form submission.
 * Validates inputs, submits data to API, and manages feedback states.
 *
 * @returns {Promise<void>}
 */
async function handleProfileUpdate() {
  updateError.value = '';
  updateSuccess.value = false;

  if (!password.value) {
    updateError.value = t('PASSWORD_NEEDED');
    return;
  }
  // Don't block submission based on optional phone number validity

  isUpdating.value = true;

  const updatedProfileData = {
    email: email.value,
    password: newPassword.value,
    firstName: firstName.value,
    lastName: lastName.value,
    // Pass phone number regardless of frontend validation state, let backend handle if needed
    phone: phone.value,
    displayName: displayName.value,
    currentPassword: password.value
  };

  try {
    await userStore.updateProfile(updatedProfileData);
    updateSuccess.value = true;
    password.value = '';
    setTimeout(() => updateSuccess.value = false, 3000);
  } catch (err: any) {
    if (err.message?.includes('Password is required')) {
      updateError.value = t('PASSWORD_NEEDED');
    } else {
      updateError.value = err.response?.data?.message || t('PROFILE_UPDATE_ERROR');
    }
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
