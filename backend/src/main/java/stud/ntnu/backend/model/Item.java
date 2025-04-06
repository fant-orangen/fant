package stud.ntnu.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import stud.ntnu.backend.model.enums.ItemStatus;

/**
 * <h2>Item</h2>
 * <p>Entity representing a product/item for sale in the marketplace.</p>
 */
@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

  /**
   * <h3>ID</h3>
   * <p>Unique identifier for the item.</p>
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * <h3>Seller</h3>
   * <p>The user who is selling this item (required).</p>
   */
  @ManyToOne(optional = false)
  @JoinColumn(name = "seller_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User seller;

  /**
   * <h3>Category</h3>
   * <p>The item's category (required).</p>
   */
  @ManyToOne(optional = false)
  @JoinColumn(name = "category_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Category category;

  /**
   * <h3>Brief Description</h3>
   * <p>Short summary of the item (required).</p>
   */
  @Column(nullable = false)
  private String briefDescription;

  /**
   * <h3>Full Description</h3>
   * <p>Detailed information about the item.</p>
   */
  @Column(columnDefinition = "TEXT")
  private String fullDescription;

  /**
   * <h3>Price</h3>
   * <p>The item's selling price (required).</p>
   */
  @Column(nullable = false)
  private BigDecimal price;

  /**
   * <h3>Latitude</h3>
   * <p>Geographic coordinate for item location.</p>
   */
  private Double latitude;

  /**
   * <h3>Longitude</h3>
   * <p>Geographic coordinate for item location.</p>
   */
  private Double longitude;

  /**
   * <h3>Status</h3>
   * <p>Current availability status (default: ACTIVE).</p>
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ItemStatus status = ItemStatus.ACTIVE;

  /**
   * <h3>Published At</h3>
   * <p>Timestamp when item was listed (auto-generated).</p>
   */
  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime publishedAt;

  /**
   * <h3>Updated At</h3>
   * <p>Timestamp when item was last modified (auto-updated).</p>
   */
  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  /**
   * <h3>Images</h3>
   * <p>Collection of images associated with this item.</p>
   */
  @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ItemImage> images;
}