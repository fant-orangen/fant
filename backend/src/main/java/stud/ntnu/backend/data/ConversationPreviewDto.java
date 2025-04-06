package stud.ntnu.backend.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2>ConversationPreviewDto</h2>
 * <p>Data transfer object for displaying conversation summaries.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationPreviewDto {

  /**
   * <h3>Conversation ID</h3>
   * <p>Unique identifier for the conversation.</p>
   */
  private Long id;

  /**
   * <h3>Other User</h3>
   * <p>Details of the other participant in the conversation.</p>
   */
  private MessageUserDto otherUser;

  /**
   * <h3>Item</h3>
   * <p>Preview information about the related item.</p>
   */
  private ItemPreviewDto item;

  /**
   * <h3>Last Message</h3>
   * <p>Most recent message in the conversation.</p>
   */
  private MessageDto lastMessage;

  /**
   * <h3>Unread Messages Count</h3>
   * <p>Number of unread messages in the conversation.</p>
   */
  private int unreadMessagesCount;

  /**
   * <h3>Related Item</h3>
   * <p>Basic information about the associated item.</p>
   */
  private RelatedItemDto relatedItem;

  /**
   * <h2>RelatedItemDto</h2>
   * <p>Nested DTO for minimal item information.</p>
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class RelatedItemDto {

    /**
     * <h3>Item ID</h3>
     * <p>Unique identifier for the item.</p>
     */
    private Long id;

    /**
     * <h3>Title</h3>
     * <p>Display title of the item.</p>
     */
    private String title;
  }
}