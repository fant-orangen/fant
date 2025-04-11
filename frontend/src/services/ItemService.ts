/**
 * Item management service module.
 *
 * This service provides methods for creating, retrieving, updating, deleting,
 * searching, and managing marketplace items, including handling views and favorites.
 * It also includes functionality for fetching recommended items based on user behavior.
 *
 * @module ItemService
 */

// Import the configured Axios instance for API calls
import api from '@/services/api/axiosInstance';

// Import necessary type definitions for items and recommendations
import type {
  ItemPreviewType,
  ItemDetailsType,
  CreateItemType,
  // ItemFavoritesType is defined but not directly used as a return type here
  PaginatedItemPreviewResponse,
} from '@/models/Item';
import type { CategoryRecommendation } from '@/models/Recommendation';


/**
 * Represents a paginated response containing item previews.
 * Used by the fetchFavoriteItems function (though fetchPagedFavoriteItems uses PaginatedItemPreviewResponse).
 *
 * @interface PaginatedItemsResponse
 */
export interface PaginatedItemsResponse {
  items: ItemPreviewType[];
  totalItems: number;
}

/**
 * Common parameters for paginated requests.
 *
 * @typedef {Object} PageableParams
 * @property {number} page - Page index (0-based)
 * @property {number} size - Number of items per page
 * @property {string} [sort] - Optional sorting instruction (e.g., "price,desc")
 */
type PageableParams = {
  page: number;
  size: number;
  sort?: string;
};

/**
 * Parameters for advanced item searching.
 * Maps to the backend ItemSearchDto structure.
 * Includes optional filtering by sellerId.
 *
 * @interface ItemSearchParams
 */
export interface ItemSearchParams {
  searchTerm?: string | null;
  minPrice?: number | null;
  maxPrice?: number | null;
  status?: string; // e.g., 'ACTIVE'
  categoryName?: string | null;
  userLatitude?: number | null;
  userLongitude?: number | null;
  maxDistance?: number | null; // in km
  sellerId?: string | number | null; // Added sellerId for public profile filtering
  page?: number; // 0-based index
  size?: number;
  sort?: string; // e.g., 'price,asc' or 'price,desc'
}


// --- CRUD Operations ---

/**
 * Creates a new item in the marketplace.
 *
 * Makes a POST request to create an item with the provided information.
 * Requires user authentication.
 *
 * @param {CreateItemType} item - The item information to create
 * @returns {Promise<number>} Promise resolving to the ID of the created item
 * @throws {Error} When the request fails due to validation issues or network errors
 */
export async function createItem(item: CreateItemType): Promise<number> {
  const response = await api.post<number>('/items', item);
  return response.data;
}

/**
 * Updates an existing item in the marketplace.
 *
 * Makes a PUT request to modify an item's details.
 * Requires user authentication and ownership of the item.
 *
 * @param {number} id - The ID of the item to update
 * @param {CreateItemType} item - The updated item information
 * @returns {Promise<CreateItemType>} Promise resolving to the updated item data
 * @throws {Error} When the request fails due to validation issues or network errors
 */
export async function updateItem(id: number, item: CreateItemType): Promise<CreateItemType> {
  const response = await api.put<CreateItemType>(`/items/${id}`, item);
  return response.data;
}

/**
 * Deletes an item from the marketplace.
 *
 * Makes a DELETE request to remove an item.
 * Requires user authentication and ownership of the item.
 *
 * @param {number} id - The ID of the item to delete
 * @returns {Promise<void>} Promise that resolves when deletion is successful
 * @throws {Error} When deletion fails due to authorization issues or network errors
 */
export async function deleteItem(id: number): Promise<void> {
  await api.delete(`/items/${id}`);
}

/**
 * Allows administrators to delete any item in the system.
 *
 * @param {number|string} id - The ID of the item to delete
 * @returns {Promise<void>} Promise that resolves when deletion is successful
 * @throws {Error} When deletion fails due to authentication issues or network errors
 */
export async function adminDeleteItem(id: number | string): Promise<void> {
  await api.delete(`/admin/item/${id}`);
}


// --- Fetching Operations ---

/**
 * Fetches a paginated list of all item previews.
 *
 * Makes a GET request to retrieve a default set of items, typically the first page.
 * This endpoint is publicly accessible.
 *
 * @returns {Promise<PaginatedItemPreviewResponse>} Promise resolving to paginated item preview data
 * @throws {Error} When the request fails or network issues occur
 */
export async function fetchPreviewItems(): Promise<PaginatedItemPreviewResponse> {
  try {
    const response = await api.get<PaginatedItemPreviewResponse>('/items/all');
    return response.data;
  } catch (error) {
    console.error('Error fetching preview items:', error);
    throw error;
  }
}

