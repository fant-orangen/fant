package stud.ntnu.backend.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.UserDto;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.service.UserService;

/**
 * <h2> The AdminController class is a REST controller that handles HTTP requests related to admin-functionality.</h2>
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
  /**
   * <h3> The UserService. </h3>
   */
  private final UserService userService;

  @GetMapping("/users")
  public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(userService.findAll());
  }
}
