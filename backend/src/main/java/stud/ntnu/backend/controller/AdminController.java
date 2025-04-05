package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.CategoryRequestDto;
import stud.ntnu.backend.data.UserRequestDto;
import stud.ntnu.backend.model.Category;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.service.CategoryService;
import stud.ntnu.backend.service.UserService;

/**
 * REST controller for administrative operations requiring elevated privileges.
 * <p>
 * Provides endpoints for managing system-wide resources including users and categories.
 * All endpoints are prefixed with {@code /api/admin}.
 * </p>
 *
 * @see User
 * @see Category
 * @see CategoryRequestDto
 * @see UserService
 * @see CategoryService
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {


  private final UserService userService;

  private final CategoryService categoryService;

  @PutMapping("/users/{id}")
  public ResponseEntity<User> updateUser(@Valid @RequestBody UserRequestDto userRequestDto,
                                         @Positive @PathVariable Long id) {
    return ResponseEntity.ok(userService.updateUser(userRequestDto, id));
  }

  @DeleteMapping("users/{id}")
  public ResponseEntity<Void> deleteUser(@Valid @PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/users")
  public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(userService.getAll());
  }

  @PostMapping("/category")
  public ResponseEntity<Category> createCategory(
      @Valid @RequestBody CategoryRequestDto categoryRequestDto) {
    return ResponseEntity.ok(categoryService.create(categoryRequestDto));
  }

  @PutMapping("/category/{id}")
  public ResponseEntity<Category> updateCategory(@Valid @RequestBody
                                                 CategoryRequestDto categoryRequestDto,
                                                 @Positive @PathVariable Long id) {
    return ResponseEntity.ok(categoryService.update(categoryRequestDto, id));
  }

  @DeleteMapping("/category/{id}")
  public ResponseEntity<Void> deleteCategory(@Positive @PathVariable Long id) {
    categoryService.delete(id);
    return ResponseEntity.ok().build();
  }
}