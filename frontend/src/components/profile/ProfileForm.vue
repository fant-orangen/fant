<script setup lang="ts">
import { defineProps, ref } from 'vue';
import TextInput from "@/components/input/TextInput.vue";
import { useUserStore } from "@/stores/UserStore.ts";

/**
 * Define the structure of profile object.
 */
interface Profile {
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
}

// Accept current profile as prop.
const props = defineProps<{ profile: Profile }>();

// Create local reactive copes for editing.
const email = ref(props.profile.email);
const firstName = ref(props.profile.firstName);
const lastName = ref(props.profile.lastName);
const phoneNumber = ref(props.profile.phoneNumber);

const userStore = useUserStore();

/**
 * Calls the updateProfile function in UserStore with updated data
 */
async function updateProfile(){
  const updateProfile: Profile = {
    email: email.value,
    firstName: firstName.value,
    lastName: lastName.value,
    phoneNumber: phoneNumber.value
  };
  try {
    await userStore.updateProfile(updateProfile);
  } catch (err) {
    console.error("Error updating profile: ", err);
  }

}
</script>


<template>
  <div class="profile-form">
    <TextInput
      id="email"
      v-model="email"
      :label="$t('PROFILE_EMAIL')"
      :placeholder="$t('PROFILE_EMAIL')"
    />
    <TextInput
      id="firstName"
      v-model="firstName"
      :label="$t('PROFILE_FIRSTNAME')"
      :placeholder="$t('PROFILE_FIRSTNAME')"
      />
    <TextInput
      id="lastName"
      v-model="lastName"
      :label="$t('PROFILE_LASTNAME')"
      :placeholder="$t('PROFILE_LASTNAME')"
    />
    <TextInput
      id="phoneNumber"
      v-model="phoneNumber"
      type="tel"
      :label="$t('PROFILE_PHONENUMBER')"
      :placeholder="$t('PROFILE_PHONENUMBER')"
    />
    <button @click.prevent="updateProfile">{{ $t("PROFILE_UPDATE_BUTTON") }}</button>

  </div>

</template>

