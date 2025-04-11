package stud.ntnu.backend.data.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2>MessageCreateDto</h2>
 * <p>Data transfer object for creating new messages between users.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageCreateDto {

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
   * <h3>Item ID</h3>
   * <p>Unique identifier of the related item.</p>
   */
  private Long itemId;

  /**
   * <h3>Content</h3>
   * <p>The actual message text content.</p>
   */
  private String content;
}