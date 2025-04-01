package stud.ntnu.backend.data;

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
public class UserDto {

  /**
   * <h3> The id of the user. </h3>
   */
  private Long id;

  /**
   * <h3> The username of the user. </h3>
   */
  private String username;

  /**
   * <h3> The email of the user. </h3>
   */
  private String email;

  /**
   * <h3> Create a UserDto from a User object. </h3>
   *
   * @param user The user object.
   * @return The UserDto object.
   */
  public static UserDto from(User user) {
    return new UserDto(user.getId(), user.getUsername(), user.getEmail());
  }
}

