import api from '@/services/api/axiosInstance' // [cite: uploaded:frontend 6/frontend/src/services/api/axiosInstance.ts]
import type {
  ItemPreviewType,
  ItemDetailsType,
  CreateItemType,
  ItemFavoritesType,
  PaginatedItemPreviewResponse,
} from '@/models/Item' // [cite: uploaded:frontend 6/frontend/src/models/Item.ts]
import type { CategoryRecommendation } from '@/models/Recommendation' // [cite: uploaded:frontend 6/frontend/src/models/Recommendation.ts]

// Define an interface for the expected paginated response (needed by fetchFavoriteItems)
export interface PaginatedItemsResponse {
  items: ItemPreviewType[]
  totalItems: number
}

// Define a type for common pagination and sorting parameters
type PageableParams = {
  page: number;
  size: number;
  sort?: string;
};


export async function createItem(item: CreateItemType): Promise<number> {
  console.log('before post', item)
  const response = await api.post<number>('/items', item)
  console.log('after post', response.data)
  return response.data
}

export async function updateItem(id: number, item: CreateItemType): Promise<CreateItemType> {
  console.log('before put', id, item);
  const response = await api.put<CreateItemType>(`/items/${id}`, item);
  console.log('after put', response.data);
  return response.data;
}

export async function deleteItem(id: number): Promise<void> {
  console.log('before delete', id);
  await api.delete(`/items/${id}`);
  console.log('after delete');
}

// Existing function to fetch all preview items (potentially needs pagination update too)
export async function fetchPreviewItems(): Promise<PaginatedItemPreviewResponse> {
  try {
    // Assuming the endpoint /items/all implicitly returns the first page or a default set
    const response = await api.get<PaginatedItemPreviewResponse>('/items/all')
    console.log('url: ' + api.defaults.baseURL + '/items/all')
    console.log('Data:' + response.data.totalElements)
    console.log('Items per page' + response.data.size)
    return response.data
  } catch (error) {
    console.error('Error fetching items:', error)
    throw error
  }
}

// Define an interface for the search parameters matching the backend ItemSearchDto
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
    console.log("Searching items with params:", params);
    // Filter out null/undefined values before sending
    // Initialize accumulator with Partial<ItemSearchParams> for better type safety
    const filteredParams = Object.entries(params).reduce((acc, [key, value]) => {
      if (value !== null && value !== undefined && value !== '') { // Also filter empty strings if necessary
        // TypeScript now knows `key` is a string, and `acc` is partially like ItemSearchParams
        // Using keyof typeof ensures the key exists on the type
        acc[key as keyof ItemSearchParams] = value as any; // Cast value to 'any' is okay here after check
      }
      return acc;
    }, {} as Partial<ItemSearchParams>); // Use Partial<ItemSearchParams> as the initial type

    const response = await api.get<PaginatedItemPreviewResponse>('/items/search', {
      params: filteredParams
    });
    console.log('Search response:', response.data);
    return response.data;
  } catch (error) {
    console.error('Error searching items:', error);
    throw error;
  }
}

/**
 * Fetches a paginated set of item previews from the backend.
 *
 * @param page - The page index to fetch (0-based)
 * @param size - The number of items per page
 * @param sort
 * @returns A promise resolving to a paginated response of item previews
 * @throws Error if the request fails
 */
export async function fetchPagedPreviewItems(
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

    // Convert params object to URLSearchParams for GET request
    const queryParams = new URLSearchParams();
    queryParams.append('page', String(params.page));
    queryParams.append('size', String(params.size));
    if (params.sort) {
      queryParams.append('sort', params.sort);
    }


    // Use the axios instance's params option for cleaner handling
    const response = await api.get<PaginatedItemPreviewResponse>('/items/page', { params });

    console.log('Requested page:', page, 'size:', size, 'sort:', sort)
    return response.data
  } catch (error) {
    console.error('Error fetching paginated items:', error)
    throw error
  }
}

/**
 * Fetches a paginated set of item previews for a specific category, with optional sorting.
 *
 * @param categoryId - The category to filter by
 * @param page - Page index (0-based)
 * @param size - Number of items per page
 * @param sort - Optional sort string, e.g. "price,desc"
 * @returns A promise resolving to a paginated response of item previews
 */
export async function fetchPagedPreviewItemsByCategory(
  categoryId: string,
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

    const response = await api.get<PaginatedItemPreviewResponse>(
      `/items/category/${categoryId}/page`, { params } // Pass params object to axios
    )

    return response.data
  } catch (error) {
    console.error(`Error fetching paginated items for category ${categoryId}:`, error)
    throw error
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

export async function fetchPreviewItemsByCategoryId(
  categoryId: string,
): Promise<ItemPreviewType[]> {
  try {
    // Note: This fetches *all* items for a category, might need pagination if categories are large
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
 * @param limit - Optional maximum number of items to return (default: 5)
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
    const response = await api.post(`/items/view/post/${itemId}`)
    console.log(`View recorded for item ${itemId}`)
    return { status: response.status }
  } catch (error) {
    console.error(`Failed to record view for item ${itemId}:`, error)
    // Consider how to handle errors, maybe return a specific status or rethrow
    // depending on how the caller should react.
    // For now, returning 500 is a placeholder.
    // You might want to check error type (e.g., network vs server error)
    return { status: 500 }
  }
}

/**
 * Fetches all favorite items for the currently logged-in user.
 * @returns Promise that resolves to an array of favorite items
 */
export async function fetchFavoriteItems(): Promise<ItemPreviewType[]> {
  console.log('on way to fetch fav items')
  const { data: favorites } = await api.get<ItemFavoritesType[]>('/favorite')
  console.log('items fetched fav')

  // This fetches details for each favorite individually.
  // Consider if the backend could provide a single endpoint
  // to fetch favorite items with their details directly for efficiency.
  const fullItems: ItemPreviewType[] = []
  for (const fav of favorites) {
    try {
      console.log(fav)
      console.log('hello:', fav.itemId)
      // Assuming /items/details/{id} returns ItemPreviewType compatible data
      // If it returns ItemDetailsType, you might need to map it.
      const { data: item } = await api.get<ItemPreviewType>(`/items/details/${fav.itemId}`)
      fullItems.push(item)
    } catch (error) {
      console.error(`Error fetching item details for ID ${fav.itemId}:`, error)
      // Decide how to handle errors for individual items (e.g., skip, show placeholder)
    }
  }

  return fullItems
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
    // Use the PageableParams type
    const params: PageableParams = { page, size };
    if (sort) params.sort = sort;

    console.log('Fetching paginated favorite items with:', params);
    // Pass params object directly to axios
    const { data } = await api.get<PaginatedItemPreviewResponse>('/favorite', { params });

    console.log(`Workspaceed ${data.content.length} favorite items`);
    return data;
  } catch (error) {
    console.error('Error fetching paginated favorite items:', error);
    throw error;
  }
}


/**
 * Fetches items listed by the currently authenticated user.
 * Requires the user to be logged in. TODO: Remove this function when we know it's not needed (replaced by paged version)
 * @returns A Promise resolving to an array of the user's items.
 */
export async function fetchMyItems(): Promise<ItemPreviewType[]> {
  try {
    const response = await api.get<ItemPreviewType[]>('/items/my')
    return response.data
  } catch (error) {
    console.error("Error fetching logged-in user's items:", error)
    throw error // Re-throw the error to be caught by the component
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

    console.log(`Workspaceed my items - Page: ${page}, Size: ${size}, Sort: ${sort || 'unsorted'}`)
    return response.data
  } catch (error) {
    console.error('Error fetching paginated items for current user:', error)
    throw error
  }
}
