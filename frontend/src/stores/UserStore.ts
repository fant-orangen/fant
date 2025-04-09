import { defineStore } from 'pinia';
import { fetchToken } from '@/services/api/authService';
import { register } from '@/services/api/userService';
import { computed, ref } from 'vue';
import api from '@/services/api/axiosInstance';

interface RegistrationData {
  email: string;
  password: string;
  displayName: string; // Already here for registration
  firstName: string;
  lastName: string;
  phone: string;
}

// Interface for the profile state managed by the store
interface UserProfile {
  email: string;
  firstName: string;
  lastName: string;
  phone: string;
  displayName: string; // <-- Add displayName here
}


export const useUserStore = defineStore("user", () => {
  const token = ref<string | null>(localStorage.getItem('token'));
  const username = ref<string | null>(localStorage.getItem('username'));
  const role = ref<string | null>(localStorage.getItem('role'));
  const userId = ref<string | null>(localStorage.getItem('userId'));

  // Initialize profile state including displayName
  const profile = ref<UserProfile>({ // Use the UserProfile interface
    email: '',
    firstName: '',
    lastName: '',
    phone: '',
    displayName: '' // <-- Initialize displayName
  });

  // --- (login, verifyLogin, registerUser functions remain the same) ---
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

  async function verifyLogin(userEmail: string, password: string) { // Renamed 'user' to 'userEmail'
    try {
      console.log(`Starting login for user: ${userEmail}`);
      // Use userEmail for the 'username' field expected by fetchToken
      const response = await fetchToken({ username: userEmail, password: password }); // [cite: uploaded:project V 2/frontend/src/services/api/authService.ts]
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
   * Assumes the backend /api/users/profile GET endpoint returns the User entity structure.
   */
  async function fetchProfile() {
    try {
      const response = await api.get('/users/profile');
      // Map backend response to the profile ref structure, including displayName
      profile.value = {
        email: response.data.email || '',
        firstName: response.data.firstName || '',
        lastName: response.data.lastName || '',
        phone: response.data.phone || '',
        displayName: response.data.displayName || '' // <-- Map displayName
      };
    } catch (error) {
      console.error("Failed to fetch profile:", error);
      // Reset profile on error
      profile.value = { email: '', firstName: '', lastName: '', phone: '', displayName: '' };
      throw error;
    }
  }


  /**
   * Updates the user's profile by sending updated data to the backend.
   * The payload must match the backend's UserCreateDto structure.
   *
   * @param updatedProfile - An object matching UserProfile interface, plus the required password.
   */
    // The payload for update must match UserCreateDto, so it needs password and displayName
  interface UpdatePayload extends UserProfile {
    password?: string; // Password might be optional if backend handles it, but DTO requires it
  }

  async function updateProfile(updatedProfile: UpdatePayload) { // Use the extended payload type
    try {
      // Construct the payload matching UserCreateDto
      // Crucially includes displayName and password
      const payload = {
        email: updatedProfile.email,
        firstName: updatedProfile.firstName,
        lastName: updatedProfile.lastName,
        phone: updatedProfile.phone,
        displayName: updatedProfile.displayName, // <-- Include displayName
        password: updatedProfile.password        // <-- Include password
      };

      // Check if password is provided, if not, handle error or send a dummy (as per backend requirement)
      if (!payload.password) {
        // Option A: Throw an error asking user for password
        throw new Error("Password is required to update profile.");
        // Option B: Send a dummy password (ONLY if backend validation is the ONLY reason)
        // payload.password = "DUMMY_PASSWORD_FOR_VALIDATION"; // Use with caution!
      }


      const response = await api.put('/users/profile', payload);

      // Update local profile state with the response from the backend
      profile.value = {
        email: response.data.email || '',
        firstName: response.data.firstName || '',
        lastName: response.data.lastName || '',
        phone: response.data.phone || '',
        displayName: response.data.displayName || '' // <-- Update displayName
      };
    } catch (error) {
      console.error("Failed to update profile:", error);
      throw error;
    }
  }

  // --- (logout and computed getters remain the same) ---
  function logout() {
    token.value = null;
    username.value = null;
    role.value = null;
    userId.value = null; // Clear userId
    profile.value = { email: '', firstName: '', lastName: '', phone: '', displayName: '' }; // Reset profile including displayName
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
    localStorage.removeItem('userId'); // Remove userId from storage
  }

  // Check whether user is logged in
  const isLoggedInUser = computed(() => {
    return userId.value !== null && userId.value !== '0';
  })

  // Computed getters for accessing state reactively.
  const loggedIn = computed(() => token.value !== null);
  const getUsername = computed(() => username.value); // This holds the email
  const getToken = computed(() => token.value);
  const getUserId = computed(() => userId.value);
  const getUserRole = computed(() => role.value); // Getter for role

  return {
    token,
    username,
    profile, // profile now includes displayName
    role,
    userId,
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
    getUserRole,
    isLoggedInUser
  };
});
