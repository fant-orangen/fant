/**
 * User management service module.
 *
 * This service provides methods for user-related operations including authentication,
 * registration, profile management, and administrative user management functions.
 *
 * @module UserService
 */
import api from '@/services/api/axiosInstance'
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
    throw error;
  }
}

/**
 * Represents the payload structure required for user registration.
 * Corresponds to UserCreateDto in the backend.
 *
 * @interface UserCreatePayload
 */
interface UserCreatePayload {
  email: string;
  password: string;
  displayName: string;
  firstName: string;
  lastName: string;
  phone: string;
}

/**
 * Registers a new user account in the system.
 *
 * Sends user registration data to the backend's registration endpoint.
 * Does not automatically log the user in.
 *
 * @param {UserCreatePayload} userData - Object containing user registration details
 * @returns {Promise<any>} Promise resolving to the response from the registration endpoint
 * @throws {Error} If registration fails due to validation issues or network errors
 */
export async function register(userData: UserCreatePayload) {
  return await axiosInstance.post('/auth/register', userData)
}

/**
 * Registers a new user account in the system.
 *
 * Sends user registration data to the backend's registration endpoint.
 * Does not automatically log the user in.
 *
 * @param {UserCreatePayload} userData - Object containing user registration details
 * @returns {Promise<any>} Promise resolving to the response from the registration endpoint
 * @throws {Error} If registration fails due to validation issues or network errors
 */
interface BackendUser {
  id: number | string;
  email: string;
  displayName: string;
  role: 'USER' | 'ADMIN';
  firstName: string | null;
  lastName: string | null;
  phone: string | null;
  createdAt: string;
  updatedAt: string;
}

/**
 * Generic interface for Spring Boot paginated responses.
 *
 * Represents the structure of a Spring Data Page object when serialized to JSON.
 * Used for endpoints that return paginated data.
 *
 * @interface SpringPage
 * @template T - The type of objects contained in the content array
 */
interface SpringPage<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;         // True if this is the first page
  last: boolean;          // True if this is the last page
  empty: boolean;         // True if the content list is empty

}

/**
 * Type alias for a paginated response containing user objects.
 * Used for admin user listing endpoints.
 *
 * @typedef {SpringPage<BackendUser>} PaginatedUserResponse
 */
type PaginatedUserResponse = SpringPage<BackendUser>;

/**
 * Payload structure for updating user information via admin endpoints.
 * Maps to UserCreateDto.java in the backend.
 *
 * @interface AdminUserUpdatePayload
 */
interface AdminUserUpdatePayload {
  email: string;
  password?: string;
  displayName: string;
  firstName: string | null;
  lastName: string | null;
  phone: string | null;
}

/**
 * Fetches a paginated list of users for administrative purposes.
 *
 * Makes a GET request to retrieve users with pagination and optional filtering.
 * Requires administrative privileges.
 *
 * @param {number} page - Page index (0-based)
 * @param {number} size - Number of users per page
 * @param {Record<string, any>} [params={}] - Optional filter/search parameters
 * @returns {Promise<PaginatedUserResponse>} Promise resolving to paginated user data
 * @throws {Error} If the request fails due to authorization issues or network errors
 */
export async function fetchAdminUsers(page: number, size: number, params: Record<string, any> = {}): Promise<PaginatedUserResponse> {
  try {
    // Make the GET request to the admin endpoint
    const response = await api.get<PaginatedUserResponse>('/admin/users', {
      params: { page, size, ...params }
    });
    return response.data;
  } catch (error) {
    throw error;
  }
}


/**
 * Updates a user's details via the administrative API.
 *
 * Makes a PUT request to modify user information. Requires administrative privileges.
 *
 * @param {number|string} id - The ID of the user to update
 * @param {AdminUserUpdatePayload} userData - The updated user data
 * @returns {Promise<BackendUser>} Promise resolving to the updated user data
 * @throws {Error} If the update fails due to validation or authorization issues
 */
export async function updateAdminUser(id: number | string, userData: AdminUserUpdatePayload): Promise<BackendUser> {
  try {
    if (!userData.password) {
    }
    // Make the PUT request to the admin endpoint
    const response = await api.put<BackendUser>(`/admin/users/${id}`, userData);
    return response.data;
  } catch (error) {
    throw error;
  }
}

/**
 * Deletes a user account via the administrative API.
 *
 * Makes a DELETE request to remove a user. Requires administrative privileges.
 *
 * @param {number|string} id - The ID of the user to delete
 * @returns {Promise<void>} Promise that resolves when deletion is successful
 * @throws {Error} If deletion fails due to authorization issues or network errors
 */
export async function deleteAdminUser(id: number | string): Promise<void> {
  try {
    await api.delete(`/admin/users/${id}`);
  } catch (error) {
    throw error;
  }
}

/**
 * Fetches detailed information for a specific user by ID.
 *
 * Makes a GET request to retrieve a user's profile information.
 * Requires administrative privileges.
 *
 * @param {number|string} id - The ID of the user to fetch
 * @returns {Promise<BackendUser>} Promise resolving to the user's profile data
 * @throws {Error} If the fetch fails due to authorization issues or network errors
 */
export async function fetchAdminUserById(id: number | string): Promise<BackendUser> {
  try {
    const response = await api.get<BackendUser>(`/admin/users/${id}`);

    const user: BackendUser = {
      id: response.data.id ?? id,
      email: response.data.email ?? '',
      displayName: response.data.displayName,
      role: response.data.role ?? 'USER',
      firstName: response.data.firstName ?? null,
      lastName: response.data.lastName ?? null,
      phone: response.data.phone ?? null,
      createdAt: response.data.createdAt,
      updatedAt: '',
    };
    return user;

  } catch (error) {
    throw error;
  }
}

