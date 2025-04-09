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



/**
 * Interface representing the structure of a User object
 * as returned by the backend API (matching backend User.java entity).
 */
interface BackendUser {
  id: number | string; // Assuming ID is number or string
  email: string;
  displayName: string;
  role: 'USER' | 'ADMIN'; // Matches the Role enum from backend
  firstName: string | null; // Nullable fields
  lastName: string | null;  // Nullable fields
  phone: string | null;     // Nullable fields
  createdAt: string; // Assuming ISO date string
  updatedAt: string; // Assuming ISO date string
  // passwordHash is NOT included as it's sensitive and usually omitted
}

/**
 * Generic interface representing the structure of Spring Boot's Page object
 * when serialized to JSON.
 */
interface SpringPage<T> {
  content: T[];           // The list of items on the current page
  totalPages: number;     // Total number of pages available
  totalElements: number;  // Total number of items across all pages
  size: number;           // The number of items per page
  number: number;         // The current page number (0-based index)
  first: boolean;         // True if this is the first page
  last: boolean;          // True if this is the last page
  empty: boolean;         // True if the content list is empty
  // Spring Page includes other fields like sort, pageable, numberOfElements,
  // but these are often sufficient for frontend pagination.
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
  password?: string; // Include password if the admin PUT endpoint requires/allows setting it.
                     // If not required, make it optional or remove if backend ignores it for updates.
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
      params: { page, size, ...params } // Pass pagination and any filter params
    });
    // The response.data should already match the PaginatedUserResponse structure
    return response.data;
  } catch (error) {
    console.error('Error fetching admin users:', error);
    throw error; // Re-throw to allow calling component to handle it
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
    // Ensure password handling matches backend expectation for admin updates
    if (!userData.password) {
      // If backend requires password for validation even on admin updates,
      // you might need a strategy here (e.g., prompt admin, or disallow updates without it).
      console.warn("Attempting admin user update without password. Backend might require it.");
    }
    // Make the PUT request to the admin endpoint
    const response = await api.put<BackendUser>(`/admin/users/${id}`, userData);
    // The response.data should match the BackendUser structure
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

interface UserResponseDto {
  displayName: string;
  createdAt: string; // Assuming these are the fields based on UserResponseDto.java
  // Add email, firstName, lastName, phone if they are *also* in UserResponseDto
  email?: string;
  firstName?: string | null;
  lastName?: string | null;
  phone?: string | null;
  // NOTE: 'role' and 'id' might NOT be in the public DTO.
  id?: number | string; // Add if available
  role?: 'USER' | 'ADMIN'; // Add if available
}


export async function fetchAdminUserById(id: number | string): Promise<BackendUser> { // Keep returning BackendUser for now, or adjust
  try {
    // *** Change the URL from /admin/users/{id} to /users/{id} ***
    const response = await api.get<UserResponseDto>(`/users/${id}`); // <-- CALL PUBLIC ENDPOINT
    console.warn("Using public /users/{id} endpoint. Data might be limited (UserResponseDto).");

    // You might need to map UserResponseDto to BackendUser if structures differ significantly
    // For example, 'role' might be missing.
    const user: BackendUser = {
      id: response.data.id ?? id, // Use response ID if available, else prop ID
      email: response.data.email ?? '', // Get from DTO if available
      displayName: response.data.displayName,
      role: response.data.role ?? 'USER', // Role might be missing, default or handle
      firstName: response.data.firstName ?? null,
      lastName: response.data.lastName ?? null,
      phone: response.data.phone ?? null,
      createdAt: response.data.createdAt,
      updatedAt: '', // Not available in UserResponseDto
    };
    return user;

  } catch (error) {
    console.error(`Error fetching user ${id} via public endpoint:`, error);
    throw error;
  }
}

