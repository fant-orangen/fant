package stud.ntnu.backend.model.enums;

/**
 * <h2>ItemStatus</h2>
 * <p>Enumeration representing the different states an {@code Item} can be in.</p>
 * <p>These statuses indicate the current availability and lifecycle stage of a marketplace item.</p>
 */
public enum ItemStatus {

  /**
   * <h3>ACTIVE</h3>
   * <p>Indicates that the item is currently listed and available for purchase or bidding.</p>
   */
  ACTIVE,

  /**
   * <h3>ARCHIVED</h3>
   * <p>Indicates that the item listing has been archived and is no longer actively displayed to
   * users.</p>
   */
  ARCHIVED,

  /**
   * <h3>SOLD</h3>
   * <p>Indicates that the item has been sold and is no longer available.</p>
   */
  SOLD,

  /**
   * <h3>RESERVED</h3>
   * <p>Indicates that the item has been temporarily reserved for a user and is not currently
   * available to other buyers.</p>
   */
  RESERVED
}