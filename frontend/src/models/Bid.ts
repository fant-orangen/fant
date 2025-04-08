/**
 * Defines the possible statuses for a modals, matching the backend ENUM.
 */
export type BidStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED';

/**
 * Represents the data to be sent when creating or updating a bid.
 * Matches the structure of the server-side BidCreateDto.
 */
export interface BidPayload {
  /**
   * The ID of the item for which the bid is placed.
   */
  itemId: string | number;

  /**
   * The monetary amount offered for the item.
   */
  amount: number;

  /**
   * Optional message from the bidder to the seller.
   */
  comment?: string;
}

/**
 * Represents the data payload for updating an existing bid.
 * Matches the structure expected by the updateMyBid service.
 */
export interface BidUpdatePayload {
  itemId: string | number;
  amount?: number;
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

export interface PaginatedBidResponse {
  content: BidResponseType[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}


