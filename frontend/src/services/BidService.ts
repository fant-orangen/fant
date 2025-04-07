import api from '@/services/api/axiosInstance';
import type { BidPayload, BidResponseType } from '@/models/Bid';

/**
 * Places a new bid for an item.
 * The authenticated user is identified through their JWT token.
 *
 * @param bid - The bid information including itemId, amount, and optional comment
 * @returns A promise that resolves when the bid is successfully placed
 * @throws Error if the request fails
 */
export async function placeBid(bid: BidPayload): Promise<{ status: number }> {
  try {
    const response = await api.post('/orders/bid', bid);
    console.log(`Bid successfully placed for item ${bid.itemId}`);

    return {status: response.status }
  } catch (error) {
    console.error('Error placing bid:', error);
    throw error;
  }
}

/**
 * Fetches all bids placed by the currently authenticated user.
 * Uses the authentication token to identify the user.
 *
 * @returns A promise resolving to an array of the user's bids
 * @throws Error if the request fails
 */
export async function fetchUserBids(): Promise<BidResponseType[]> {
  try {
    const response = await api.get<BidResponseType[]>('/orders/bids');
    console.log('User bids fetched:', response.data.length);
    return response.data;
  } catch (error) {
    console.error('Error fetching user bids:', error);
    throw error
  }
}

/**
 * Accepts a bid for an item
 * @param bidderId The ID of the user who placed the bid
 * @param itemId The ID of the item the bid was placed on
 * @returns The response with status code
 */
export async function acceptBid(bidderId: string | number, itemId: string | number): Promise<{ status: number }> {
  try {
    const response = await api.post('/orders/accept', { bidderId, itemId });
    return { status: response.status }
  } catch (error) {
    console.error('Error accepting bid:', error)
    throw error
  }
}

/**
 * Rejects a bid for an item
 * @param bidderId The ID of the user who placed the bid
 * @param itemId The ID of the item the bid was placed on
 * @returns The response with status code
 */
export async function rejectBid(bidderId: string | number, itemId: string | number): Promise<{ status: number }> {
  try {
    const response = await api.post('/orders/reject', { bidderId, itemId });
    return { status: response.status }
  } catch (error) {
    console.error('Error rejecting bid:', error)
    throw error
  }
}

/**
 * Deletes a bid for the specified item
 * @param id - The ID of the item to delete the bid for
 * @returns Promise with response status
 */
export async function deleteBid(id: string | number): Promise<{status: number}> {
  try {
    const response = await api.delete(`/orders/delete/${id}`)
    return { status: response.status }
  } catch (error) {
    console.error('Error deleting bid:', error)
    throw error
  }
}

/**
 * Updates an existing bid for an item
 * @param bid - The updated bid information
 * @returns The response with status code
 */
export async function updateBid(bid: BidPayload): Promise<{status: number}> {
  try {
    const response = await api.put('/orders/update', bid)
    return { status: response.status }
  } catch (error) {
    console.error('Error updating bid:', error)
    throw error
  }
}

