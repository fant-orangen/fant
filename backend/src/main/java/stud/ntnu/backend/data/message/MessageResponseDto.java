package stud.ntnu.backend.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * <h2>MessageResponseDto</h2>
 * <p>Data transfer object for complete message responses including user and item references.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponseDto {

  /**
   * <h3>Message ID</h3>
   * <p>Unique identifier of the message.</p>
   */
  private Long id;

  /**
   * <h3>Sender</h3>
   * <p>User information of the message sender.</p>
   */
  private MessageUserDto sender;

  /**
   * <h3>Receiver</h3>
   * <p>User information of the message recipient.</p>
   */
  private MessageUserDto receiver;

  /**
   * <h3>Item Reference</h3>
   * <p>Basic information about the related item.</p>
   */
  private ItemReferenceDto item;

  /**
   * <h3>Message Content</h3>
   * <p>The actual text content of the message.</p>
   */
  private String messageContent;

  /**
   * <h3>Is Read</h3>
   * <p>Status indicating whether the message has been read.</p>
   */
  @JsonProperty("isRead")
  private boolean isRead;

  /**
   * <h3>Sent Date</h3>
   * <p>Timestamp when the message was sent.</p>
   */
  private LocalDateTime sentDate;

  /**
   * <h2>ItemReferenceDto</h2>
   * <p>Nested DTO containing minimal item reference information.</p>
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ItemReferenceDto {

    /**
     * <h3>Item ID</h3>
     * <p>Unique identifier of the referenced item.</p>
     */
    private Long id;

    /**
     * <h3>Item Title</h3>
     * <p>Display title of the referenced item.</p>
     */
    private String title;
  }
}