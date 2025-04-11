package stud.ntnu.backend.unit;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import stud.ntnu.backend.data.user.AdminUserUpdateDto;
import stud.ntnu.backend.data.user.UserCreateDto;
import stud.ntnu.backend.data.user.UserResponseDto;
import stud.ntnu.backend.data.user.UserUpdateDto;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.UserRepository;

import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;
import stud.ntnu.backend.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * <h2>Unit Tests for User Service</h2>
 * <p>This class contains unit tests for the {@link UserService} class, focusing on the business logic for updating, deleting, retrieving users, and managing the current user's information.</p>
 * <p>The tests utilize Mockito to mock the dependencies of the {@code UserService}, such as {@link UserRepository}, {@link PasswordEncoder}, and {@link ModelMapper}, allowing for isolated testing of the service's methods.</p>
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  /**
   * <h3>Mock UserRepository</h3>
   * <p>A Mockito mock for the {@link UserRepository} dependency, used to simulate database interactions related to users.</p>
   */
  @Mock
  private UserRepository userRepository;

  /**
   * <h3>Mock PasswordEncoder</h3>
   * <p>A Mockito mock for the {@link PasswordEncoder} dependency, used to simulate password encoding.</p>
   */
  @Mock
  private PasswordEncoder passwordEncoder;

  /**
   * <h3>Mock ModelMapper</h3>
   * <p>A Mockito mock for the {@link ModelMapper} dependency, used for mapping between entities and DTOs.</p>
   */
  @Mock
  private ModelMapper modelMapper;

  /**
   * <h3>Mock Principal</h3>
   * <p>A Mockito mock for the {@link Principal} interface, used to represent the currently authenticated user.</p>
   */
  @Mock
  private Principal principal;

  /**
   * <h3>UserService Instance</h3>
   * <p>The instance of {@link UserService} being tested, with its dependencies injected via Mockito.</p>
   */
  private UserService userService;

  /**
   * <h3>Setup Method</h3>
   * <p>This method is executed before each test case. It initializes the {@code UserService} instance and opens Mockito annotations.</p>
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userService = new UserService(userRepository, passwordEncoder, modelMapper);
  }

  /**
   * <h3>Test Admin Update User - Success</h3>
   * <p>Tests the successful update of a user's password by an administrator. It verifies that the user exists, the password is encoded, and the updated user is saved.</p>
   */
  @Test
  void updateUser_shouldUpdateUserSuccessfully() {
    Long userId = 1L;

    // Prepare the input data for the update
    AdminUserUpdateDto userUpdateDto = new AdminUserUpdateDto();
    userUpdateDto.setPassword("newPassword");

    // Create an existing user to be updated
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setPasswordHash("oldPassword");

    // Mock the repository behavior: findById should return the existing user
    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

    // Mock the password encoder to return the encoded password
    when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

    // Mock the save method to return the updated user
    when(userRepository.save(existingUser)).thenReturn(existingUser);

    // Perform the update
    User updatedUser = userService.adminUpdateUser(userUpdateDto, userId);

    // Assertions
    assertNotNull(updatedUser);  // Ensure the updated user is not null
    assertEquals("encodedNewPassword",
        updatedUser.getPasswordHash());  // Ensure password was updated
    verify(userRepository).findById(userId);  // Ensure findById was called
    verify(userRepository).save(existingUser);  // Ensure save was called
  }

  /**
   * <h3>Test Admin Update User - User Not Found - Throws Exception</h3>
   * <p>Tests the scenario where an attempt is made to update a non-existent user. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void updateUser_shouldThrowEntityNotFoundException_whenUserNotFound() {
    Long userId = 1L;
    AdminUserUpdateDto userUpdateDto = new AdminUserUpdateDto();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,
        () -> userService.adminUpdateUser(userUpdateDto, userId));
  }

  /**
   * <h3>Test Delete User - Success</h3>
   * <p>Tests the successful deletion of a user by their ID. It verifies that the repository's {@code deleteById} method is called with the correct ID.</p>
   */
  @Test
  void deleteUser_shouldDeleteUserSuccessfully() {
    Long userId = 1L;
    userService.deleteUser(userId);

    verify(userRepository).deleteById(userId);
  }

  /**
   * <h3>Test Get All Users - Success</h3>
   * <p>Tests the successful retrieval of all users with pagination. It verifies that the repository's {@code findAll} method is called and returns a {@link Page} of users.</p>
   */
  @Test
  void getAll_shouldReturnAllUsers() {
    User user1 = new User();
    User user2 = new User();
    Page<User> userPage = new PageImpl<>(Arrays.asList(user1, user2));
    Pageable pageable = mock(Pageable.class);
    when(userRepository.findAll(pageable)).thenReturn(userPage);

    Page<User> users = userService.getAll(pageable);

    assertNotNull(users);
    assertEquals(2, users.getContent().size());
    verify(userRepository).findAll(pageable);
  }

  /**
   * <h3>Test Get User By ID - Success</h3>
   * <p>Tests the successful retrieval of a user by their ID. It verifies that the user is found and mapped to a {@link UserResponseDto}.</p>
   */
  @Test
  void getUserById_shouldReturnUserResponseDto() {
    Long userId = 1L;
    User user = new User();
    user.setId(userId);
    UserResponseDto userResponseDto = new UserResponseDto();
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(modelMapper.map(user, UserResponseDto.class)).thenReturn(userResponseDto);

    UserResponseDto response = userService.getUserById(userId);

    assertNotNull(response);
    verify(userRepository).findById(userId);
  }

  /**
   * <h3>Test Get User By ID - User Not Found - Throws Exception</h3>
   * <p>Tests the scenario where an attempt is made to retrieve a non-existent user by ID. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void getUserById_shouldThrowEntityNotFoundException_whenUserNotFound() {
    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));
  }

  /**
   * <h3>Test Get Current User - Success</h3>
   * <p>Tests the successful retrieval of the currently authenticated user. It verifies that the user's email is obtained from the {@link Principal}, the user is found by email, and the user object is returned.</p>
   */
  @Test
  void getCurrentUser_shouldReturnCurrentUser() {
    String email = "user@example.com";
    User currentUser = new User();
    currentUser.setEmail(email);
    when(principal.getName()).thenReturn(email);
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(currentUser));

    User user = userService.getCurrentUser(principal);

    assertNotNull(user);
    assertEquals(email, user.getEmail());
  }

  /**
   * <h3>Test Get Current User - User Not Found - Throws Exception</h3>
   * <p>Tests the scenario where the currently authenticated user's email does not correspond to any user in the database. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void getCurrentUser_shouldThrowEntityNotFoundException_whenUserNotFound() {
    when(principal.getName()).thenReturn("nonexistent@example.com");
    when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> userService.getCurrentUser(principal));
  }

  /**
   * <h3>Test Get Current User ID - Success</h3>
   * <p>Tests the successful retrieval of the ID of the currently authenticated user. It verifies that the user's email is obtained from the {@link Principal}, the user is found by email, and their ID is returned.</p>
   */
  @Test
  void getCurrentUserId_shouldReturnCurrentUserId() {
    Long userId = 1L;
    User currentUser = new User();
    currentUser.setId(userId);
    when(principal.getName()).thenReturn("user@example.com");
    when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(currentUser));

    Long id = userService.getCurrentUserId(principal);

    assertEquals(userId, id);
  }
}