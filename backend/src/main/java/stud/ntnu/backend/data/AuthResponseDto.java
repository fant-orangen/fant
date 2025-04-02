package stud.ntnu.backend.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2> AuthResponseDto for representing an authentication response. </h2>
 */
@Data
@AllArgsConstructor
public class AuthResponseDto {
  /**
   * <h3> The token. </h3>
   */
  private String token; // Contains the JWT token
}