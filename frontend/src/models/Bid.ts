/**
 * Represents the data sent from the frontend to the backend
 * when creating a new bid.
 */
export interface BidCreatePayload {
  /**
   * The monetary amount of the bid.
   */
  bidAmount: number;

  /**
   * An optional comment accompanying the bid.
   */
  comment?: string;
}

/**
 * Represents the data received from the backend after a bid
 * has been successfully created, confirming the bid details.
 */
export interface BidResponseType {
  /**
   * The unique identifier for the created bid record.
   */
  id: number | string;

  /**
   * The identifier of the item the bid was placed on.
   */
  itemId: number | string;

  /**
   * The identifier of the user who placed the bid.
   */
  userId: number | string;

  /**
   * The display name of the user who placed the bid.
   * (Useful if you plan to display bid history later)
   */
  username: string;

  /**
   * The monetary amount of the bid that was placed.
   */
  bidAmount: number;

  /**
   * The comment submitted with the bid, if any.
   */
  comment?: string;

  /**
   * The timestamp indicating when the bid was placed.
   */
  bidTime: string;
}
