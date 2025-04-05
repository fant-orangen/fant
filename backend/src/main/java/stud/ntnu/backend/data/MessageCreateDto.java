package stud.ntnu.backend.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2>MessageCreateDto</h2>
 * <p>Data Transfer Object for creating new messages.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageCreateDto {

  private Long senderId;
  private Long receiverId;
  private Long itemId;
  private String content;
}