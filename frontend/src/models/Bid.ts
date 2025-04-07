/**
 * Defines the possible statuses for a modals, matching the backend ENUM.
 */
export type BidStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED';

/**
 * Represents the data sent from the frontend to the backend
 * when creating a new modals (payload for the POST request body).
 * Note: itemId is typically passed in the URL path, and bidderId
 * is determined from the authenticated user on the backend.
 */
export interface BidCreatePayload {
  /**
   * The monetary amount of the modals. Matches 'amount' in the database.
   */
  amount: number;

  /**
   * An optional comment accompanying the modals.
   */
  comment?: string;
}

/**
 * Represents the detailed data received from the backend for a modals,
 * often used when fetching bids or after creating one.
 * Matches the updated database schema.
 */
export interface BidResponseType {
  /**
   * The unique identifier for the modals record.
   */
  id: number | string;

  /**
   * The identifier of the item the modals was placed on.
   */
  itemId: number | string;

  /**
   * The identifier of the user who placed the modals. Matches 'bidder_id' in DB.
   */
  bidderId: number | string;

  /**
   * The display name of the user who placed the modals.
   * (Assumes backend includes this for convenience, e.g., via a JOIN or lookup)
   */
  bidderUsername: string;

  /**
   * The monetary amount of the modals. Matches 'amount' in the database.
   */
  amount: number;

  /**
   * The comment submitted with the modals, if any.
   */
  comment?: string;

  /**
   * The current status of the modals (PENDING, ACCEPTED, REJECTED).
   */
  status: BidStatus;

  /**
   * The timestamp indicating when the modals was initially placed. Matches 'created_at'.
   * Often received as an ISO 8601 string from the backend.
   */
  createdAt: string;

  /**
   * The timestamp indicating when the modals was last updated
   * Often received as an ISO 8601 string from the backend.
   */
  updatedAt: string;
}
