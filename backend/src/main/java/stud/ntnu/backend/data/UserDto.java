package stud.ntnu.backend.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.backend.model.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
  private Long id;
  private String username;
  private String email;

  public static UserDto from(User user) {
    return new UserDto(user.getId(), user.getUsername(), user.getEmail());
  }
}

