package stud.ntnu.backend.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.backend.model.User;


/**
 * <h2> Public-facing user data object. </h2>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto {
  /**
   * <h3> The email of the user. </h3>
   */

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 6, message = "Password must be at least 6 characters")
  private String password;

  private String firstName;
  private String lastName;

  /**
   * <h3> The username of the user. </h3>
   */
  private String username;

  private String phone;
}

