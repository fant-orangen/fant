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

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private ModelMapper modelMapper;

  @Mock
  private Principal principal;

  private UserService userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userService = new UserService(userRepository, passwordEncoder, modelMapper);
  }

  @Test
  void updateUser_shouldUpdateUserSuccessfully() {
    Long userId = 1L;

    // Prepare the input data for the update
    UserUpdateDto userUpdateDto = new UserUpdateDto();
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
    User updatedUser = userService.updateUser(userUpdateDto, userId);

    // Assertions
    assertNotNull(updatedUser);  // Ensure the updated user is not null
    assertEquals("encodedNewPassword",
        updatedUser.getPasswordHash());  // Ensure password was updated
    verify(userRepository).findById(userId);  // Ensure findById was called
    verify(userRepository).save(existingUser);  // Ensure save was called
  }


  @Test
  void updateUser_shouldThrowEntityNotFoundException_whenUserNotFound() {
    Long userId = 1L;
    UserUpdateDto userUpdateDto = new UserUpdateDto();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,
        () -> userService.updateUser(userUpdateDto, userId));
  }

  @Test
  void deleteUser_shouldDeleteUserSuccessfully() {
    Long userId = 1L;
    userService.deleteUser(userId);

    verify(userRepository).deleteById(userId);
  }

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

  @Test
  void getUserById_shouldThrowEntityNotFoundException_whenUserNotFound() {
    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));
  }

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

  @Test
  void getCurrentUser_shouldThrowEntityNotFoundException_whenUserNotFound() {
    when(principal.getName()).thenReturn("nonexistent@example.com");
    when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> userService.getCurrentUser(principal));
  }

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
