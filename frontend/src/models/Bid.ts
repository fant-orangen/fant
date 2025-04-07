/**
 * Defines the possible statuses for a bid, matching the backend ENUM.
 */
export type BidStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED';

/**
 * Represents the data sent from the frontend to the backend
 * when creating a new bid (payload for the POST request body).
 * Note: itemId is typically passed in the URL path, and bidderId
 * is determined from the authenticated user on the backend.
 */
export interface BidCreatePayload {
  /**
   * The monetary amount of the bid. Matches 'amount' in the database.
   */
  amount: number;

  /**
   * An optional comment accompanying the bid.
   */
  comment?: string;
}

/**
 * Represents the detailed data received from the backend for a bid,
 * often used when fetching bids or after creating one.
 * Matches the updated database schema.
 */
export interface BidResponseType {
  /**
   * The unique identifier for the bid record.
   */
  id: number | string;

  /**
   * The identifier of the item the bid was placed on.
   */
  itemId: number | string;

  /**
   * The identifier of the user who placed the bid. Matches 'bidder_id' in DB.
   */
  bidderId: number | string;

  /**
   * The display name of the user who placed the bid.
   * (Assumes backend includes this for convenience, e.g., via a JOIN or lookup)
   */
  bidderUsername: string;

  /**
   * The monetary amount of the bid. Matches 'amount' in the database.
   */
  amount: number;

  /**
   * The comment submitted with the bid, if any.
   */
  comment?: string;

  /**
   * The current status of the bid (PENDING, ACCEPTED, REJECTED).
   */
  status: BidStatus;

  /**
   * The timestamp indicating when the bid was initially placed. Matches 'created_at'.
   * Often received as an ISO 8601 string from the backend.
   */
  createdAt: string;

  /**
   * The timestamp indicating when the bid was last updated
   * Often received as an ISO 8601 string from the backend.
   */
  updatedAt: string;
}
