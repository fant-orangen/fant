package stud.ntnu.backend.data;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * <h2> Public-facing user data object. </h2>
 */
@Data
@AllArgsConstructor
public class UserResponseDto {
  /**
   * <h3> The username of the user. </h3>
   */
  private String displayName;

  private LocalDateTime createdAt;
}

