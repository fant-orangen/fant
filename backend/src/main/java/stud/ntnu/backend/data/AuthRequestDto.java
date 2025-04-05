package stud.ntnu.backend.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
   * <h3> The email of the user.</h3>
   */
  @NotBlank(message = "Email is required")
  @Email
  private String email;

  /**
   * <h3> The password of the user.</h3>
   */
  @NotBlank(message = "Password is required")
  private String password;
}