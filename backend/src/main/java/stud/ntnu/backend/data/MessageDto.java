package stud.ntnu.backend.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <h2>MessageDto</h2>
 * <p>Data Transfer Object for message information.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {

  private Long id;
  private String content;
  private Long senderId;
  private Long receiverId;
  private LocalDateTime sentAt;
  private boolean read;
}