package stud.ntnu.backend.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2> AuthRequestDTO for representing the authentication request. </h2>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDto {

  /**
   * <h3> The username of the user.</h3>
   */
  private String email;

  /**
   * <h3> The password of the user.</h3>
   */
  private String password;
}