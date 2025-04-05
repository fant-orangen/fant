package stud.ntnu.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * <h2>ItemView</h2>
 * <p>Entity tracking when users view marketplace items.</p>
 */
@Entity
@Table(name = "item_views")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemView {

    /**
     * <h3>ID</h3>
     * <p>Unique identifier for the view record.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * <h3>User</h3>
     * <p>The user who viewed the item (required).</p>
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * <h3>Item</h3>
     * <p>The item that was viewed (required).</p>
     */
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /**
     * <h3>Viewed At</h3>
     * <p>Timestamp when the view occurred (defaults to current time).</p>
     */
    @Column(name = "viewed_at")
    private LocalDateTime viewedAt = LocalDateTime.now();
}