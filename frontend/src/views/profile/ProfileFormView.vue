<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useUserStore } from '@/stores/UserStore.ts';
import TextInput from '@/components/input/TextInput.vue';
import { useI18n } from 'vue-i18n';

const userStore = useUserStore();
const { t } = useI18n();

// Create local reactive copies, including displayName and password
const email = ref(userStore.profile.email);
const firstName = ref(userStore.profile.firstName);
const lastName = ref(userStore.profile.lastName);
const phone = ref(userStore.profile.phone);
const displayName = ref(userStore.profile.displayName);
const password = ref('');

// State for update process
const isUpdating = ref(false);
const updateError = ref('');
const updateSuccess = ref(false);
const isLoading = ref(false);

// Fetch profile data when the component is mounted
onMounted(async () => {
  isLoading.value = true;
  try {
    await userStore.fetchProfile(); // Fetches profile including displayName
    // Update local refs after fetching
    email.value = userStore.profile.email;
    firstName.value = userStore.profile.firstName;
    lastName.value = userStore.profile.lastName;
    phone.value = userStore.profile.phone;
    displayName.value = userStore.profile.displayName; // <-- Update displayName ref
  } catch (err) {
    console.error('Failed to fetch profile on mount:', err);
    updateError.value = t('PROFILE_INFO_UNAVAILABLE');
  } finally {
    isLoading.value = false;
  }
});

// Watch the store's profile for changes
watch(() => userStore.profile, (newProfile) => {
  email.value = newProfile.email;
  firstName.value = newProfile.firstName;
  lastName.value = newProfile.lastName;
  phone.value = newProfile.phone;
  displayName.value = newProfile.displayName; // <-- Watch displayName
}, { deep: true });


/**
 * Calls the store action to update the profile on the backend.
 */
async function handleProfileUpdate() {
  // Simple frontend validation for password presence
  if (!password.value) {
    updateError.value = 'Password is required to confirm changes.'; // Or use i18n key
    return; // Stop submission if password is empty
  }

  isUpdating.value = true;
  updateError.value = '';
  updateSuccess.value = false;

  // Include displayName and password in the object sent to the store action
  const updatedProfileData = { // Use a different name to avoid confusion with ProfileFormData interface if needed
    email: email.value,
    firstName: firstName.value,
    lastName: lastName.value,
    phone: phone.value,
    displayName: displayName.value,
    password: password.value
  };

  try {
    await userStore.updateProfile(updatedProfileData); // Pass the complete payload
    updateSuccess.value = true;
    password.value = ''; // Clear password field after successful update
    setTimeout(() => updateSuccess.value = false, 3000);
  } catch (err: any) {
    console.error('Failed to update profile:', err);
    // Display specific error if it's the known password requirement issue
    if (err.message?.includes('Password is required')) {
      updateError.value = 'Password is required to confirm changes.';
    } else {
      updateError.value = t('PROFILE_UPDATE_ERROR');
    }
  } finally {
    isUpdating.value = false;
  }
}
</script>

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
      :label="$t('USERNAME')" :placeholder="$t('USERNAME')"
      required
      :disabled="isUpdating"
    />
    <TextInput
      id="phone"
      v-model="phone"
      type="tel"
      :label="$t('PHONENUMBER')"
      :placeholder="'+47 123 45 678'" :disabled="isUpdating"
    />
    <small>{{ $t('INTERNATIONAL_FORMAT_PHONENUMBER_REQUIREMENT') }}</small>
    <TextInput
      id="password"
      v-model="password"
      :label="$t('PASSWORD')"
      :placeholder="$t('PASSWORD_CURRENT_REQUIRED')" type="password"
      required :disabled="isUpdating"
      autocomplete="current-password" />
    <small>{{ t('PASSWORD_NEEDED') }};</small> <p v-if="updateSuccess" class="success-message">{{ $t('PROFILE_UPDATE_SUCCESS') }}</p>
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
  gap: 1.2rem;
}
button {
  padding: 0.8rem 1.5rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  align-self: flex-start;
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
.loading-message {
  padding: 1rem;
  text-align: center;
  color: #555;
}
small {
  font-size: 0.8em;
  color: #666;
  margin-top: -0.8rem; /* Adjust spacing */
}
</style>
