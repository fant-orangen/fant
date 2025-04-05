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
 * <h2>AdminController</h2>
 * <p>REST controller for administrative operations requiring elevated privileges.</p>
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

  /**
   * <h3>User Service</h3>
   * <p>Service for user management operations.</p>
   *
   * @see UserService
   */
  private final UserService userService;

  /**
   * <h3>Category Service</h3>
   * <p>Service for category management operations.</p>
   *
   * @see CategoryService
   */
  private final CategoryService categoryService;

  /**
   * <h3>Update User</h3>
   * <p>Updates an existing user with the provided details.</p>
   *
   * @param userRequestDto the user data to update
   * @param id             the ID of the user to update
   * @return the updated {@link User}
   */
  @PutMapping("/users/{id}")
  public ResponseEntity<User> updateUser(@Valid @RequestBody UserRequestDto userRequestDto,
                                         @Positive @PathVariable Long id) {
    return ResponseEntity.ok(userService.updateUser(userRequestDto, id));
  }

  /**
   * <h3>Delete User</h3>
   * <p>Deletes a user with the specified ID.</p>
   *
   * @param id the ID of the user to delete
   * @return empty response with OK status
   */
  @DeleteMapping("users/{id}")
  public ResponseEntity<Void> deleteUser(@Valid @PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.ok().build();
  }

  /**
   * <h3>Get All Users</h3>
   * <p>Retrieves a list of all users in the system.</p>
   *
   * @return list of all {@link User} entities
   */
  @GetMapping("/users")
  public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(userService.getAll());
  }

  /**
   * <h3>Create Category</h3>
   * <p>Creates a new category with the provided details.</p>
   *
   * @param categoryRequestDto the category data to create
   * @return the created {@link Category}
   */
  @PostMapping("/category")
  public ResponseEntity<Category> createCategory(
      @Valid @RequestBody CategoryRequestDto categoryRequestDto) {
    return ResponseEntity.ok(categoryService.create(categoryRequestDto));
  }

  /**
   * <h3>Update Category</h3>
   * <p>Updates an existing category with the provided details.</p>
   *
   * @param categoryRequestDto the category data to update
   * @param id                 the ID of the category to update
   * @return the updated {@link Category}
   */
  @PutMapping("/category/{id}")
  public ResponseEntity<Category> updateCategory(@Valid @RequestBody
                                                 CategoryRequestDto categoryRequestDto,
                                                 @Positive @PathVariable Long id) {
    return ResponseEntity.ok(categoryService.update(categoryRequestDto, id));
  }

  /**
   * <h3>Delete Category</h3>
   * <p>Deletes a category with the specified ID.</p>
   *
   * @param id the ID of the category to delete
   * @return empty response with OK status
   */
  @DeleteMapping("/category/{id}")
  public ResponseEntity<Void> deleteCategory(@Positive @PathVariable Long id) {
    categoryService.delete(id);
    return ResponseEntity.ok().build();
  }
}