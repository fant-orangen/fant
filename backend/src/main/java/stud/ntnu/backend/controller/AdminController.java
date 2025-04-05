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

  /**
   * Service for handling user management operations.
   */
  private final UserService userService;

  /**
   * Service for handling category management operations.
   */
  private final CategoryService categoryService;

  /**
   * Retrieves all registered users in the system.
   *
   * @return {@link ResponseEntity} containing a list of all {@link User} entities
   */
  @GetMapping("/users")
  public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(userService.findAll());
  }

  /**
   * Creates a new category in the system.
   *
   * @param categoryRequestDto the {@link CategoryRequestDto} containing category details
   * @return {@link ResponseEntity} containing the created {@link Category}
   */
  @PostMapping("/category")
  public ResponseEntity<Category> createCategory(
      @Valid @RequestBody CategoryRequestDto categoryRequestDto) {
    return ResponseEntity.ok(categoryService.create(categoryRequestDto));
  }

  /**
   * Updates an existing category.
   *
   * @param id                 the ID of the category to update (must be positive)
   * @param categoryRequestDto the {@link CategoryRequestDto} containing updated details
   * @return {@link ResponseEntity} containing the updated {@link Category}
   */
  @PutMapping("/category/{id}")
  public ResponseEntity<Category> updateCategory(@Positive @PathVariable Long id,
                                                 @Valid @RequestBody
                                                 CategoryRequestDto categoryRequestDto) {
    return ResponseEntity.ok(categoryService.update(categoryRequestDto, id));
  }

  /**
   * Deletes a category by its ID.
   *
   * @param id the ID of the category to delete (must be positive)
   * @return empty {@link ResponseEntity} with status 200 if successful
   */
  @DeleteMapping("/category/{id}")
  public ResponseEntity<Void> deleteCategory(@Positive @PathVariable Long id) {
    categoryService.delete(id);
    return ResponseEntity.ok().build();
  }
}