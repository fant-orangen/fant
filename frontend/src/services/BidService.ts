import api from '@/services/api/axiosInstance';
import type { BidCreatePayload, BidResponseType } from '@/models/Bid';

/**
 * Places a new bid for an item.
 * The authenticated user is identified through their JWT token.
 *
 * @param bid - The bid information including itemId, amount, and optional comment
 * @returns A promise that resolves when the bid is successfully placed
 * @throws Error if the request fails
 */
export async function placeBid(bid: BidCreatePayload): Promise<{ status: number }> {
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
 * Marks a specific modals as 'ACCEPTED'.
 * Used by the seller on the 'Manage Listing' page.
 * The backend should handle associated logic (e.g., updating item status, rejecting other bids).
 *
 * @param bidId - The ID of the modals to accept.
 * @returns A promise resolving to the updated modals details (with status 'ACCEPTED').
 */
export async function acceptBid(bidId: string | number): Promise<BidResponseType> {
  try {
    const response = await api.put<BidResponseType>(`/bids/${bidId}/accept`);
    console.log(`Bid ${bidId} accepted:`, response.data);
    return response.data;
  } catch (error) {
    console.error(`Error accepting bid ${bidId}:`, error);
    throw error;
  }
}

/**
 * Marks a specific modals as 'REJECTED'.
 * Used by the seller on the 'Manage Listing' page.
 * Assumes a backend endpoint like PUT /api/bids/{bidId}/reject exists.
 *
 * @param bidId - The ID of the modals to reject.
 * @returns A promise resolving to the updated modals details (with status 'REJECTED').
 */
export async function rejectBid(bidId: string | number): Promise<BidResponseType> {
  try {
    const response = await api.put<BidResponseType>(`/bids/${bidId}/reject`);
    console.log(`Bid ${bidId} rejected:`, response.data);
    return response.data;
  } catch (error) {
    console.error(`Error rejecting bid ${bidId}:`, error);
    throw error;
  }
}
