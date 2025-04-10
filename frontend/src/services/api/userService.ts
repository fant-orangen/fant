/**
 * User management service module.
 *
 * This service provides methods for user-related operations including
 * registration and other user account management functions.
 */
import axiosInstance from '@/axiosConfig'

/**
 * Represents the data structure required for user registration.
 * @interface RegistrationData
 */
class RegistrationData {}

/**
 * Registers a new user with the system.
 *
 * Makes a POST request to the registration endpoint with the user's registration data
 * and returns the server response.
 *
 * @param {RegistrationData} userData - The user's registration information
 * @returns {Promise<import('axios').AxiosResponse>} Promise resolving to the server response
 * @throws {Error} When registration fails or network issues occur
 */
export async function register(userData: RegistrationData) {
  return await axiosInstance.post('/auth/register', userData)
}
