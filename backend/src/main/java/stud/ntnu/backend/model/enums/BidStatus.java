package stud.ntnu.backend.model.enums;

/**
 * <h2>BidStatus</h2>
 * <p>Enumeration of possible states for a bid.</p>
 */
public enum BidStatus {
  /**
   * <h3>PENDING</h3>
   * <p>Bid has been submitted but not yet reviewed by the seller.</p>
   */
  PENDING,

  /**
   * <h3>ACCEPTED</h3>
   * <p>Bid has been accepted by the seller.</p>
   */
  ACCEPTED,

  /**
   * <h3>REJECTED</h3>
   * <p>Bid has been rejected by the seller.</p>
   */
  REJECTED,

  /**
   * <h3>WITHDRAWN</h3>
   * <p>Bid has been withdrawn by the bidder.</p>
   */
  WITHDRAWN
}