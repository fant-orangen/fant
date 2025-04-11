/**
 * @fileoverview Bid model definitions for item bidding functionality.
 * <p>This module provides types for:</p>
 * <ul>
 *   <li>Bid status management (pending, accepted, rejected)</li>
 *   <li>Data structures for bid creation and updates</li>
 *   <li>Response interfaces for API interactions</li>
 *   <li>Pagination support for listing multiple bids</li>
 * </ul>
 */

/**
 * Defines the possible statuses for a bid, matching the backend ENUM.
 * @typedef {'PENDING'|'ACCEPTED'|'REJECTED'} BidStatus
 */
export type BidStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED';

/**
 * Represents the data to be sent when creating or updating a bid.
 * Matches the structure of the server-side BidCreateDto.
 * @interface BidPayload
 */
export interface BidPayload {
  /**
   * The ID of the item for which the bid is placed.
   * @type {string|number}
   */
  itemId: string | number;

  /**
   * The monetary amount offered for the item.
   * @type {number}
   */
  amount: number;

  /**
   * Optional message from the bidder to the seller.
   * @type {string|undefined}
   */
  comment?: string;
}

/**
 * Represents the data payload for updating an existing bid.
 * Matches the structure expected by the updateMyBid service.
 * @interface BidUpdatePayload
 */
export interface BidUpdatePayload {
  /**
   * The ID of the item for which the bid was placed.
   * @type {string|number}
   */
  itemId: string | number;

  /**
   * The updated monetary amount offered for the item.
   * @type {number|undefined}
   */
  amount?: number;

  /**
   * Updated optional message from the bidder to the seller.
   * @type {string|undefined}
   */
  comment?: string;
}

/**
 * Represents the detailed data received from the backend for a bid,
 * often used when fetching bids or after creating one.
 * Matches the updated database schema.
 * @interface BidResponseType
 */
export interface BidResponseType {
  /**
   * The unique identifier for the bid record.
   * @type {number|string}
   */
  id: number | string;

  /**
   * The identifier of the item the modals was placed on.
   * @type {number|string}
   */
  itemId: number | string;

  /**
   * The identifier of the user who placed the modals. Matches 'bidder_id' in DB.
   * @type {number|string}
   */
  bidderId: number | string;

  /**
   * The display name of the user who placed the modals.
   * (Assumes backend includes this for convenience, e.g., via a JOIN or lookup)
   * @type {string}
   */
  bidderUsername: string;

  /**
   * The monetary amount of the modals. Matches 'amount' in the database.
   * @type {number}
   */
  amount: number;

  /**
   * The comment submitted with the modals, if any.
   * @type {string|undefined}
   */
  comment?: string;

  /**
   * The current status of the modals (PENDING, ACCEPTED, REJECTED).
   * @type {BidStatus}
   */
  status: BidStatus;

  /**
   * The timestamp indicating when the modals was initially placed. Matches 'created_at'.
   * Often received as an ISO 8601 string from the backend.
   * @type {string}
   */
  createdAt: string;

  /**
   * The timestamp indicating when the modals was last updated
   * Often received as an ISO 8601 string from the backend.
   * @type {string}
   */
  updatedAt: string;
}

/**
 * Represents a paginated response of bids from the API.
 * Used for list views where multiple bids are returned.
 * @interface PaginatedBidResponse
 */
export interface PaginatedBidResponse {
  /**
   * The array of bid objects for the current page.
   * @type {BidResponseType[]}
   */
  content: BidResponseType[];

  /**
   * Total number of pages available.
   * @type {number}
   */
  totalPages: number;

  /**
   * Total number of bid elements across all pages.
   * @type {number}
   */
  totalElements: number;

  /**
   * Number of elements per page.
   * @type {number}
   */
  size: number;

  /**
   * Current page number (0-based).
   * @type {number}
   */
  number: number;

  /**
   * Whether this is the first page.
   * @type {boolean}
   */
  first: boolean;

  /**
   * Whether this is the last page.
   * @type {boolean}
   */
  last: boolean;

  /**
   * Whether the current page is empty.
   * @type {boolean}
   */
  empty: boolean;
}


