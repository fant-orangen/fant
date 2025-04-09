import { defineStore } from 'pinia';
import { fetchToken } from '@/services/api/authService';
import { register } from '@/services/api/userService'; // Correct import [cite: uploaded:project V/frontend/src/services/api/userService.ts]
import { computed, ref } from 'vue';
import api from '@/services/api/axiosInstance';

// Define the type for registration data matching the backend DTO
interface RegistrationData {
  email: string;
  password: string;
  displayName: string;
  firstName: string;
  lastName: string;
  phone: string;
}


/**
 * UserStore manages user authentication and profile state.
 * It handles login, registration, profile fetching/updating, and logout.
 * The store persists the JWT token, username, and user profile.
 */
export const useUserStore = defineStore("user", () => {
  // ... (existing reactive state: token, username, role, userId, profile) ...
  const token = ref<string | null>(localStorage.getItem('token'));
  const username = ref<string | null>(localStorage.getItem('username')); // Keep username for display/login context if needed, separate from displayName
  const role = ref<string | null>(localStorage.getItem('role'));
  const userId = ref<string | null>(localStorage.getItem('userId'));

  if (token.value && (!role.value || !userId.value)) { // Also check userId
    try {
      const tokenParts = token.value.split('.');
      if (tokenParts.length === 3) {
        const payload = JSON.parse(atob(tokenParts[1]));
        role.value = payload.role;
        userId.value = payload.userId?.toString(); // Ensure userId is stored as string
        if (payload.role) localStorage.setItem('role', payload.role);
        if (payload.userId) localStorage.setItem('userId', payload.userId.toString()); // Store as string
      }
    } catch (error) {
      console.error("Error parsing token:", error);
      // Consider logging out if token is invalid
      // logout();
    }
  }

  const profile = ref({
    email: '',
    firstName: '',
    lastName: '',
    // Renamed phoneNumber to phone to align better, though not strictly necessary here
    phone: ''
  });

  /**
   * Updates the store state with token and username (email) if the login response is successful.
   * Also extracts role and userId from the token.
   * @param status - HTTP status code from the authentication request.
   * @param tokenStr - JWT token string retrieved from the backend.
   * @param userEmail - Email used for login (acts as the unique identifier).
   * @throws {Error} If the login status is not 200.
   */
  function login(status: number, tokenStr: string, userEmail: string) { // Changed 'user' to 'userEmail' for clarity
    if (status === 200) {
      token.value = tokenStr;
      username.value = userEmail; // Store email as username identifier

      // Extract role and userId from token and save to localStorage
      const tokenParts = tokenStr.split('.');
      if (tokenParts.length === 3) {
        try {
          const payload = JSON.parse(atob(tokenParts[1]));
          role.value = payload.role;
          userId.value = payload.userId?.toString(); // Ensure userId is stored as string
          if (payload.role) localStorage.setItem('role', payload.role);
          if (payload.userId) localStorage.setItem('userId', payload.userId.toString()); // Store as string
        } catch (error) {
          console.error("Error parsing token during login:", error);
          // Handle error, maybe clear invalid token?
        }
      } else {
        console.error("Invalid token format received during login.");
        // Handle invalid token format
      }

      localStorage.setItem('token', tokenStr);
      localStorage.setItem('username', userEmail); // Store email in localStorage
    } else {
      throw new Error("Login Info Error");
    }
  }

  /**
   * Verifies login credentials by calling the backend and updating the store state.
   * Expects the HTTP status code to be 200 and the token to be returned in response.data.
   *
   * @param userEmail - The user's email.
   * @param password - The password.
   * @throws {Error} If login is unsuccessful.
   */
  async function verifyLogin(userEmail: string, password: string) { // Renamed 'user' to 'userEmail'
    try {
      console.log(`Starting login for user: ${userEmail}`);
      // Use userEmail for the 'username' field expected by fetchToken
      const response = await fetchToken({ username: userEmail, password: password }); // [cite: uploaded:project V/frontend/src/services/api/authService.ts]
      console.log("Login response:", response.data);

      const tokenStr = response.data.token;

      if (response.status !== 200 || !tokenStr) {
        throw new Error("Login Info Error: Invalid status or missing token");
      }

      login(response.status, tokenStr, userEmail); // Pass userEmail here
    } catch (error) {
      console.error("Login error:", error);
      // Clear potentially bad state if login fails
      logout(); // Consider calling logout on failure
      throw error;
    }
  }

  /**
   * Registers a new user and automatically logs them in using their email.
   *
   * @param userData - An object containing registration details (RegistrationData type).
   */
  async function registerUser(userData: RegistrationData) { // Use the defined interface
    try {
      // Call the register service with the correctly structured data
      await register(userData);
      // After successful registration, log in using email and password
      await verifyLogin(userData.email, userData.password);
    } catch (error) {
      console.error("Registration or subsequent login failed:", error);
      throw error; // Re-throw the error to be handled by the component
    }
  }

  /**
   * Fetches the current user's profile from the backend.
   */
  async function fetchProfile() {
    try {
      const response = await api.get('/users/profile'); // Assuming this returns the profile structure
      // Map backend response (which might include passwordHash etc.) to the profile ref structure
      profile.value = {
        email: response.data.email || '',
        firstName: response.data.firstName || '',
        lastName: response.data.lastName || '',
        phone: response.data.phone || '' // Map backend 'phone' to profile 'phone'
      };
    } catch (error) {
      console.error("Failed to fetch profile:", error);
      // Handle error, maybe clear profile or show notification
      profile.value = { email: '', firstName: '', lastName: '', phone: '' }; // Reset profile on error
      throw error;
    }
  }


  /**
   * Updates the user's profile by sending updated data to the backend.
   * Note: This sends the entire profile object. Adjust if backend expects partial updates.
   *
   * @param updatedProfile - An object with the updated profile details.
   * @returns A promise that resolves when the profile has been updated.
   */
  async function updateProfile(updatedProfile: typeof profile.value) {
    try {
      // Send data matching UserCreateDto (email, displayName, firstName, lastName, phone, password)
      // We might need a separate DTO or endpoint for profile updates that doesn't require password
      // For now, assuming /users/profile PUT expects a similar structure, potentially without password
      // Or adjust the payload based on the actual backend endpoint requirement.
      // Let's assume it expects the fields available in `profile.value` plus potentially others.
      // **Crucially, the backend /users/profile PUT likely expects UserCreateDto structure.**
      // This means we might need to add password and displayName fields if updating those.
      // For now, sending only the fields available in the ref:
      const payload = {
        email: updatedProfile.email,
        firstName: updatedProfile.firstName,
        lastName: updatedProfile.lastName,
        phone: updatedProfile.phone,
        // Missing: password, displayName. If needed, collect them in the profile form.
        // If password is required for update, it needs secure handling.
      };

      const response = await api.put('/users/profile', payload);

      // Update local profile state with the response from the backend
      profile.value = {
        email: response.data.email || '',
        firstName: response.data.firstName || '',
        lastName: response.data.lastName || '',
        phone: response.data.phone || ''
      };
    } catch (error) {
      console.error("Failed to update profile:", error);
      throw error; // Re-throw to be handled by component
    }
  }

  /**
   * Logs out the current user by clearing the token, username, role, userId, and profile data.
   */
  function logout() {
    token.value = null;
    username.value = null;
    role.value = null;
    userId.value = null; // Clear userId
    profile.value = { email: '', firstName: '', lastName: '', phone: '' }; // Reset profile
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
    localStorage.removeItem('userId'); // Remove userId from storage
  }

  // Computed getters for accessing state reactively.
  const loggedIn = computed(() => token.value !== null);
  const getUsername = computed(() => username.value); // This holds the email
  const getToken = computed(() => token.value);
  const getUserId = computed(() => userId.value);
  const getUserRole = computed(() => role.value); // Getter for role

  return {
    token,
    username,
    profile,
    role, // Expose role directly if needed
    userId, // Expose userId directly if needed
    login,
    verifyLogin,
    registerUser,
    fetchProfile,
    updateProfile,
    logout,
    loggedIn,
    getUsername,
    getToken,
    getUserId,
    getUserRole // Expose role getter
  };
});
