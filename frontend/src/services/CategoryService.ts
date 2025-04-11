/**
 * Category management service module.
 *
 * This service provides methods for retrieving and managing categories
 * in the marketplace system, with both admin and public endpoints.
 *
 * @module CategoryService
 */
import api from '@/services/api/axiosInstance'; // [cite: project V e2e 13/frontend/src/services/api/axiosInstance.ts]
import type { Category } from '@/models/Category'; // [cite: project V e2e 13/frontend/src/models/Category.ts]

// Define the structure expected by the backend CategoryRequestDto
// Note: parentId is expected, not the full parent object
interface CategoryRequestPayload {
  name: string;
  imageUrl?: string | null; // Optional
  parentId?: number | string | null; // Optional
}


/**
 * Fetches all available categories from the system.
 *
 * Makes a GET request to retrieve the complete list of categories.
 * This endpoint is publicly accessible.
 *
 * @returns {Promise<Category[]>} Promise resolving to array of category objects
 * @throws {Error} When the request fails or network issues occur
 */
export async function fetchCategories(): Promise<Category[]> {
  const response = await api.get<Category[]>('/category/all'); // [cite: project V e2e 13/backend/src/main/java/stud/ntnu/backend/controller/CategoryController.java]
  return response.data;
}

/**
 * Creates a new category in the system.
 *
 * Makes a POST request to add a category. Requires admin authentication.
 * The payload matches CategoryRequestDto.
 *
 * @param {Omit<Category, 'id' | 'parent'> & { parentId?: number | string | null }} categoryData - Category data without ID, using parentId.
 * @returns {Promise<Category>} Promise resolving to the created category with server-assigned ID
 * @throws {Error} When the request fails due to validation issues, authentication problems, or network errors
 */
export async function addCategory(categoryData: Omit<Category, 'id' | 'parent'> & { parentId?: number | string | null }): Promise<Category> {
  // Construct payload matching CategoryRequestDto
  const payload: CategoryRequestPayload = {
    name: categoryData.name,
    imageUrl: categoryData.imageUrl || null, // Ensure null if empty
    parentId: categoryData.parentId ?? null // Use parentId if provided
  };
  const response = await api.post<Category>('/admin/category', payload);
  return response.data;
}

/**
 * Updates an existing category.
 *
 * Makes a PUT request to modify category details. Requires admin authentication.
 * Sends ID in the URL path and CategoryRequestDto fields in the body.
 *
 * @param {Category} category - The category object with updated information (must include id).
 * @returns {Promise<Category>} Promise resolving to the updated category.
 * @throws {Error} If category ID is missing, or if the request fails.
 */
export async function updateCategory(category: Category): Promise<Category> {
  // Ensure the category has an ID for updating
  if (category.id === null || category.id === undefined) {
    throw new Error("Category ID is required for updating.");
  }

  const categoryId = category.id;

  // Create payload matching CategoryRequestDto structure
  const payload: CategoryRequestPayload = {
    name: category.name,
    imageUrl: category.imageUrl || null, // Use null if imageUrl is empty/undefined
    // Extract parent ID if parent object exists, otherwise null
    parentId: category.parent?.id ?? null
  };

  // Construct the correct URL with ID in the path
  const url = `/admin/category/${categoryId}`;

  // Make the PUT request
  const response = await api.put<Category>(url, payload);
  return response.data;
}


/**
 * Deletes a category from the system.
 *
 * Makes a DELETE request to remove a category by its ID. Requires admin authentication.
 * May fail if products are still associated with this category.
 *
 * @param {number | string} id - The ID of the category to delete.
 * @returns {Promise<void>} Promise that resolves when deletion is successful.
 * @throws {Error} When deletion fails due to constraints, authorization issues, or network errors.
 */
export async function deleteCategory(id: number | string): Promise<void> {
  // Construct the correct URL with ID in the path
  const url = `/admin/category/${id}`;
  await api.delete(url);
}
