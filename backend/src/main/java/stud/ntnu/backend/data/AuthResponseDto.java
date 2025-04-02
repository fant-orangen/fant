package stud.ntnu.backend.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2> AuthResponseDto for representing an authentication response. </h2>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {

  /**
   * <h3> The token. </h3>
   */
  private String data; // Contains the JWT token
}