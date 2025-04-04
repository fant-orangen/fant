package stud.ntnu.backend.service;

import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.data.UserRegistrationDto;
import stud.ntnu.backend.data.UserResponseDto;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.UserRepository;

import java.util.Optional;

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

  private final PasswordEncoder passwordEncoder;

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  public User createUser(UserRegistrationDto registrationDto) {
    logger.info("creating user: {}", registrationDto);
    User user = new User();
    user.setEmail(registrationDto.getEmail());
    user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
    user.setDisplayName(registrationDto.getDisplayName());
    user.setFirstName(registrationDto.getFirstName());
    user.setLastName(registrationDto.getLastName());
    user.setPhone(registrationDto.getPhone());
    return userRepository.save(user);
  }

  /**
   * <h3>Find all users in the repository.</h3>
   *
   * @return All users as list.
   */
  public List<User> findAll() {
    return userRepository.findAll();
  }

  /**
   * <h3> Find user by email. </h3>
   *
   * @param email The email of the user.
   * @return An Optional containing the user if found, or empty if not found.
   */
  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  /**
   * <h3> Get user by id. </h3>
   *
   * @param id The id of the user.
   * @return The user with the given id.
   */
  public User getUserById(Long id) {
    logger.info("fetching user with id: {} ", id);
    return userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

  public UserResponseDto getUserResponseById(Long id) {
    User user = getUserById(id);
    return new UserResponseDto(user.getDisplayName(), user.getCreatedAt());
  }

  public User getCurrentUser(Principal principal) {
    return userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new RuntimeException("User not found"));
  }
}