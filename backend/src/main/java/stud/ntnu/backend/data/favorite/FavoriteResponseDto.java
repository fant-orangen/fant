package stud.ntnu.backend.data.favorite;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <h2>FavoriteResponseDto</h2>
 * <p>Data transfer object for favorite item responses.</p>
 */
@Data
@AllArgsConstructor
public class FavoriteResponseDto {

  /**
   * <h3>Item ID</h3>
   * <p>Unique identifier of the favorited item.</p>
   */
  private Long itemId;

  /**
   * <h3>Creation Timestamp</h3>
   * <p>Date and time when the item was favorited.</p>
   */
  private LocalDateTime createdAt;
}