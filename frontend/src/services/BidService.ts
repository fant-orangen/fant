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
 * Accepts a bid for an item
 * @param bidderId The ID of the user who placed the bid
 * @param itemId The ID of the item the bid was placed on
 * @returns The response with status code
 */
export async function acceptBid(bidderId: string | number, itemId: string | number): Promise<{ status: number }> {
  const response = await api.post('/orders/accept', { bidderId, itemId });
  return { status: response.status }
}

/**
 * Rejects a bid for an item
 * @param bidderId The ID of the user who placed the bid
 * @param itemId The ID of the item the bid was placed on
 * @returns The response with status code
 */
export async function rejectBid(bidderId: string | number, itemId: string | number): Promise<{ status: number }> {
  const response = await api.post('/orders/reject', { bidderId, itemId });
  return { status: response.status }
}

