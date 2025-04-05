package stud.ntnu.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * <h2>Favorite</h2>
 * <p>Entity representing a user's favorited items with timestamp.</p>
 */
@Entity
@Table(name = "favorites", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "item_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

  /**
   * <h3>ID</h3>
   * <p>Unique identifier for the favorite record.</p>
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * <h3>User</h3>
   * <p>The user who favorited the item (required).</p>
   */
  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  /**
   * <h3>Item</h3>
   * <p>The favorited item (required).</p>
   */
  @ManyToOne(optional = false)
  @JoinColumn(name = "item_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Item item;

  /**
   * <h3>Created At</h3>
   * <p>Timestamp when the item was favorited (auto-generated).</p>
   */
  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;
}