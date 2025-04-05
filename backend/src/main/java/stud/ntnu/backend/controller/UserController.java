package stud.ntnu.backend.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

  private Logger logger = LoggerFactory.getLogger(UserController.class);

  /**
   * <h3>Get user by id.</h3>
   *
   * @return The user with the given id.
   */
  @GetMapping("/id")
  public ResponseEntity<Long> getUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    logger.info(email);

    User user = userService.getUserByEmail(email);
    return ResponseEntity.ok(user.getId());
  }

  @GetMapping("/profile")
  public ResponseEntity<User> getCurrentUser(Principal principal) {
    return ResponseEntity.ok(userService.getCurrentUser(principal));
  }
}
