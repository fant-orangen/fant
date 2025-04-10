/**
 * Category management service module.
 *
 * This service provides methods for retrieving and managing categories
 * in the marketplace system, with both admin and public endpoints.
 *
 * @module CategoryService
 */
import api from '@/services/api/axiosInstance.ts';
import type { Category } from '@/models/Category.ts';

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
  const response = await api.get<Category[]>('/category/all');
  return response.data;
}

/**
 * Creates a new category in the system.
 *
 * Makes a POST request to add a category. Requires admin authentication.
 *
 * @param {Category} category - The category information to create
 * @returns {Promise<Category>} Promise resolving to the created category with server-assigned ID
 * @throws {Error} When the request fails due to validation issues, authentication problems, or network errors
 */
export async function addCategory(category: Category): Promise<Category> {
  const response = await api.post<Category>('/admin/category', category);
  return response.data;
}

/**
 * Updates an existing category.
 *
 * Makes a PUT request to modify category details. Requires admin authentication.
 *
 * @param {Category} category - The category object with updated information
 * @returns {Promise<Category>} Promise resolving to the updated category
 * @throws {Error} When the request fails due to validation issues, authentication problems, or network errors
 */
export async function updateCategory(category: Category): Promise<Category> {
  const response = await api.put<Category>(`/admin/category`, category);
  return response.data;
}

/**
 * Deletes a category from the system.
 *
 * Makes a DELETE request to remove a category by its ID. Requires admin authentication.
 * May fail if products are still associated with this category.
 *
 * @param {number} id - The ID of the category to delete
 * @returns {Promise<void>} Promise that resolves when deletion is successful
 * @throws {Error} When deletion fails due to constraints, authorization issues, or network errors
 */
export async function deleteCategory(id: number): Promise<void> {
  await api.delete(`/admin/category/${id}`);
}
