import api from '@/services/api/axiosInstance';
import type { BidCreatePayload, BidResponseType } from '@/models/Bid';

/**
 * Places a new bid on a specific item.
 * Corresponds to POST /api/items/{itemId}/bids
 *
 * @param itemId - The ID of the item to bid on.
 * @param bidData - The bid details (amount and optional comment).
 * @returns A promise resolving to the details of the created bid.
 */
export async function placeBidOnItem(itemId: string | number, bidData: BidCreatePayload): Promise<BidResponseType> {
  try {
    const response = await api.post<BidResponseType>(`/items/${itemId}/bids`, bidData);
    console.log(`Bid placed successfully for item ${itemId}:`, response.data);
    return response.data;
  } catch (error) {
    console.error(`Error placing bid on item ${itemId}:`, error);
    throw error;
  }
}

/**
 * Fetches all bids for a specific item.
 * Used by the seller on the 'Manage Listing' page.
 * Corresponds to GET /api/items/{itemId}/bids
 *
 * @param itemId - The ID of the item whose bids are to be fetched.
 * @returns A promise resolving to an array of bids for the item.
 */
export async function fetchBidsForItem(itemId: string | number): Promise<BidResponseType[]> {
  try {
    const response = await api.get<BidResponseType[]>(`/items/${itemId}/bids`);
    console.log(`Fetched ${response.data.length} bids for item ${itemId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching bids for item ${itemId}:`, error);
    throw error;
  }
}

/**
 * Marks a specific bid as 'ACCEPTED'.
 * Used by the seller on the 'Manage Listing' page.
 * The backend should handle associated logic (e.g., updating item status, rejecting other bids).
 *
 * @param bidId - The ID of the bid to accept.
 * @returns A promise resolving to the updated bid details (with status 'ACCEPTED').
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
 * Marks a specific bid as 'REJECTED'.
 * Used by the seller on the 'Manage Listing' page.
 * Assumes a backend endpoint like PUT /api/bids/{bidId}/reject exists.
 *
 * @param bidId - The ID of the bid to reject.
 * @returns A promise resolving to the updated bid details (with status 'REJECTED').
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
