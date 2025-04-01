<script setup lang="ts">
import { ref, watch } from 'vue';
import { useUserStore } from '@/stores/UserStore';
import TextInput from '@/components/input/TextInput.vue'; // Check path
import { useI18n } from 'vue-i18n';

/**
 * Define the shape of the profile data the form handles.
 * Should align with the structure in UserStore.
 */
interface Profile {
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
}

// Accept the profile object as a prop.
const props = defineProps<{ profile: Profile }>();

const userStore = useUserStore();
const { t } = useI18n();

// Create local reactive copies for editing.
const email = ref(props.profile.email);
const firstName = ref(props.profile.firstName);
const lastName = ref(props.profile.lastName);
const phoneNumber = ref(props.profile.phoneNumber);

// State for update process
const isUpdating = ref(false);
const updateError = ref('');
const updateSuccess = ref(false);

// Watch for prop changes to update local refs if profile is re-fetched
watch(() => props.profile, (newProfile) => {
  email.value = newProfile.email;
  firstName.value = newProfile.firstName;
  lastName.value = newProfile.lastName;
  phoneNumber.value = newProfile.phoneNumber;
}, { deep: true });


/**
 * Calls the store action to update the profile on the backend.
 */
async function handleProfileUpdate() {
  isUpdating.value = true;
  updateError.value = '';
  updateSuccess.value = false;

  const updatedProfile: Profile = {
    email: email.value,
    firstName: firstName.value,
    lastName: lastName.value,
    phoneNumber: phoneNumber.value,
  };

  try {
    // Call the store action
    await userStore.updateProfile(updatedProfile);
    updateSuccess.value = true;
    // Optionally hide success message after a delay
    setTimeout(() => updateSuccess.value = false, 3000);
  } catch (err) {
    console.error('Failed to update profile:', err);
    // Provide a user-friendly error message
    updateError.value = t('PROFILE_UPDATE_ERROR'); // Ensure key exists
  } finally {
    isUpdating.value = false;
  }
}
</script>

<template>
  <form class="profile-form" @submit.prevent="handleProfileUpdate">
    <TextInput
      id="email"
      v-model="email"
      :label="$t('PROFILE_EMAIL')"
      :placeholder="$t('PROFILE_EMAIL_PLACEHOLDER')"
      type="email"
      required
      :disabled="isUpdating"
    />
    <TextInput
      id="firstName"
      v-model="firstName"
      :label="$t('PROFILE_FIRSTNAME')"
      :placeholder="$t('PROFILE_FIRSTNAME_PLACEHOLDER')"
      required
      :disabled="isUpdating"
    />
    <TextInput
      id="lastName"
      v-model="lastName"
      :label="$t('PROFILE_LASTNAME')"
      :placeholder="$t('PROFILE_LASTNAME_PLACEHOLDER')"
      required
      :disabled="isUpdating"
    />
    <TextInput
      id="phoneNumber"
      v-model="phoneNumber"
      type="tel"
      :label="$t('PROFILE_PHONENUMBER')"
      :placeholder="$t('PROFILE_PHONENUMBER_PLACEHOLDER')"
      :disabled="isUpdating"
    />

    <p v-if="updateSuccess" class="success-message">{{ $t('PROFILE_UPDATE_SUCCESS') }}</p>
    <p v-if="updateError" class="error-message">{{ updateError }}</p>

    <button type="submit" :disabled="isUpdating">
      {{ isUpdating ? $t('SAVING') : $t('PROFILE_UPDATE_BUTTON') }}
    </button>
  </form>
</template>

<style scoped>
.profile-form {
  display: flex;
  flex-direction: column;
  gap: 1.2rem; /* Slightly more gap */
}
button {
  padding: 0.8rem 1.5rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  align-self: flex-start; /* Align button to the left */
}
button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
button:hover:not(:disabled) {
  background-color: #0056b3;
}

.success-message {
  color: green;
  font-weight: bold;
}
.error-message {
  color: red;
  font-weight: bold;
}
</style>
