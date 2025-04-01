<!-- src/components/profile/ProfileForm.vue -->
<script setup lang="ts">
import { defineProps, ref } from 'vue';
import TextInput from '@/components/input/TextInput.vue';

/**
 * Define the shape of the profile data.
 */
interface Profile {
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
}

// Accept the profile object as a prop.
const props = defineProps<{ profile: Profile }>();

// Create local reactive copies for editing.
const email = ref(props.profile.email);
const firstName = ref(props.profile.firstName);
const lastName = ref(props.profile.lastName);
const phoneNumber = ref(props.profile.phoneNumber);

/**
 * Emits an event to indicate the profile has been updated.
 * (Alternatively, you could directly call a store action here.)
 */
function updateProfile() {
  // For example, you could emit an event with the updated data.
  // This example simply logs the updated profile.
  const updatedProfile: Profile = {
    email: email.value,
    firstName: firstName.value,
    lastName: lastName.value,
    phoneNumber: phoneNumber.value
  };
  console.log('Updated profile:', updatedProfile);
  // In a real app, you would call userStore.updateProfile(updatedProfile)
}
</script>

<template>
  <div class="profile-form">
    <TextInput
      id="email"
      v-model="email"
      :label="$t('PROFILE_EMAIL')"
      :placeholder="$t('PROFILE_EMAIL_PLACEHOLDER')"
    />
    <TextInput
      id="firstName"
      v-model="firstName"
      :label="$t('PROFILE_FIRSTNAME')"
      :placeholder="$t('PROFILE_FIRSTNAME_PLACEHOLDER')"
    />
    <TextInput
      id="lastName"
      v-model="lastName"
      :label="$t('PROFILE_LASTNAME')"
      :placeholder="$t('PROFILE_LASTNAME_PLACEHOLDER')"
    />
    <TextInput
      id="phoneNumber"
      v-model="phoneNumber"
      type="tel"
      :label="$t('PROFILE_PHONENUMBER')"
      :placeholder="$t('PROFILE_PHONENUMBER_PLACEHOLDER')"
    />
    <button @click.prevent="updateProfile">{{ $t('PROFILE_UPDATE_BUTTON') }}</button>
  </div>
</template>

<style scoped>
.profile-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
</style>

