import api from '@/services/api/axiosInstance' // Keep using axios instance
import axiosInstance from '@/axiosConfig'


/**
 * Fetches the currently authenticated user's ID from the server.
 * This ID comes from the JWT token on the backend.
 *
 * @returns A promise resolving to the user's ID
 * @throws Error if the request fails
 */
export async function fetchCurrentUserId(): Promise<number> {
  try {
    const response = await api.get<number>('/users/id');
    return response.data;
  } catch (error) {
    console.error('Error fetching user ID:', error);
    throw error;
  }
}

// Define the type matching UserCreateDto from backend
interface UserCreatePayload {
  email: string;
  password: string;
  displayName: string;
  firstName: string;
  lastName: string;
  phone: string;
}

/**
 * Sends user registration data to the backend /auth/register endpoint.
 * @param userData - Object containing user registration details matching UserCreatePayload.
 * @returns Axios response promise.
 */
export async function register(userData: UserCreatePayload) { // Use the defined interface
                                                              // The payload sent to the backend must match the UserCreateDto structure
  return await axiosInstance.post('/auth/register', userData)
}
