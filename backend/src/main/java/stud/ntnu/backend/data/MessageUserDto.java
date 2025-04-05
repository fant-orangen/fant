package stud.ntnu.backend.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2>MessageUserDto</h2>
 * <p>Data transfer object for user information in messaging contexts.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageUserDto {

  /**
   * <h3>User ID</h3>
   * <p>Unique identifier of the user.</p>
   */
  private Long id;

  /**
   * <h3>Display Name</h3>
   * <p>User's display name for messaging.</p>
   */
  private String displayName;

  /**
   * <h3>Email</h3>
   * <p>User's email address.</p>
   */
  private String email;
}