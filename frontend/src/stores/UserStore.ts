import { defineStore } from 'pinia';
import { fetchToken } from '@/services/api/authService';
import { register } from "@/services/api/userService";
import { computed, ref } from "vue";

/**
 *  UserStore manages user authentication state.
 *  Handles login, registration, and logout, and it persists the JWT token and username.
 *
 */
export const useUserStore = defineStore("user", () => {
  // Reactive state to store the JWT token and username.
  const token = ref<string | null>(null);
  const username = ref<string | null>(null);

  /**
   * Updates the store state with token and username if the login response is successful.
   * @param status - HTTP status code from the authentication request.
   * @param tokenStr - JWT token string retrieved from the backend.
   * @param user - Username for the logged-in user.
   * @throws {Error} If the login status is not 200.
   */
  function login(status: number, tokenStr: string, user: string) {
    if (status === 200) {
      token.value = tokenStr;
      username.value = user;
    } else {
      throw new Error("Login Info Error");
    }
  }

  /**
   * Verifies login credentials by calling the backend and updating the store state.
   * Expects the HTTP status code to be 200 and the token to be returned in response.data.
   *
   * @param user - The username.
   * @param password - The password.
   * @throws {Error} If login is unsuccessful.
   */
  async function verifyLogin(user: string, password: string) {
    const response = await fetchToken({ username: user, password: password });
    // Check if HTTP status code is 200.
    if (response.status !== 200){
      throw new Error("Login Info Error");
    }
    // Extract the JWT token from the JSON body
    const tokenStr = response.data as string;
    login(response.status, tokenStr, user);
  }

  /**
   * Register a new user and automatically log them in.
   *
   * @param userData
   */
  async function registerUser(userData: {
    username: string;
    password: string;
    email: string;
    firstName: string;
    lastName: string;
    birthDate: string;
  }) {
    await register(userData);
    await verifyLogin(userData.username, userData.password);
  }

  /**
   * Logs out the current user by clearing token and username.
   */
  function logout() {
    token.value = null;
    username.value = null;
  }

  // Computed getters for accessing the state.
  const loggedIn = computed(() => token.value !== null);
  const getUsername = computed(() => username.value);
  const getToken = computed(() => token.value);

  return {
    token,
    username,
    login,
    verifyLogin,
    registerUser,
    logout,
    loggedIn,
    getUsername,
    getToken
  };
});

