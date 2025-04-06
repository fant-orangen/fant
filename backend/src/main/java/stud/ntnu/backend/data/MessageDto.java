package stud.ntnu.backend.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * <h2>MessageDto</h2>
 * <p>Data transfer object for complete message information.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {

  /**
   * <h3>Message ID</h3>
   * <p>Unique identifier of the message.</p>
   */
  private Long id;

  /**
   * <h3>Content</h3>
   * <p>The text content of the message.</p>
   */
  private String content;

  /**
   * <h3>Sender ID</h3>
   * <p>Unique identifier of the message sender.</p>
   */
  private Long senderId;

  /**
   * <h3>Receiver ID</h3>
   * <p>Unique identifier of the message recipient.</p>
   */
  private Long receiverId;

  /**
   * <h3>Sent At</h3>
   * <p>Timestamp when the message was sent.</p>
   */
  private LocalDateTime sentAt;

  /**
   * <h3>Read Status</h3>
   * <p>Indicates whether the message has been read.</p>
   */
  private boolean read;
}