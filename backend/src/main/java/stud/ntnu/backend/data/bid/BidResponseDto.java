package stud.ntnu.backend.data.bid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.backend.model.enums.BidStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <h2>BidResponseDto</h2>
 * <p>Data transfer object for sending bid details (including bidder info) to the frontend.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidResponseDto {

  /**
   * <h3>ID</h3>
   * <p>The unique identifier of the bid.</p>
   */
  private Long id;

  /**
   * <h3>Item ID</h3>
   * <p>The ID of the item for which the bid is placed.</p>
   */
  private Long itemId;

  /**
   * <h3>Bidder ID</h3>
   * <p>The unique identifier of the user who placed the bid.</p>
   */
  private Long bidderId;

  /**
   * <h3>Bidder Username</h3>
   * <p>The username of the user who placed the bid.</p>
   */
  private String bidderUsername;

  /**
   * <h3>Amount</h3>
   * <p>The monetary amount offered for the item.</p>
   */
  private BigDecimal amount;

  /**
   * <h3>Comment</h3>
   * <p>Optional message from the bidder to the seller.</p>
   */
  private String comment;

  /**
   * <h3>Status</h3>
   * <p>The current status of the bid (PENDING, ACCEPTED, REJECTED, etc.).</p>
   */
  private BidStatus status;

  /**
   * <h3>Created At</h3>
   * <p>The date and time when the bid was initially created.</p>
   */
  private LocalDateTime createdAt;

  /**
   * <h3>Updated At</h3>
   * <p>The date and time when the bid was last modified.</p>
   */
  private LocalDateTime updatedAt;
}