package stud.ntnu.backend.data.user;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2>UserResponseDto</h2>
 * <p>Public-facing data transfer object for user information.</p>
 * <p>MODIFIED: Now includes email.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

  /**
   * <h3>Display Name</h3>
   * <p>User's public display name.</p>
   */
  private String displayName;

  /**
   * <h3>Creation Timestamp</h3>
   * <p>Date and time when the user account was created.</p>
   */
  private LocalDateTime createdAt;

  /**
   * <h3>Email</h3>
   * <p>User's unique email address.</p>
   * <p>Added for display on public profile.</p>
   */
  private String email;

}