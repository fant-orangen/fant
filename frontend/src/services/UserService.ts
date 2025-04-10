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
export async function register(userData: UserCreatePayload) {
  return await axiosInstance.post('/auth/register', userData)
}

/**
 * Interface representing the structure of a User object
 * as returned by the backend API (matching backend User.java entity).
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
 * Generic interface representing the structure of Spring Boot's Page object
 * when serialized to JSON.
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
 * Specific type alias for a paginated response containing BackendUser objects.
 */
type PaginatedUserResponse = SpringPage<BackendUser>;

/**
 * Interface for the payload when updating a user via admin endpoints.
 * This should match the structure of the backend's UserCreateDto.java.
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
 * Fetches a paginated list of users for admin management.
 * @param page - Page index (0-based)
 * @param size - Number of users per page
 * @param params - Optional filter/search parameters (e.g., { email: 'search@example.com' })
 * @returns Promise resolving to a PaginatedUserResponse object.
 */
export async function fetchAdminUsers(page: number, size: number, params: Record<string, any> = {}): Promise<PaginatedUserResponse> {
  try {
    // Make the GET request to the admin endpoint
    const response = await api.get<PaginatedUserResponse>('/admin/users', {
      params: { page, size, ...params }
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching admin users:', error);
    throw error;
  }
}


/**
 * Updates a user's details via the admin endpoint.
 * @param id - The ID of the user to update.
 * @param userData - The updated user data (matching AdminUserUpdatePayload).
 * @returns Promise resolving to the updated user data (as BackendUser).
 */
export async function updateAdminUser(id: number | string, userData: AdminUserUpdatePayload): Promise<BackendUser> {
  try {
    if (!userData.password) {
      console.warn("Attempting admin user update without password. Backend might require it.");
    }
    // Make the PUT request to the admin endpoint
    const response = await api.put<BackendUser>(`/admin/users/${id}`, userData);
    return response.data;
  } catch (error) {
    console.error(`Error updating user ${id} via admin:`, error);
    throw error;
  }
}

/**
 * Deletes a user via the admin endpoint.
 * @param id - The ID of the user to delete.
 * @returns Promise resolving when deletion is successful.
 */
export async function deleteAdminUser(id: number | string): Promise<void> {
  try {
    await api.delete(`/admin/users/${id}`);
  } catch (error) {
    console.error(`Error deleting user ${id} via admin:`, error);
    throw error;
  }
}

export async function fetchAdminUserById(id: number | string): Promise<BackendUser> {
  try {
    const response = await api.get<BackendUser>(`/admin/users/${id}`);
    console.warn("Using public /users/{id} endpoint. Data might be limited (UserResponseDto).");

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
    console.error(`Error fetching user ${id} via public endpoint:`, error);
    throw error;
  }
}

