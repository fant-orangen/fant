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

  /**
   * <h3> The CategoryService. </h3>
   */
  private final CategoryService categoryService;

  /**
   * <h3>Get all users.</h3>
   *
   * @return All users as list.
   */
  @GetMapping("/users")
  public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(userService.findAll());
  }

  /**
   * <h3>Save a new category to the database.</h3>
   *
   * @param categoryRequestDto The category to be saved.
   * @return The saved category.
   */
  @PostMapping("/category")
  public ResponseEntity<Category> createCategory(@Valid @RequestBody
                                                 CategoryRequestDto categoryRequestDto) {
    return ResponseEntity.ok(categoryService.create(categoryRequestDto));
  }


  @PutMapping("/category/{id}")
  public ResponseEntity<Category> updateProduct(@Positive @PathVariable Long id,
                                                @Valid @RequestBody
                                                CategoryRequestDto categoryRequestDto) {
    return ResponseEntity.ok(categoryService.update(categoryRequestDto, id));
  }

  @DeleteMapping("/category/{id}")
  public ResponseEntity<Void> deleteCategory(@Positive @PathVariable Long id) {
    categoryService.delete(id);
    return ResponseEntity.ok().build();
  }
}
