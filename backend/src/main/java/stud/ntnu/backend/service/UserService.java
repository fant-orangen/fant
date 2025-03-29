package stud.ntnu.backend.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  /**
   * <h3> Get user by id. </h3>
   *
   * @param id The id of the user.
   * @return The user with the given id.
   */
  public UserDto getUserById(Long id) {
    logger.info("fetching user with id: {} ", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));
    return UserDto.from(user);
  }
}

