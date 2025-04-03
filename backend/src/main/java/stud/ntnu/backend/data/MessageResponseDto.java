package stud.ntnu.backend.data;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponseDto {
  private Long senderId;
  private Long receiverId;
  private Long itemId;

  private String content;
  private LocalDateTime sentAt;
}
