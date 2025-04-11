package stud.ntnu.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * <h2>ItemImage</h2>
 * <p>Entity representing images associated with marketplace items.</p>
 */
@Entity
@Table(name = "item_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemImage {

  /**
   * <h3>ID</h3>
   * <p>Unique identifier for the image record.</p>
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * <h3>Item</h3>
   * <p>The item this image belongs to (required).</p>
   */
  @ManyToOne(optional = false)
  @JoinColumn(name = "item_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JsonBackReference
  private Item item;

  /**
   * <h3>Image URL</h3>
   * <p>Storage location/path of the image (required).</p>
   */
  @Column(name = "image_url", nullable = false)
  private String imageUrl;

  /**
   * <h3>Position</h3>
   * <p>Display order/sequence of the image (required).</p>
   */
  @Column(nullable = false)
  private Integer position;
}