import api from '@/services/api/axiosInstance.ts'
import type { ItemPreviewType, ItemFavoritesType } from '@/models/Item.ts';

export interface PaginatedItemsResponse {
  items: ItemPreviewType[]
  totalItems: number
}

/**
 * Add an item to user's favorites
 * @param itemId ID of the item to add to favorites
 * @returns Promise that resolves when the item is added
 */
export async function addFavorite(itemId: string | number): Promise<void> {
  console.log("added fav:", itemId);
  await api.post(`/favorite/${itemId}`);
}

/**
 * Remove an item from user's favorites
 * @param itemId ID of the item to remove from favorites
 * @returns Promise that resolves when the item is removed
 */
export async function removeFavorite(itemId: string | number): Promise<void> {
  await api.delete(`/favorite/${itemId}`);
}

export async function fetchFavoriteItemsShort(): Promise<ItemFavoritesType[]> {
  const { data } = await api.get<ItemFavoritesType[]>('/favorite');
  return data;
}

/**
 * Checks if a specific item is favorited by the current user by calling the dedicated backend endpoint.
 * @param itemId ID of the item to check
 * @returns Promise resolving to true if favorited, false otherwise.
 */
export async function checkIsFavorite(itemId: string | number): Promise<boolean> {
  // Ensure itemId is valid before making the call
  if (!itemId && itemId !== 0) { // Check for null, undefined, empty string, allow 0
    console.warn('checkIsFavorite called with invalid itemId:', itemId);
    return false;
  }

  try {
    // Call the new backend endpoint GET /api/favorite/status/{itemId}
    console.log(`Checking favorite status for item: ${itemId}`); // Add log
    const response = await api.get<boolean>(`/favorite/status/${itemId}`);
    console.log(`Favorite status for item ${itemId}: ${response.data}`); // Add log
    return response.data; // Endpoint directly returns true or false
  } catch (error: any) {
    // If the backend returns 404 (e.g., item deleted), treat as not favorite
    if (error.response?.status === 404) {
      console.warn(`checkIsFavorite received 404 for item ${itemId}, treating as not favorite.`);
      return false;
    }
    // Log other errors but return false as a safe default
    console.error(`Error checking favorite status for item ${itemId}:`, error);
    return false;
  }
}


/**
 * Get favorite count for an item
 * @param itemId ID of the item to get count for
 * @returns Promise that resolves to count of favorites
 */
export async function getFavoriteCount(itemId: string | number): Promise<number> {
  const response = await api.get(`/favorite/count/${itemId}`);
  return response.data;
}
