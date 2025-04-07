package stud.ntnu.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import stud.ntnu.backend.model.enums.BidStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <h2>Bid</h2>
 * <p>Entity representing a purchase offer made on an item.</p>
 */
@Entity
@Table(name = "bids", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"item_id", "bidder_id"})})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bid {

  /**
   * <h3>ID</h3>
   * <p>Unique identifier for the bid.</p>
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * <h3>Item</h3>
   * <p>The item for which the bid is placed.</p>
   */
  @ManyToOne(optional = false)
  @JoinColumn(name = "item_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Item item;

  /**
   * <h3>Bidder</h3>
   * <p>The user who made the bid.</p>
   */
  @ManyToOne(optional = false)
  @JoinColumn(name = "bidder_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User bidder;

  /**
   * <h3>Amount</h3>
   * <p>The monetary amount offered for the item.</p>
   */
  @Column(nullable = false)
  private BigDecimal amount;

  /**
   * <h3>Comment</h3>
   * <p>Optional message from the bidder to the seller.</p>
   */
  @Column(columnDefinition = "TEXT")
  private String comment;

  /**
   * <h3>Status</h3>
   * <p>The current status of the bid.</p>
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BidStatus status = BidStatus.PENDING;

  /**
   * <h3>Created At</h3>
   * <p>Timestamp when the bid was created.</p>
   */
  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * <h3>Updated At</h3>
   * <p>Timestamp when the bid was last updated.</p>
   */
  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime updatedAt;
}