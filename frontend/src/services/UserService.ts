import api from '@/services/api/axiosInstance' // Keep using axios instance

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