/**
 * Searches for items based on various criteria using the backend API.
 * Now includes optional filtering by sellerId.
 *
 * @param params - An object containing the search criteria (ItemSearchParams).
 * @returns A promise resolving to a paginated response of item previews.
 * @throws Error if the request fails.
 */
export async function searchItems(params: ItemSearchParams): Promise<PaginatedItemPreviewResponse> {
  try {
    // Filter out null, undefined, or empty string values before sending
    const filteredParams = Object.entries(params).reduce((acc, [key, value]) => {
      if (value !== null && value !== undefined && value !== '') {
        acc[key as keyof ItemSearchParams] = value as any;
      }
      return acc;
    }, {} as Partial<ItemSearchParams>);

    console.log("Searching items with params:", filteredParams); // For debugging

    const response = await api.get<PaginatedItemPreviewResponse>('/items/search', {
      params: filteredParams
    });
    return response.data;
  } catch (error) {
    console.error("Error during item search:", error);
    throw error;
  }
}

/**
 * Fetches detailed information for a specific item by its ID.
 *
 * Makes a GET request to the item details endpoint.
 *
 * @param {string | number} itemId - The ID of the item to fetch details for.
 * @returns {Promise<ItemDetailsType>} Promise resolving to the detailed item data.
 * @throws {Error} If the request fails (e.g., item not found, network error).
 */
export async function fetchItem(itemId: string | number): Promise<ItemDetailsType> {
  try {
    const response = await api.get<ItemDetailsType>(`/items/details/${itemId}`);
    console.log(response.data, 'here is data for item', itemId); // Log fetched data
    return response.data;
  } catch (error) {
    console.error(`Error fetching item with ID ${itemId}:`, error);
    throw error;
  }
}

/**
 * Fetches items based on category distribution probabilities (for recommendations).
 *
 * @param {CategoryRecommendation} recommendation - An object containing category probability distribution.
 * @param {number} [limit=6] - Maximum number of items to return.
 * @returns {Promise<PaginatedItemPreviewResponse>} A Promise resolving to paginated items matching the distribution.
 * @throws {Error} If the request fails.
 */
export async function fetchItemsByDistribution(
  recommendation: CategoryRecommendation,
  limit: number = 6,
): Promise<PaginatedItemPreviewResponse> {
  try {
    const response = await api.post<PaginatedItemPreviewResponse>('/items/view/recommended_items', {
      distribution: recommendation.distribution,
      limit,
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching items by distribution:', error);
    throw error;
  }
}

/**
 * Fetches a paginated list of favorite items for the currently logged-in user.
 *
 * @param {number} page - Page index (0-based).
 * @param {number} size - Number of items per page.
 * @param {string} [sort] - Optional sorting string (e.g., "createdAt,desc").
 * @returns {Promise<PaginatedItemPreviewResponse>} Promise resolving to a paginated response of favorite items.
 * @throws {Error} If the request fails.
 */
export async function fetchPagedFavoriteItems(
  page: number,
  size: number,
  sort?: string
): Promise<PaginatedItemPreviewResponse> {
  try {
    const params: PageableParams = { page, size };
    if (sort) params.sort = sort;
    const { data } = await api.get<PaginatedItemPreviewResponse>('/favorite', { params });
    return data;
  } catch (error) {
    console.error('Error fetching favorite items:', error);
    throw error;
  }
}

/**
 * Fetches a paginated set of items listed by the currently authenticated user.
 *
 * @param {number} page - Page index (0-based).
 * @param {number} size - Number of items per page.
 * @param {string} [sort] - Optional sorting (e.g., "createdAt,desc").
 * @returns {Promise<PaginatedItemPreviewResponse>} A promise resolving to a paginated response of the user's items.
 * @throws {Error} If the request fails.
 */
export async function fetchMyPagedItems(
  page: number,
  size: number,
  sort?: string,
): Promise<PaginatedItemPreviewResponse> {
  try {
    const params: PageableParams = { page, size };
    if (sort) {
      params.sort = sort;
    }
    const response = await api.get<PaginatedItemPreviewResponse>('/items/my', { params });
    return response.data;
  } catch (error) {
    console.error('Error fetching user\'s items:', error);
    throw error;
  }
}

// --- Item Interaction ---

/**
 * Records that a user viewed an item.
 * The user is automatically identified through the authentication token.
 *
 * @param {string | number} itemId - The ID of the viewed item.
 * @returns {Promise<{ status: number }>} Promise resolving with the response status object, or 500 on error.
 */
export async function recordItemView(itemId: string | number): Promise<{ status: number }> {
  try {
    const response = await api.post(`/items/view/post/${itemId}`);
    return { status: response.status };
  } catch (error) {
    console.error(`Error recording view for item ID ${itemId}:`, error);
    return { status: 500 }; // Return a generic error status
  }
}
