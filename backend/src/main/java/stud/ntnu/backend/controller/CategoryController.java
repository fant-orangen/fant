package stud.ntnu.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
@Tag(name = "Category", description = "Public operations for retrieving categories.")
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
   * @return the requested {@link ResponseEntity} containing the {@link Category}
   */
  @GetMapping("/{id}")
  @Operation(summary = "Get Category by ID", description = "Retrieves a category based on its unique identifier.")
  @ApiResponse(responseCode = "200", description = "Category found", content = @Content(schema = @Schema(implementation = Category.class)))
  @ApiResponse(responseCode = "400", description = "Invalid category ID", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Category> getCategoryById(
      @Parameter(description = "ID of the category to retrieve", required = true) @Positive
      @PathVariable Long id) {
    return ResponseEntity.ok(categoryService.getCategoryById(id));
  }

  /**
   * <h3>Get All Categories</h3>
   * <p>Retrieves all available categories.</p>
   *
   * @return the {@link ResponseEntity} containing a list of all {@link Category} entities
   */
  @GetMapping("/all")
  @Operation(summary = "Get All Categories", description = "Retrieves a list of all available categories.")
  @ApiResponse(responseCode = "200", description = "List of all categories", content = @Content(schema = @Schema(implementation = List.class, subTypes = {
      Category.class})))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<List<Category>> getAllCategories() {
    return ResponseEntity.ok(categoryService.getAll());
  }
}