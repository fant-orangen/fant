package stud.ntnu.backend.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import stud.ntnu.backend.data.user.AdminUserUpdateDto;
import stud.ntnu.backend.data.user.UserCreateDto;
import stud.ntnu.backend.data.user.UserResponseDto;
import stud.ntnu.backend.data.user.UserUpdateDto;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.model.enums.Role;
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
   * <p>Component for object-to-object mapping.</p>
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
    User user = User.builder().email(registrationDto.getEmail())
        .passwordHash(passwordEncoder.encode(registrationDto.getPassword()))
        .firstName(registrationDto.getFirstName()).lastName(registrationDto.getLastName())
        .phone(registrationDto.getPhone()).displayName(registrationDto.getDisplayName())
        .role(Role.USER).build();
    return userRepository.save(user);
  }

  /**
   * <h3>Admin Update User</h3>
   * <p>Allows administrators to update any user's profile information, including their role.</p>
   *
   * @param userUpdateDto the updated user data provided by the administrator
   * @param id            the ID of the user to update
   * @return the updated {@link User}
   * @throws EntityNotFoundException if the user with the given ID does not exist
   */
  @Transactional
  public User adminUpdateUser(AdminUserUpdateDto userUpdateDto, Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    updateUser(user, userUpdateDto.getEmail(), userUpdateDto.getPassword(),
        userUpdateDto.getDisplayName(), userUpdateDto.getFirstName(), userUpdateDto.getLastName(),
        userUpdateDto.getPhone());
    if (userUpdateDto.getRole() != null) {
      user.setRole(userUpdateDto.getRole());
    }
    return userRepository.save(user);
  }

  /**
   * <h3>Verify and Update User</h3>
   * <p>Verifies the current password of a user before updating their profile information.</p>
   *
   * @param userUpdateDto the updated user data, including the current password
   * @param id            the ID of the user to update
   * @return the updated {@link User} if the current password is correct
   * @throws EntityNotFoundException  if the user with the given ID does not exist
   * @throws BadCredentialsException  if the provided current password does not match the user's stored password
   * @throws IllegalArgumentException if the current password field is null
   */
  @Transactional
  public User verifyAndUpdate(UserUpdateDto userUpdateDto, Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    if (!passwordEncoder.matches(userUpdateDto.getCurrentPassword(), user.getPasswordHash())) {
      throw new BadCredentialsException("Wrong password");
    }
    updateUser(user, userUpdateDto.getEmail(), userUpdateDto.getPassword(),
        userUpdateDto.getDisplayName(), userUpdateDto.getFirstName(), userUpdateDto.getLastName(),
        userUpdateDto.getPhone());
    return userRepository.save(user);
  }

  /**
   * <h3>Update User Fields</h3>
   * <p>A private helper method to update the modifiable fields of a {@link User} entity.</p>
   * <p>Only non-null values from the provided parameters will be used to update the user's attributes.</p>
   *
   * @param user        the {@link User} entity to update
   * @param email       the new email address (nullable)
   * @param password    the new password (nullable)
   * @param displayName the new display name (nullable)
   * @param firstName   the new first name (nullable)
   * @param lastName    the new last name (nullable)
   * @param phone       the new phone number (nullable)
   */
  private void updateUser(User user, String email, String password, String displayName,
                          String firstName, String lastName, String phone) {
    if (email != null) {
      user.setEmail(email);
    }
    if (password != null) {
      user.setPasswordHash(passwordEncoder.encode(password));
    }
    if (displayName != null) {
      user.setDisplayName(displayName);
    }
    if (firstName != null) {
      user.setFirstName(firstName);
    }
    if (lastName != null) {
      user.setLastName(lastName);
    }
    if (phone != null) {
      user.setPhone(phone);
    }
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
   * <h3>Get All Users (Paginated)</h3>
   * <p>Retrieves all user accounts with pagination support.</p>
   *
   * @param pageable the pagination information (page number, size, sort)
   * @return a {@link Page} of {@link User} entities
   */
  public Page<User> getAll(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  /**
   * <h3>Get User By ID</h3>
   * <p>Retrieves a user by their unique identifier and maps it to a {@link UserResponseDto}.</p>
   * <p>This method is intended for general user retrieval, exposing a limited set of user information.</p>
   *
   * @param id the user ID
   * @return {@link UserResponseDto} representing the user
   * @throws EntityNotFoundException if user not found with the given ID
   */
  public UserResponseDto getUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    return modelMapper.map(user, UserResponseDto.class);
  }

  /**
   * <h3>Get User By ID for Admins</h3>
   * <p>Retrieves a user by their unique identifier, returning the full {@link User} entity.</p>
   * <p>This method is intended for administrative operations where access to all user details is required.</p>
   *
   * @param id the user ID
   * @return {@link User} the full user entity
   * @throws EntityNotFoundException if user not found with the given ID
   */
  public User adminGetUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
  }

  /**
   * <h3>Get Current User</h3>
   * <p>Retrieves the authenticated user's profile based on the provided {@link Principal}.</p>
   *
   * @param principal the authenticated user's principal
   * @return the current {@link User} entity
   * @throws EntityNotFoundException if the user associated with the principal is not found
   */
  public User getCurrentUser(Principal principal) {
    return userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new EntityNotFoundException("Current user not found"));
  }

  /**
   * <h3>Get Current User ID</h3>
   * <p>Retrieves the unique identifier of the authenticated user.</p>
   *
   * @param principal the authenticated user's principal
   * @return the ID of the current user
   * @throws EntityNotFoundException if the user associated with the principal is not found
   */
  public Long getCurrentUserId(Principal principal) {
    return getCurrentUser(principal).getId();
  }
}