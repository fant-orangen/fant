package stud.ntnu.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "buyer_id")
  private User buyer;

  @ManyToOne(optional = false)
  @JoinColumn(name = "seller_id")
  private User seller;

  @ManyToOne(optional = false)
  @JoinColumn(name = "item_id")
  private Item item;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus orderStatus;

  @Column(nullable = false)
  private BigDecimal price;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  void onCreate() {
    createdAt = updatedAt = LocalDateTime.now();
    orderStatus = OrderStatus.PENDING;
  }

  @PreUpdate
  void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}

