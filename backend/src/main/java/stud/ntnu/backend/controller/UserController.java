package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.security.Principal;
import java.util.Optional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.UserRequestDto;
import stud.ntnu.backend.data.UserResponseDto;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.service.UserService;

/**
 * <h2>UserController</h2>
 * <p>REST controller that handles user-related operations.</p>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Operations related to user management")
public class UserController {

  /**
   * <h3>The UserService.</h3>
   */
  private final UserService userService;

  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDto> getUser(@Positive @PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @PutMapping("/profile")
  public ResponseEntity<User> updateUser(@Valid @RequestBody UserRequestDto userRequestDto,
                                         Principal principal) {
    return ResponseEntity.ok(
        userService.updateUser(userRequestDto, userService.getCurrentUserId(principal)));
  }

  @GetMapping("/profile")
  public ResponseEntity<User> getCurrentUser(Principal principal) {
    return ResponseEntity.ok(userService.getCurrentUser(principal));
  }

  @GetMapping("/id")
  public ResponseEntity<Long> getCurrentUserId(Principal principal) {
    return ResponseEntity.ok(userService.getCurrentUserId(principal));
  }
}
