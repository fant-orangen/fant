package stud.ntnu.backend.unit;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import stud.ntnu.backend.data.user.UserCreateDto;
import stud.ntnu.backend.data.user.UserResponseDto;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.UserRepository;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import stud.ntnu.backend.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;
  private ModelMapper modelMapper;
  private UserService userService;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    passwordEncoder = mock(PasswordEncoder.class);
    modelMapper = mock(ModelMapper.class);
    userService = new UserService(userRepository, passwordEncoder, modelMapper);
  }

  @Test
  void createUser_shouldSaveUser() {
    UserCreateDto dto = new UserCreateDto();
    dto.setPassword("secret");

    User user = new User();
    user.setPasswordHash("encoded");

    when(modelMapper.map(dto, User.class)).thenReturn(user);
    when(passwordEncoder.encode("secret")).thenReturn("encoded");
    when(userRepository.save(user)).thenReturn(user);

    User result = userService.createUser(dto);

    assertEquals(user, result);
    verify(userRepository).save(user);
  }

  @Test
  void updateUser_shouldUpdateUserWithGivenId() {
    UserCreateDto dto = new UserCreateDto();
    dto.setPassword("pw");

    User mappedUser = new User();
    mappedUser.setPasswordHash("encoded");

    when(modelMapper.map(dto, User.class)).thenReturn(mappedUser);
    when(passwordEncoder.encode("pw")).thenReturn("encoded");
    when(userRepository.save(mappedUser)).thenReturn(mappedUser);

    User result = userService.updateUser(dto, 42L);

    assertEquals(mappedUser, result);
    assertEquals(42L, result.getId());
    verify(userRepository).save(mappedUser);
  }

  @Test
  void deleteUser_shouldCallRepositoryDeleteById() {
    userService.deleteUser(5L);
    verify(userRepository).deleteById(5L);
  }

  @Test
  void getAll_shouldReturnPageOfUsers() {
    Pageable pageable = Pageable.unpaged();
    Page<User> page = new PageImpl<>(List.of(new User()));

    when(userRepository.findAll(pageable)).thenReturn(page);

    Page<User> result = userService.getAll(pageable);

    assertEquals(page, result);
  }

  @Test
  void getUserById_shouldReturnUserDto() {
    User user = new User();
    UserResponseDto dto = new UserResponseDto();

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(modelMapper.map(user, UserResponseDto.class)).thenReturn(dto);

    UserResponseDto result = userService.getUserById(1L);

    assertEquals(dto, result);
  }

  @Test
  void getUserById_shouldThrowIfNotFound() {
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> userService.getUserById(99L));
  }

  @Test
  void getCurrentUser_shouldReturnUser() {
    Principal principal = () -> "email@example.com";
    User user = new User();

    when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.of(user));

    User result = userService.getCurrentUser(principal);

    assertEquals(user, result);
  }

  @Test
  void getCurrentUser_shouldThrowIfNotFound() {
    Principal principal = () -> "missing@example.com";
    when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> userService.getCurrentUser(principal));
  }

  @Test
  void getCurrentUserId_shouldReturnId() {
    Principal principal = () -> "user@domain.com";
    User user = new User();
    user.setId(7L);

    when(userRepository.findByEmail("user@domain.com")).thenReturn(Optional.of(user));

    Long result = userService.getCurrentUserId(principal);

    assertEquals(7L, result);
  }
}
