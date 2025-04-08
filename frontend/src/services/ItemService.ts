import api from '@/services/api/axiosInstance' // [cite: uploaded:src/services/api/axiosInstance.ts]
import type {
  ItemPreviewType,
  ItemDetailsType,
  CreateItemType,
  ItemFavoritesType,
  PaginatedItemPreviewResponse
} from '@/models/Item' // [cite: uploaded:src/models/Item.ts] Adjusted import
import type { CategoryRecommendation } from '@/models/Recommendation'

// Define an interface for the expected paginated response (needed by fetchFavoriteItems)
export interface PaginatedItemsResponse {
  items: ItemPreviewType[]
  totalItems: number
}

export async function createItem(item: CreateItemType): Promise< number> {
  console.log("before post", item);
  const response = await api.post<number>('/items', item);
  console.log("after post", response.data);
  return response.data;
}

// Existing function to fetch all preview items (potentially needs pagination update too)
export async function fetchPreviewItems(): Promise<PaginatedItemPreviewResponse> {
  try {
    const response = await api.get<PaginatedItemPreviewResponse>('/items/all') //
    console.log('url: ' + api.defaults.baseURL + '/items/all')
    console.log('Data:' + response.data.totalElements)
    console.log('Items per page' + response.data.size)
    return response.data
  } catch (error) {
    console.error('Error fetching items:', error)
    throw error
  }
}

/**
 * Fetches a paginated set of item previews from the backend.
 *
 * @param page - The page index to fetch (0-based)
 * @param size - The number of items per page
 * @returns A promise resolving to a paginated response of item previews
 * @throws Error if the request fails
 */
export async function fetchPagedPreviewItems(page: number, size: number): Promise<PaginatedItemPreviewResponse> {
  try {
    const response = await api.get<PaginatedItemPreviewResponse>(`/items/page?page=${page}&size=${size}`);
    console.log('Requested page:', page, 'with size:', size);
    console.log('Backend returned:', response.data.totalElements, 'total items, size:', response.data.size);
    return response.data;
  } catch (error) {
    console.error('Error fetching paginated items:', error);
    throw error;
  }
}

// Existing function to fetch details for a single item
export async function fetchItem(itemId: string | number): Promise<ItemDetailsType> {
  try {
    // NOTE: This endpoint likely needs updating for your actual API
    // Using relative path and assuming endpoint like /items/{itemId} or similar
    const response = await api.get<ItemDetailsType>(`/items/details/${itemId}`)
    console.log(response.data, 'here is data for item', itemId) // Log fetched data
    return response.data
  } catch (error) {
    console.error(`Error fetching item with ID ${itemId}:`, error)
    throw error
  }
}

export async function fetchPreviewItemsByCategoryId(
  categoryId: string,
): Promise<ItemPreviewType[]> {
  try {
    const response = await api.get<ItemPreviewType[]>(`/items/category/${categoryId}`)
    return response.data
  } catch (error) {
    console.error(`Error fetching items for category ID ${categoryId}:`, error)
    throw error
  }
}

/**
 * Fetches items based on category distribution probabilities.
 *
 * @param recommendation - An object containing category probability distribution
 * @param limit - Optional maximum number of items to return (default: 10)
 * @returns A Promise resolving to an array of items matching the distribution
 */
export async function fetchItemsByDistribution(
  recommendation: CategoryRecommendation,
  limit: number = 5,
): Promise<ItemPreviewType[]> {
  try {
    const response = await api.post<ItemPreviewType[]>('/items/view/recommended_items', {
      distribution: recommendation.distribution,
      limit,
    })

    return response.data
  } catch (error) {
    console.error('Error fetching items by distribution:', error)
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
    // Send the item ID to the backend and capture the response
    const response = await api.post(`/items/view/post/${itemId}`);
    console.log(`View recorded for item ${itemId}`);

    // Return the status code from the response
    return { status: response.status };
  } catch (error) {
    // Log the error but still return something to avoid disrupting UI flow
    console.error(`Failed to record view for item ${itemId}:`, error);

    // Generic error code if we couldn't get a proper status
    return { status: 500 };
  }
}

/**
 * Fetches all favorite items for the currently logged-in user.
 * @returns Promise that resolves to an array of favorite items
 */
export async function fetchFavoriteItems(): Promise<ItemPreviewType[]> {
  console.log("on way to fetch fav items");
  const { data: favorites } = await api.get<ItemFavoritesType[]>('/favorite');
  console.log("items fetched fav")

  const fullItems: ItemPreviewType[] = [];
  for (const fav of favorites) {
    try {
      console.log(fav);
      console.log("hello:",fav.itemId);
      const { data: item } = await api.get<ItemPreviewType>(`/items/details/${fav.itemId}`);
      fullItems.push(item);
    } catch (error) {
      console.error(`Error fetching item details for ID ${fav.itemId}:`, error);
    }
  }

  return fullItems;
}

/**
 * Fetches items listed by the currently authenticated user.
 * Requires the user to be logged in.
 * @returns A Promise resolving to an array of the user's items.
 */
export async function fetchMyItems(): Promise<ItemPreviewType[]> { // <-- Add this function
  try {
    const response = await api.get<ItemPreviewType[]>('/items/my');
    return response.data;
  } catch (error) {
    console.error('Error fetching logged-in user\'s items:', error);
    throw error; // Re-throw the error to be caught by the component
  }
}
