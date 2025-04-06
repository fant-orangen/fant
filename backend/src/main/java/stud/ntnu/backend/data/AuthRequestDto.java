package stud.ntnu.backend.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2>AuthRequestDto</h2>
 * <p>Data transfer object for user authentication requests.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDto {

  /**
   * <h3>Email</h3>
   * <p>The user's email address used for authentication.</p>
   */
  @NotBlank(message = "Email is required")
  @Email
  private String email;

  /**
   * <h3>Password</h3>
   * <p>The user's password used for authentication.</p>
   */
  @NotBlank(message = "Password is required")
  private String password;
}