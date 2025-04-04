package stud.ntnu.backend.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2>ConversationPreviewDto</h2>
 * <p>Data Transfer Object for conversation preview information.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationPreviewDto {

  private Long id;
  private MessageUserDto otherUser;
  private ItemPreviewDto item;
  private MessageDto lastMessage;
  private int unreadMessagesCount;
  private RelatedItemDto relatedItem;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class RelatedItemDto {

    private Long id;
    private String title;
  }
}