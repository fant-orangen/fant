package stud.ntnu.backend.data;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FavoriteResponseDto {
  private long itemId;
  private LocalDateTime createdAt;
}
