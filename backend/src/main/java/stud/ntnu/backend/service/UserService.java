package stud.ntnu.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.data.UserDto;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repos.UserRepository;

/**
 * <h2> UserService for user-related business logic. </h2>
 */
@Service
@RequiredArgsConstructor
public class UserService {

  /**
   * <h3> The UserRepository. </h3>
   */
  private final UserRepository userRepository;

  /**
   * <h3> Get user by id. </h3>
   *
   * @param id The id of the user.
   * @return The user with the given id.
   */
  public UserDto getUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));
    return UserDto.from(user);
  }
}

