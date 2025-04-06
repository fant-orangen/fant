package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.security.Principal;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.user.UserCreateDto;
import stud.ntnu.backend.data.user.UserResponseDto;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.service.UserService;

/**
 * <h2>UserController</h2>
 * <p>Controller for user profile and account operations.</p>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Operations related to user management")
public class UserController {

  /**
   * <h3>User Service</h3>
   * <p>Service handling user account operations.</p>
   *
   * @see UserService
   */
  private final UserService userService;

  /**
   * <h3>Get User By ID</h3>
   * <p>Retrieves user details by user ID.</p>
   *
   * @param id the user ID
   * @return {@link UserResponseDto} with user details
   */
  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDto> getUser(@Positive @PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  /**
   * <h3>Update User Profile</h3>
   * <p>Updates the current user's profile information.</p>
   *
   * @param userCreateDto the updated user data
   * @param principal      the authenticated user
   * @return updated {@link User} entity
   */
  @PutMapping("/profile")
  public ResponseEntity<User> updateUser(@Valid @RequestBody UserCreateDto userCreateDto,
                                         Principal principal) {
    return ResponseEntity.ok(
        userService.updateUser(userCreateDto, userService.getCurrentUserId(principal)));
  }

  /**
   * <h3>Get Current User</h3>
   * <p>Retrieves details of the currently authenticated user.</p>
   *
   * @param principal the authenticated user
   * @return current {@link User} entity
   */
  @GetMapping("/profile")
  public ResponseEntity<User> getCurrentUser(Principal principal) {
    return ResponseEntity.ok(userService.getCurrentUser(principal));
  }

  /**
   * <h3>Get Current User ID</h3>
   * <p>Retrieves the ID of the currently authenticated user.</p>
   *
   * @param principal the authenticated user
   * @return the user ID
   */
  @GetMapping("/id")
  public ResponseEntity<Long> getCurrentUserId(Principal principal) {
    return ResponseEntity.ok(userService.getCurrentUserId(principal));
  }
}
