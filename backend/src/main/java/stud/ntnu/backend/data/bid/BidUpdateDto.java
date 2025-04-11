package stud.ntnu.backend.data.bid;

import jakarta.validation.constraints.NotBlank;
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
   * <h3>Amount</h3>
   * <p>The updated monetary amount offered for the item (optional).</p>
   */
  @NotNull(message = "Amount is required")
  @Positive(message = "Amount must be positive")
  private BigDecimal amount;

  /**
   * <h3>Comment</h3>
   * <p>Updated message from the bidder to the seller (optional).</p>
   */
  private String comment;
}