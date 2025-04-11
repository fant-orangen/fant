package stud.ntnu.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * <h2>Message</h2>
 * <p>Entity representing private messages between users about marketplace items.</p>
 */
@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

  /**
   * <h3>ID</h3>
   * <p>Unique identifier for the message.</p>
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * <h3>Sender</h3>
   * <p>The user who sent the message (required).</p>
   */
  @ManyToOne(optional = false)
  @JoinColumn(name = "sender_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User sender;

  /**
   * <h3>Receiver</h3>
   * <p>The user who received the message (required).</p>
   */
  @ManyToOne(optional = false)
  @JoinColumn(name = "receiver_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User receiver;

  /**
   * <h3>Item</h3>
   * <p>The marketplace item this message relates to (required).</p>
   */
  @ManyToOne(optional = false)
  @JoinColumn(name = "item_id")
  @OnDelete(action = OnDeleteAction.SET_NULL)
  private Item item;

  /**
   * <h3>Content</h3>
   * <p>The message text content (required).</p>
   */
  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  /**
   * <h3>Sent At</h3>
   * <p>Timestamp when message was sent (auto-generated).</p>
   */
  @CreationTimestamp
  @Column(name = "sent_at", nullable = false)
  private LocalDateTime sentAt;


  /**
   * <h3>Read Status</h3>
   * <p>Indicates whether the message has been read (default: false).</p>
   */
  @Column(nullable = false)
  private boolean read;
}