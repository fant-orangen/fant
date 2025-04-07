package stud.ntnu.backend.data.bid;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.backend.model.enums.BidStatus;

import java.math.BigDecimal;

/**
 * <h2>BidCreateDto</h2>
 * <p>Data transfer object for creating a new bid.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidCreateDto {

    /**
     * <h3>Item ID</h3>
     * <p>The ID of the item for which the bid is placed.</p>
     */
    @NotNull(message = "Item ID is required")
    @Positive(message = "Item ID must be positive")
    private Long itemId;

    /**
     * <h3>Amount</h3>
     * <p>The monetary amount offered for the item.</p>
     */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    /**
     * <h3>Comment</h3>
     * <p>Optional message from the bidder to the seller.</p>
     */
    private String comment;

    /**
     * <h3>Status</h3>
     * <p>The status of the bid (default: PENDING).</p>
     */
    private BidStatus status = BidStatus.PENDING;
}