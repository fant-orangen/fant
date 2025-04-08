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
 * <h2>CategoryController</h2>
 * <p>Controller for public read-only category operations.</p>
 */
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

  /**
   * <h3>Category Service</h3>
   * <p>Service handling category business logic.</p>
   *
   * @see CategoryService
   */
  private final CategoryService categoryService;

  /**
   * <h3>Get Category By ID</h3>
   * <p>Retrieves a single category by its identifier.</p>
   *
   * @param id the ID of the category to retrieve
   * @return the requested {@link Category}
   */
  @GetMapping("/{id}")
  public ResponseEntity<Category> getCategoryById(@Positive @PathVariable Long id) {
    return ResponseEntity.ok(categoryService.getCategoryById(id));
  }

  /**
   * <h3>Get All Categories</h3>
   * <p>Retrieves all available categories.</p>
   *
   * @return list of all {@link Category} entities
   */
  @GetMapping("/all")
  public ResponseEntity<List<Category>> getAllCategories() {
    return ResponseEntity.ok(categoryService.getAll());
  }
}