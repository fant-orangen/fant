package stud.ntnu.backend.controller;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.UserResponseDto;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.service.UserService;

/**
 * <h2> The UserController class is a REST controller that handles HTTP requests related to users.</h2>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  /**
   * <h3> The UserService. </h3>
   */
  private final UserService userService;

  /**
   * <h3> Get user by id. </h3>
   * @param id The id of the user.
   * @return The user with the given id.
   */
  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @GetMapping("/profile")
  public ResponseEntity<Optional<User>> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return ResponseEntity.ok(userService.findByEmail(authentication.getName()));
  }
}

