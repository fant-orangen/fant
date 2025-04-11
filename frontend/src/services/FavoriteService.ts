/**
 * Favorite management service module.
 *
 * This service provides methods for managing user favorites in the marketplace system,
 * including adding and removing items from favorites, checking favorite status,
 * and retrieving favorite-related information.
 *
 * @module FavoriteService
 */
import api from '@/services/api/axiosInstance.ts'
import type { ItemPreviewType, ItemFavoritesType } from '@/models/Item.ts';
/**
 * Adds an item to the authenticated user's favorites.
 *
 * Makes a POST request to add the specified item to the user's favorites collection.
 * Requires user authentication.
 *
 * @param {string|number} itemId - ID of the item to add to favorites
 * @returns {Promise<void>} Promise that resolves when the item is successfully added
 * @throws {Error} When the request fails due to authentication problems or network errors
 */
export async function addFavorite(itemId: string | number): Promise<void> {
  await api.post(`/favorite/${itemId}`);
}

/**
 * Removes an item from the authenticated user's favorites.
 *
 * Makes a DELETE request to remove the specified item from the user's favorites collection.
 * Requires user authentication.
 *
 * @param {string|number} itemId - ID of the item to remove from favorites
 * @returns {Promise<void>} Promise that resolves when the item is successfully removed
 * @throws {Error} When the request fails due to authentication problems or network errors
 */
export async function removeFavorite(itemId: string | number): Promise<void> {
  await api.delete(`/favorite/${itemId}`);
}

/**
 * Checks if a specific item is favorited by the current authenticated user.
 *
 * Makes a GET request to a dedicated backend endpoint to determine favorite status.
 * Requires user authentication.
 *
 * @param {string|number} itemId - ID of the item to check favorite status for
 * @returns {Promise<boolean>} Promise resolving to true if item is favorited, false otherwise
 * @throws {Error} When the request fails due to authentication problems or network errors
 */
export async function checkIsFavorite(itemId: string | number): Promise<boolean> {
  // Ensure itemId is valid before making the call
  if (!itemId && itemId !== 0) {
    return false;
  }

  try {
    // Call the new backend endpoint GET /api/favorite/status/{itemId}
    const response = await api.get<boolean>(`/favorite/status/${itemId}`);
    return response.data; // Endpoint directly returns true or false
  } catch (error: any) {
    // If the backend returns 404 (e.g., item deleted), treat as not favorite
    if (error.response?.status === 404) {
      return false;
    }
    return false;
  }
}
