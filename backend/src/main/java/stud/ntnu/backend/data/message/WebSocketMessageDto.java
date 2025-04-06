package stud.ntnu.backend.data.message;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * <h2>WebSocketMessageDto</h2>
 * <p>Data transfer object for real-time WebSocket messages.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebSocketMessageDto {

  /**
   * <h3>Message ID</h3>
   * <p>Unique identifier for the message.</p>
   */
  private String id;

  /**
   * <h3>Receiver</h3>
   * <p>Recipient information (required).</p>
   */
  @NotBlank
  private ReceiverDto receiver;

  /**
   * <h3>Item</h3>
   * <p>Related item information.</p>
   */
  private ItemDto item;

  /**
   * <h3>Message Content</h3>
   * <p>The text content of the message (required).</p>
   */
  @NotBlank
  private String messageContent;

  /**
   * <h3>Sent Date</h3>
   * <p>Timestamp when the message was sent.</p>
   */
  private Date sentDate;

  /**
   * <h2>ReceiverDto</h2>
   * <p>Data transfer object for message recipient information.</p>
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ReceiverDto {

    /**
     * <h3>Receiver ID</h3>
     * <p>Unique identifier of the recipient.</p>
     */
    private String id;

    /**
     * <h3>Username</h3>
     * <p>Display name of the recipient.</p>
     */
    private String username;
  }

  /**
   * <h2>ItemDto</h2>
   * <p>Data transfer object for related item information.</p>
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ItemDto {

    /**
     * <h3>Item ID</h3>
     * <p>Unique identifier of the related item.</p>
     */
    private String id;

    /**
     * <h3>Item Title</h3>
     * <p>Display title of the related item.</p>
     */
    private String title;
  }
}