/**
 * Deletes an image for an item from the backend and Cloudinary.
 *
 * Makes a DELETE request with query parameters identifying the item and image URL.
 * Validates the response status to ensure successful deletion.
 *
 * @param {number} itemId - The ID of the item the image belongs to
 * @param {string} imageUrl - The URL of the image to delete
 * @returns {Promise<void>} Promise that resolves when the deletion is complete
 * @throws {Error} If deletion fails or server returns non-200 status
 */
import api from '@/services/api/axiosInstance' // [cite: uploaded:frontend 6/frontend/src/services/api/axiosInstance.ts]
import type {
  ItemPreviewType,
  ItemDetailsType,
  CreateItemType,
  ItemFavoritesType,
  PaginatedItemPreviewResponse,
} from '@/models/Item' // [cite: uploaded:frontend 6/frontend/src/models/Item.ts]
import type { CategoryRecommendation } from '@/models/Recommendation' // [cite: uploaded:frontend 6/frontend/src/models/Recommendation.ts]

/**
 * Represents a paginated response containing item previews.
 * Used by the fetchFavoriteItems function.
 *
 * @interface PaginatedItemsResponse
 */
export interface PaginatedItemsResponse {
  items: ItemPreviewType[]
  totalItems: number
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
  const response = await api.post<number>('/items', item)
  return response.data
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
    const response = await api.get<PaginatedItemPreviewResponse>('/items/all')
    return response.data
  } catch (error) {
    throw error
  }
}

/**
 * Parameters for advanced item searching.
 * Maps to the backend ItemSearchDto structure.
 *
 * @interface ItemSearchParams
 */
export interface ItemSearchParams {
  searchTerm?: string | null;
  minPrice?: number | null;
  maxPrice?: number | null;
  status?: string; // e.g., 'ACTIVE'
  categoryName?: string | null; // Use category name if backend expects it, or categoryId if needed
  userLatitude?: number | null;
  userLongitude?: number | null;
  maxDistance?: number | null; // in km
  page?: number; // 0-based index
  size?: number;
  sort?: string; // e.g., 'price,asc' or 'price,desc'
}

/**
 * Searches for items based on various criteria using the backend API.
 *
 * @param params - An object containing the search criteria.
 * @returns A promise resolving to a paginated response of item previews.
 * @throws Error if the request fails.
 */
export async function searchItems(params: ItemSearchParams): Promise<PaginatedItemPreviewResponse> {
  try {
    const filteredParams = Object.entries(params).reduce((acc, [key, value]) => {
      if (value !== null && value !== undefined && value !== '') { // Also filter empty strings if necessary
        acc[key as keyof ItemSearchParams] = value as any; // Cast value to 'any' is okay here after check
      }
      return acc;
    }, {} as Partial<ItemSearchParams>); // Use Partial<ItemSearchParams> as the initial type

    const response = await api.get<PaginatedItemPreviewResponse>('/items/search', {
      params: filteredParams
    });
    return response.data;
  } catch (error) {
    throw error;
  }
}

// Existing function to fetch details for a single item
export async function fetchItem(itemId: string | number): Promise<ItemDetailsType> {
  try {
    const response = await api.get<ItemDetailsType>(`/items/details/${itemId}`)
    console.log(response.data, 'here is data for item', itemId) // Log fetched data
    return response.data
  } catch (error) {
    console.error(`Error fetching item with ID ${itemId}:`, error)
    throw error
  }
}

/**
 * Fetches items based on category distribution probabilities.
 *
 * @param recommendation - An object containing category probability distribution
 * @param limit - Optional maximum number of items to return (default: 5)
 * @returns A Promise resolving to an array of items matching the distribution
 */
export async function fetchItemsByDistribution(
  recommendation: CategoryRecommendation,
  limit: number = 6, // This number can be changed to determine the number of items to display
): Promise<PaginatedItemPreviewResponse> {
  try {
    const response = await api.post<PaginatedItemPreviewResponse>('/items/view/recommended_items', {
      distribution: recommendation.distribution,
      limit,
    })

    return response.data
  } catch (error) {
    throw error
  }
}

/**
 * Records that a user viewed an item.
 * The user is automatically identified through the authentication token.
 *
 * @param itemId - The ID of the viewed item
 * @returns Promise that resolves when the view has been recorded
 */
export async function recordItemView(itemId: string | number): Promise<{ status: number }> {
  try {
    const response = await api.post(`/items/view/post/${itemId}`)
    return { status: response.status }
  } catch (error) {
    return { status: 500 }
  }
}

/**
 * Fetches a paginated list of favorite items for the currently logged-in user.
 *
 * @param page - Page index (0-based)
 * @param size - Number of items per page
 * @param sort - Optional sorting string (e.g., "createdAt,desc")
 * @returns Promise resolving to a PaginatedItemPreviewResponse
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
    throw error;
  }
}

/**
 * Fetches a paginated set of items listed by the currently authenticated user.
 *
 * @param page - Page index (0-based)
 * @param size - Number of items per page
 * @param sort - Optional sorting (e.g., "createdAt,desc")
 * @returns A promise resolving to a paginated response of the user's items
 * @throws {Error} If the request fails
 */
export async function fetchMyPagedItems(
  page: number,
  size: number,
  sort?: string,
): Promise<PaginatedItemPreviewResponse> {
  try {
    // Use the PageableParams type
    const params: PageableParams = { page, size };
    if (sort) {
      params.sort = sort;
    }

    // Pass params object directly to axios
    const response = await api.get<PaginatedItemPreviewResponse>('/items/my', { params })
    return response.data
  } catch (error) {
    throw error
  }
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
