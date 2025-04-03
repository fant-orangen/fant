package stud.ntnu.backend.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageRequestDto {
  private Long receiverId;
  private Long itemId;
  private String content;
}
