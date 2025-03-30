<script setup lang="ts">
import { ref } from 'vue';
import { useUserStore } from "@/stores/UserStore.ts";
import { useRouter } from "vue-router";
import { isAxiosError } from 'axios';

const userStore = useUserStore();
const router = useRouter();


const username = ref('');
const password = ref('');
const error = ref('');
const isLoading = ref(false);

async function login() {
  error.value = "";
  isLoading.value = true;

  try {
    await userStore.verifyLogin(username.value, password.value);
    router.push('/');
  } catch (err: unknown){
    if (isAxiosError(err)) {
      error.value = err.response?.data?.message ?? "Login Failed";
    } else {
      error.value = "Unknown Error During Login";
    }
  } finally {
    isLoading.value = false;
  }
}
</script>

<template>
  <div class="form-container">
    <h1>{{ $t('LOGIN_LOGIN') }}</h1>
    <form @sugmit.prevent="login">
      <input v-model="username" :placeholder="$t('REGISTRATION_USERNAME')"/>
      <input v-model="password" type="password" :placeholder="$t('REGISTRATION_PASSWORD')" />
      <button type="submit">{{ $t('LOGIN_LOGIN') }}</button>

    </form>
    <p v-if="error" class="error">{{ error }}</p>
    <router-link to="/register">{{ $t('NEW_USER_QUESTION') }}</router-link>

  </div>
</template>

