package stud.ntnu.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.category.CategoryRequestDto;
import stud.ntnu.backend.data.user.UserCreateDto;
import stud.ntnu.backend.model.Category;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.service.CategoryService;
import stud.ntnu.backend.service.ItemService;
import stud.ntnu.backend.service.UserService;

/**
 * <h2>AdminController</h2>
 * <p>REST controller for administrative operations requiring elevated privileges.</p>
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Administrative operations for users, categories, and items.")
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

  private final ItemService itemService;

  /**
   * <h3>Update User</h3>
   * <p>Updates an existing user with the provided details.</p>
   *
   * @param userCreateDto the user data to update
   * @param id            the ID of the user to update
   * @return the updated {@link User}
   */
  @PutMapping("/users/{id}")
  @Operation(summary = "Update User", description = "Updates an existing user with the provided details.")
  @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(schema = @Schema(implementation = User.class)))
  @ApiResponse(responseCode = "400", description = "Invalid request parameters or body", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<User> updateUser(@Valid @RequestBody
                                         @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User details to update", required = true, content = @Content(schema = @Schema(implementation = UserCreateDto.class)))
                                         UserCreateDto userCreateDto,
                                         @Parameter(description = "ID of the user to update", required = true)
                                         @Positive @PathVariable Long id) {
    return ResponseEntity.ok(userService.updateUser(userCreateDto, id));
  }

  /**
   * <h3>Delete User</h3>
   * <p>Deletes a user with the specified ID.</p>
   *
   * @param id the ID of the user to delete
   * @return empty response with NO_CONTENT status
   */
  @DeleteMapping("/users/{id}")
  @Operation(summary = "Delete User", description = "Deletes a user with the specified ID.")
  @ApiResponse(responseCode = "204", description = "User deleted successfully")
  @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Void> deleteUser(
      @Parameter(description = "ID of the user to delete", required = true) @Valid @PathVariable
      Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * <h3>Get All Users</h3>
   * <p>Retrieves a list of all users in the system.</p>
   *
   * @return list of all {@link User} entities in a paginated response
   */
  @GetMapping("/users")
  @Operation(summary = "Get All Users", description = "Retrieves a paginated list of all users in the system.")
  @ApiResponse(responseCode = "200", description = "A paginated list of users", content = @Content(schema = @Schema(implementation = Page.class, subTypes = {
      User.class})))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Page<User>> getAllUsers(
      @Parameter(description = "Pagination information (page number, size, sort)")
      Pageable pageable) {
    return ResponseEntity.ok(userService.getAll(pageable));
  }

  /**
   * <h3>Create Category</h3>
   * <p>Creates a new category with the provided details.</p>
   *
   * @param categoryRequestDto the category data to create
   * @return the created {@link Category} with HTTP status 201 (CREATED)
   */
  @PostMapping("/category")
  @Operation(summary = "Create Category", description = "Creates a new category with the provided details.")
  @ApiResponse(responseCode = "201", description = "Category created successfully", content = @Content(schema = @Schema(implementation = Category.class)))
  @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Category> createCategory(@Valid @RequestBody
                                                 @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Category details to create", required = true, content = @Content(schema = @Schema(implementation = CategoryRequestDto.class)))
                                                 CategoryRequestDto categoryRequestDto) {
    return ResponseEntity.status(201).body(categoryService.create(categoryRequestDto));
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
  @Operation(summary = "Update Category", description = "Updates an existing category with the provided details.")
  @ApiResponse(responseCode = "200", description = "Category updated successfully", content = @Content(schema = @Schema(implementation = Category.class)))
  @ApiResponse(responseCode = "400", description = "Invalid request parameters or body", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Category> updateCategory(@Valid @RequestBody
                                                 @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Category details to update", required = true, content = @Content(schema = @Schema(implementation = CategoryRequestDto.class)))
                                                 CategoryRequestDto categoryRequestDto,
                                                 @Parameter(description = "ID of the category to update", required = true)
                                                 @Positive @PathVariable Long id) {
    return ResponseEntity.ok(categoryService.update(categoryRequestDto, id));
  }

  /**
   * <h3>Delete Category</h3>
   * <p>Deletes a category with the specified ID.</p>
   *
   * @param id the ID of the category to delete
   * @return empty response with NO_CONTENT status
   */
  @DeleteMapping("/category/{id}")
  @Operation(summary = "Delete Category", description = "Deletes a category with the specified ID.")
  @ApiResponse(responseCode = "204", description = "Category deleted successfully")
  @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Void> deleteCategory(
      @Parameter(description = "ID of the category to delete", required = true) @Positive
      @PathVariable Long id) {
    categoryService.delete(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * <h3>Delete item</h3>
   * <p>Deletes an item with the specified ID.</p>
   *
   * @param id the ID of the item to delete
   * @return empty response with NO_CONTENT status
   */
  @DeleteMapping("/item/{id}")
  @Operation(summary = "Delete Item (Admin)", description = "Deletes an item with the specified ID (admin privilege required).")
  @ApiResponse(responseCode = "204", description = "Item deleted successfully")
  @ApiResponse(responseCode = "404", description = "Item not found", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Void> deleteItem(
      @Parameter(description = "ID of the item to delete", required = true) @Positive @PathVariable
      Long id) {
    itemService.adminDeleteItem(id);
    return ResponseEntity.noContent().build();
  }
}