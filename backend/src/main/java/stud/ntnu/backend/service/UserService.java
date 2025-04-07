package stud.ntnu.backend.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.data.user.UserCreateDto;
import stud.ntnu.backend.data.user.UserResponseDto;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.UserRepository;

/**
 * <h2>UserService</h2>
 * <p>Service for user account management operations.</p>
 */
@Service
@RequiredArgsConstructor
public class UserService {

  /**
   * <h3>User Repository</h3>
   * <p>Data access component for user accounts.</p>
   */
  private final UserRepository userRepository;

  /**
   * <h3>Password Encoder</h3>
   * <p>Component for secure password hashing.</p>
   */
  private final PasswordEncoder passwordEncoder;

  /**
   * <h3>Model Mapper</h3>
   * <p>Object mapper for DTO conversions.</p>
   */
  private final ModelMapper modelMapper;

  /**
   * <h3>Create User</h3>
   * <p>Registers a new user account.</p>
   *
   * @param registrationDto the user registration data
   * @return the created {@link User}
   */
  @Transactional
  public User createUser(UserCreateDto registrationDto) {
    return userRepository.save(fromDto(registrationDto));
  }

  /**
   * <h3>Update User</h3>
   * <p>Modifies an existing user's profile.</p>
   *
   * @param userCreateDto the updated user data
   * @param id             the user ID to update
   * @return the updated {@link User}
   */
  @Transactional
  public User updateUser(UserCreateDto userCreateDto, Long id) {
    User user = fromDto(userCreateDto);
    user.setId(id);
    return userRepository.save(user);
  }

  /**
   * <h3>Delete User</h3>
   * <p>Removes a user account.</p>
   *
   * @param id the user ID to delete
   */
  @Transactional
  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  /**
   * <h3>Get All Users</h3>
   * <p>Retrieves all user accounts.</p>
   *
   * @return list of all {@link User} entities
   */
  public Page<User> getAll(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  /**
   * <h3>Get User By ID</h3>
   * <p>Retrieves a user by their unique identifier.</p>
   *
   * @param id the user ID
   * @return {@link UserResponseDto}
   * @throws EntityNotFoundException if user not found
   */
  public UserResponseDto getUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    return toDto(user);
  }

  /**
   * <h3>Get Current User</h3>
   * <p>Retrieves the authenticated user's profile.</p>
   *
   * @param principal the authenticated user
   * @return current {@link User}
   * @throws EntityNotFoundException if user not found
   */
  public User getCurrentUser(Principal principal) {
    return userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new EntityNotFoundException("Current user not found"));
  }

  /**
   * <h3>Get Current User ID</h3>
   * <p>Retrieves the authenticated user's identifier.</p>
   *
   * @param principal the authenticated user
   * @return current user ID
   */
  public Long getCurrentUserId(Principal principal) {
    return getCurrentUser(principal).getId();
  }

  /**
   * <h3>Convert DTO to Entity</h3>
   * <p>Maps registration DTO to User entity with encrypted password.</p>
   *
   * @param userCreateDto the DTO to convert
   * @return mapped {@link User} entity
   */
  private User fromDto(UserCreateDto userCreateDto) {
    User user = modelMapper.map(userCreateDto, User.class);
    user.setPasswordHash(passwordEncoder.encode(userCreateDto.getPassword()));
    return user;
  }

  /**
   * <h3>Convert Entity to DTO</h3>
   * <p>Maps User entity to response DTO.</p>
   *
   * @param user the entity to convert
   * @return mapped {@link UserResponseDto}
   */
  public UserResponseDto toDto(User user) {
    return modelMapper.map(user, UserResponseDto.class);
  }
}