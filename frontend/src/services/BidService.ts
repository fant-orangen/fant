/**
 * Registers a new user with the system.
 *
 * Makes a POST request to the registration endpoint with the user's registration data
 * and returns the server response.
 *
 * @param {RegistrationData} userData - The user's registration information
 * @returns {Promise<import('axios').AxiosResponse>} Promise resolving to the server response
 * @throws {Error} When registration fails or network issues occur
 */
import api from '@/services/api/axiosInstance'
import type { BidPayload, BidResponseType, PaginatedBidResponse } from '@/models/Bid' // Keep BidResponseType

/**
 * Places a new bid for an item.
 * The authenticated user is identified through their JWT token.
 *
 * @param bid - The bid information including itemId, amount, and optional comment
 * @returns A promise that resolves with the response status object
 * @throws Error if the request fails
 */
export async function placeBid(bid: BidPayload): Promise<{ status: number }> {
  try {
    const response = await api.post('/orders/bid', bid)
    return { status: response.status }
  } catch (error: any) {
    const status = error.response?.status || 500
    throw new Error(`Failed to place bid (Status: ${status}): ${error.message}`)
  }
}

/**
 * Fetches a paginated list of the authenticated user's bids.
 *
 * @param {number} page - Page index (0-based)
 * @param {number} size - Number of bids per page
 * @param {string} [sort] - Optional sorting string, e.g. 'createdAt,desc'
 * @returns {Promise<PaginatedBidResponse>} A paginated response of bids
 * @throws {Error} If the fetch operation fails
 */
export async function fetchPagedUserBids(
  page: number,
  size: number,
  sort?: string,
): Promise<PaginatedBidResponse> {
  try {
    const params = new URLSearchParams({
      page: String(page),
      size: String(size),
    })

    if (sort) {
      params.append('sort', sort)
    }

    const response = await api.get<PaginatedBidResponse>(`/orders/bids?${params.toString()}`)
    return response.data
  } catch (error: any) {
    throw new Error(error.message || 'Failed to fetch paginated user bids.')
  }
}

/**
 * Fetches all bids placed on a specific item.
 * Requires the user to be authenticated and likely the owner of the item.
 *
 * @param {string|number} itemId - The ID of the item to fetch bids for
 * @returns {Promise<BidResponseType[]>} A promise resolving to an array of bids for the item
 * @throws {Error} If the request fails (e.g., 403 Forbidden if not owner, 404 if item not found)
 */
export async function fetchBidsForItem(itemId: string | number): Promise<BidResponseType[]> {
  try {
    // This endpoint already returns BidResponseDto from backend (based on previous fixes)
    // So no transformation needed here, assuming backend fix was applied OR
    // this endpoint was designed to return the DTO from the start.
    const response = await api.get<BidResponseType[]>(`/orders/item/${itemId}`)
    return response.data
  } catch (error: any) {
    const status = error.response?.status || 500
    let message = `Failed to fetch bids (Status: ${status}): ${error.message}`
    if (status === 403) {
      message = 'You are not authorized to view bids for this item.'
    } else if (status === 404) {
      message = 'Item or bids not found.' // Adjusted message slightly
    }
    throw new Error(message)
  }
}

/**
 * Accepts a bid for an item. Requires seller authentication.
 *
 * @param {string|number} bidderId - The ID of the user who placed the bid
 * @param {string|number} itemId - The ID of the item the bid was placed on
 * @returns {Promise<{status: number}>} Promise resolving with the response status object
 * @throws {Error} If the request fails
 */
export async function acceptBid(
  bidderId: string | number,
  itemId: string | number,
): Promise<{
  status: number
}> {
  try {
    const response = await api.post('/orders/accept', null, {
      params: { itemId, bidderId },
    })
    return { status: response.status }
  } catch (error: any) {
    const status = error.response?.status || 500
    throw new Error(`Failed to accept bid (Status: ${status}): ${error.message}`)
  }
}

/**
 * Rejects a bid for an item. Requires seller authentication.
 *
 * @param {string|number} bidderId - The ID of the user who placed the bid
 * @param {string|number} itemId - The ID of the item the bid was placed on
 * @returns {Promise<{status: number}>} Promise resolving with the response status object
 * @throws {Error} If the request fails
 */
export async function rejectBid(
  bidderId: string | number,
  itemId: string | number,
): Promise<{
  status: number
}> {
  try {
    const response = await api.post('/orders/reject', null, {
      params: { itemId, bidderId },
    })
    return { status: response.status }
  } catch (error: any) {
    const status = error.response?.status || 500
    throw new Error(`Failed to reject bid (Status: ${status}): ${error.message}`)
  }
}

/**
 * Deletes a bid placed by the currently authenticated user for the specified item.
 *
 * @param {string|number} itemId - The ID of the item the bid was placed on
 * @returns {Promise<{status: number}>} Promise resolving with response status object
 * @throws {Error} If the request fails
 */
export async function deleteMyBid(itemId: string | number): Promise<{ status: number }> {
  try {
    const response = await api.delete(`/orders/delete/${itemId}`)
    return { status: response.status }
  } catch (error: any) {
    const status = error.response?.status || 500
    throw new Error(`Failed to delete bid (Status: ${status}): ${error.message}`)
  }
}


/**
 * Payload for updating an existing bid.
 * @interface BidUpdatePayload
 */
interface BidUpdatePayload {
  itemId: string | number
  amount?: number // Amount is optional for update
  comment?: string // Comment is optional
}

/**
 * Updates an existing bid placed by the currently authenticated user.
 *
 * @param {BidUpdatePayload} bidUpdatePayload - Contains itemId and the fields to update
 * @returns {Promise<{status: number}>} Promise resolving with the response status object
 * @throws {Error} If the request fails or bid is no longer in PENDING status
 */
export async function updateMyBid(bidUpdatePayload: BidUpdatePayload): Promise<{ status: number }> {
  try {
    // Use the itemId in the URL path instead of the request body
    const response = await api.put(`/orders/update/${bidUpdatePayload.itemId}`, {
      amount: bidUpdatePayload.amount,
      comment: bidUpdatePayload.comment
    })
    return { status: response.status }
  } catch (error: any) {
    const status = error.response?.status || 500
    let message = `Failed to update bid (Status: ${status}): ${error.message}`
    if (status === 400 && error.response?.data?.includes('not in PENDING status')) {
      message = 'Cannot update this bid as it is no longer pending.'
    }
    throw new Error(message)
  }
}
