package stud.ntnu.backend.data.bid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * <h2>BidUpdateDto</h2>
 * <p>Data transfer object for updating an existing bid.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidUpdateDto {

  /**
   * <h3>Item ID</h3>
   * <p>The ID of the item for which the bid was placed.</p>
   */
  @NotNull(message = "Item ID is required")
  @Positive(message = "Item ID must be positive")
  private Long itemId;

  /**
   * <h3>Amount</h3>
   * <p>The updated monetary amount offered for the item (optional).</p>
   */
  private BigDecimal amount;

  /**
   * <h3>Comment</h3>
   * <p>Updated message from the bidder to the seller (optional).</p>
   */
  private String comment;
}