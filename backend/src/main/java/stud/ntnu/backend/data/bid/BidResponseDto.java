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

  private Long id;
  private Long itemId;
  private Long bidderId;
  private String bidderUsername;
  private BigDecimal amount;
  private String comment;
  private BidStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}