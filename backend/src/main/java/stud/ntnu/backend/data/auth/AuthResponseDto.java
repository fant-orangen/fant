package stud.ntnu.backend.data.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <h2>AuthResponseDto</h2>
 * <p>Data transfer object for authentication responses containing JWT tokens.</p>
 */
@Data
@AllArgsConstructor
public class AuthResponseDto {

  /**
   * <h3>Token</h3>
   * <p>JSON Web Token (JWT) for authenticated session.</p>
   */
  private String token;
}