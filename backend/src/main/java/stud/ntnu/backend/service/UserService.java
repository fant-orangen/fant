package stud.ntnu.backend.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.data.UserRequestDto;
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

  private final ModelMapper modelMapper;

  public User createUser(UserRequestDto registrationDto) {
    return userRepository.save(fromDto(registrationDto));
  }

  public User updateUser(UserRequestDto userRequestDto, Long id) {
    User user = fromDto(userRequestDto);
    user.setId(id);
    return userRepository.save(user);
  }

  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  public List<User> getAll() {
    return userRepository.findAll();
  }

  public UserResponseDto getUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    return modelMapper.map(user, UserResponseDto.class);
  }

  public User getCurrentUser(Principal principal) {
    return userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new EntityNotFoundException("Current user not found"));
  }

  public Long getCurrentUserId(Principal principal) {
    return getCurrentUser(principal).getId();
  }

  private User fromDto(UserRequestDto userRequestDto) {
    User user = modelMapper.map(userRequestDto, User.class);
    user.setPasswordHash(passwordEncoder.encode(userRequestDto.getPassword()));
    return user;
  }
}