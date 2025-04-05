package stud.ntnu.backend.controller;

import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.model.Category;
import stud.ntnu.backend.service.CategoryService;

/**
 * REST controller for public category-related operations.
 * <p>
 * Provides read-only endpoints for accessing category information.
 * All endpoints are prefixed with {@code /api/category}.
 * </p>
 *
 * @see Category
 * @see CategoryService
 */
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

  /**
   * Service for handling category business logic and data access.
   */
  private final CategoryService categoryService;

  /**
   * Retrieves a specific category by its unique identifier.
   *
   * @param id the ID of the category to retrieve (must be positive)
   * @return {@link ResponseEntity} containing the requested {@link Category}
   */
  @GetMapping("/{id}")
  public ResponseEntity<Category> getCategoryById(@Positive @PathVariable Long id) {
    return ResponseEntity.ok(categoryService.getCategoryById(id));
  }

  /**
   * Retrieves all available categories in the system.
   *
   * @return {@link ResponseEntity} containing a list of all {@link Category} entities
   */
  @GetMapping
  public ResponseEntity<List<Category>> getAllCategories() {
    return ResponseEntity.ok(categoryService.getAll());
  }
}