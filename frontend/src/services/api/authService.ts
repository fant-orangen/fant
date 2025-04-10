/**
 * Authentication service module for handling user authentication operations.
 *
 * This service provides methods for authenticating users against the backend API,
 * including login functionality with token retrieval.
 *
 * @module authService
 */
import axiosInstance from '@/axiosConfig';

/**
 * Authenticates a user and retrieves an access token.
 *
 * Makes a POST request to the authentication endpoint with user credentials
 * and returns the server response containing authentication tokens and user info.
 *
 * @param {Object} credentials - The user credentials
 * @param {string} credentials.username - User's email address used as username
 * @param {string} credentials.password - User's password
 * @returns {Promise<AxiosResponse>} Promise resolving to the server response
 * @throws {Error} When authentication fails or network issues occur
 */
export async function fetchToken({ username, password }: { username: string, password: string }) {
  return await axiosInstance.post('/auth/login',
    JSON.stringify({
      email: username,
      password: password
    }),
    {
      headers: {
        'Content-Type': 'application/json'
      }
    }
  );
}
