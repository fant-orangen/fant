package stud.ntnu.backend.data.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.backend.model.enums.Role;

/**
 * <h2>AdminUserUpdateDto</h2>
 * <p>Data transfer object for an admin updating user and profile updates.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserUpdateDto {
  /**
   * <h3>Email</h3>
   * <p>User's email address.</p>
   */
  @Email(message = "Email should be valid")
  private String email;

  /**
   * <h3>Password</h3>
   * <p>Account password (min 8 characters, and at least one capital letter and number).</p>
   */
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Password must be at least 8 characters long and contain at least one lowercase letter, one uppercase letter, and one digit")
  private String password;

  /**
   * <h3>Display Name</h3>
   * <p>Public display name (max 50 characters).</p>
   */
  @Size(max = 50, message = "Display name must be less than 50 characters")
  private String displayName;

  /**
   * <h3>First Name</h3>
   * <p>User's first name (max 50 characters).</p>
   */
  @Size(max = 50, message = "First name must be less than 50 characters")
  private String firstName;

  /**
   * <h3>Last Name</h3>
   * <p>User's last name (max 50 characters).</p>
   */
  @Size(max = 50, message = "Last name must be less than 50 characters")
  private String lastName;

  /**
   * <h3>Phone Number</h3>
   * <p>International format phone number (e.g., +47 123 45 678).</p>
   */
  @Pattern(regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$", message = "Phone number must be in international format (+XX XXXX XXX XXX)")
  private String phone;

  /**
   * <h3>Role</h3>
   * Defines the user's level of access and permissions within the system.
   * Possible values are defined in the {@code Role} enumeration, such as USER or ADMIN.
   */
  private Role role;
}