package stud.ntnu.backend.controller;

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
 * <h2> The CategoryController class is a REST controller that handles HTTP requests regarding categories.</h2>
 */
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

  /**
   * <h3> The CategoryService. </h3>
   */
  private final CategoryService categoryService;

  /**
   * <h3> Get category by id. </h3>
   * @param id The id of the category.
   * @return The category with the given id.
   */
  @GetMapping("/{id}")
  public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
    return ResponseEntity.ok(categoryService.findById(id));
  }

  @GetMapping
  public ResponseEntity<List<Category>> getAllCategories() {
    return ResponseEntity.ok(categoryService.findAll());
  }
}
