package stud.ntnu.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.item.ItemCreateDto;
import stud.ntnu.backend.data.item.ItemDetailsDto;
import stud.ntnu.backend.data.item.ItemPreviewDto;
import stud.ntnu.backend.data.item.ItemSearchDto;
import stud.ntnu.backend.data.item.RecommendedItemsRequestDto;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.service.ItemService;
import stud.ntnu.backend.service.UserService;


/**
 * <h2>ItemController</h2>
 * <p>Controller for item management operations.</p>
 */
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "Items", description = "Operations for managing and retrieving marketplace items.")
public class ItemController {

  /**
   * <h3>Item Service</h3>
   * <p>Service handling item-related operations.</p>
   *
   * @see ItemService
   */
  private final ItemService itemService;

  /**
   * <h3>User Service</h3>
   * <p>Service handling user-related operations.</p>
   *
   * @see UserService
   */
  private final UserService userService;

  /**
   * Logger instance for the ItemController class.
   */
  private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

  /**
   * <h3>Create Item</h3>
   * <p>Creates a new item listed by the currently authenticated user.</p>
   *
   * @param requestDto the details of the item to create
   * @param principal  the authenticated user
   * @return {@link ResponseEntity} containing the ID of the created item
   */
  @PostMapping
  @Operation(summary = "Create Item", description = "Creates a new item listed by the authenticated user.")
  @ApiResponse(responseCode = "201", description = "Item created successfully", content = @Content(schema = @Schema(implementation = Long.class)))
  @ApiResponse(responseCode = "400", description = "Invalid item details")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<Long> createItem(@Valid @RequestBody
                                         @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Details of the item to create", required = true, content = @Content(schema = @Schema(implementation = ItemCreateDto.class)))
                                         ItemCreateDto requestDto,
                                         @Parameter(hidden = true) Principal principal) {
    logger.info("Received request to create item");
    User currentUser = userService.getCurrentUser(principal);
    ItemDetailsDto createdItem = itemService.createItem(currentUser, requestDto);
    logger.info("Item created with ID: {}", createdItem.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdItem.getId());
  }

  /**
   * <h3>Update Item</h3>
   * <p>Updates an existing item with the provided details.</p>
   *
   * @param requestDto the updated details of the item
   * @param id         the ID of the item to update
   * @param principal  the authenticated user
   * @return {@link ResponseEntity} containing the updated {@link ItemDetailsDto}
   */
  @PutMapping("/{id}")
  @Operation(summary = "Update Item", description = "Updates an existing item with the provided details.")
  @ApiResponse(responseCode = "200", description = "Item updated successfully", content = @Content(schema = @Schema(implementation = ItemDetailsDto.class)))
  @ApiResponse(responseCode = "400", description = "Invalid item details")
  @ApiResponse(responseCode = "403", description = "User not authorized to update this item")
  @ApiResponse(responseCode = "404", description = "Item not found")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<ItemDetailsDto> updateItem(@Valid @RequestBody
                                                   @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated details of the item", required = true, content = @Content(schema = @Schema(implementation = ItemCreateDto.class)))
                                                   ItemCreateDto requestDto,
                                                   @Parameter(description = "ID of the item to update", required = true)
                                                   @Positive @PathVariable Long id,
                                                   @Parameter(hidden = true) Principal principal) {
    logger.info("Received request to update item ID: {}", id);
    User currentUser = userService.getCurrentUser(principal);
    ItemDetailsDto updatedItem = itemService.updateItem(currentUser, requestDto, id);
    logger.info("Item updated successfully for ID: {}", id);
    return ResponseEntity.ok(updatedItem);
  }

  /**
   * <h3>Delete Item</h3>
   * <p>Deletes a specific item.</p>
   *
   * @param id        the ID of the item to delete
   * @param principal the authenticated user
   * @return {@link ResponseEntity} with status OK if the item was successfully deleted
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete Item", description = "Deletes a specific item.")
  @ApiResponse(responseCode = "200", description = "Item deleted successfully")
  @ApiResponse(responseCode = "403", description = "User not authorized to delete this item")
  @ApiResponse(responseCode = "404", description = "Item not found")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<Void> deleteItem(
      @Parameter(description = "ID of the item to delete", required = true) @Positive @PathVariable
      Long id, @Parameter(hidden = true) Principal principal) {
    logger.info("Received request to delete item ID: {}", id);
    User currentUser = userService.getCurrentUser(principal);
    itemService.deleteItem(currentUser, id);
    logger.info("Item deleted successfully for ID: {}", id);
    return ResponseEntity.ok().build();
  }

  /**
   * <h3>Search Items</h3>
   * <p>Searches for items based on the provided criteria.</p>
   *
   * @param searchDto the search criteria
   * @param pageable  pagination information
   * @return {@link ResponseEntity} containing a paginated list of {@link ItemPreviewDto} matching the search criteria
   */
  @GetMapping("/search")
  @Operation(summary = "Search Items", description = "Searches for items based on the provided criteria.")
  @ApiResponse(responseCode = "200", description = "List of items matching the search criteria", content = @Content(schema = @Schema(implementation = Page.class, subTypes = {
      ItemPreviewDto.class})))
  @ApiResponse(responseCode = "400", description = "Invalid search criteria")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<Page<ItemPreviewDto>> searchItems(
      @Valid @ModelAttribute @Parameter(description = "Search criteria") ItemSearchDto searchDto,
      @Parameter(description = "Pagination information (page number, size, sort)")
      Pageable pageable) {
    return ResponseEntity.ok(itemService.searchItems(searchDto, pageable));
  }

  /**
   * <h3>Get Paged Items</h3>
   * <p>Retrieves a paginated list of item previews.</p>
   *
   * @param pageable pagination information
   * @return {@link ResponseEntity} containing a paginated list of {@link ItemPreviewDto}
   */
  @GetMapping("/page")
  @Operation(summary = "Get Paged Items", description = "Retrieves a paginated list of item previews.")
  @ApiResponse(responseCode = "200", description = "Paginated list of item previews", content = @Content(schema = @Schema(implementation = Page.class, subTypes = {
      ItemPreviewDto.class})))
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<Page<ItemPreviewDto>> getPagedItems(
      @Parameter(description = "Pagination information (page number, size, sort)")
      Pageable pageable) {
    return ResponseEntity.ok(itemService.getAllItemPreviews(pageable));
  }

  /**
   * <h3>Get All Items</h3>
   * <p>Retrieves preview information for all items in a paginated manner.</p>
   *
   * @param pageable pagination information
   * @return {@link ResponseEntity} containing a paginated list of {@link ItemPreviewDto}
   */
  @GetMapping("/all")
  @Operation(summary = "Get All Items (Paged)", description = "Retrieves a paginated list of preview information for all items.")
  @ApiResponse(responseCode = "200", description = "Paginated list of all item previews", content = @Content(schema = @Schema(implementation = Page.class, subTypes = {
      ItemPreviewDto.class})))
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<Page<ItemPreviewDto>> getAllItems(
      @Parameter(description = "Pagination information (page number, size, sort)")
      Pageable pageable) {
    return ResponseEntity.ok(itemService.getAllItemPreviews(pageable));
  }

  /**
   * <h3>Get Item Details</h3>
   * <p>Retrieves detailed information for a specific item.</p>
   *
   * @param id the ID of the item
   * @return {@link ResponseEntity} containing the {@link ItemDetailsDto} for the requested item
   */
  @GetMapping("/details/{id}")
  @Operation(summary = "Get Item Details", description = "Retrieves detailed information for a specific item.")
  @ApiResponse(responseCode = "200", description = "Details of the requested item", content = @Content(schema = @Schema(implementation = ItemDetailsDto.class)))
  @ApiResponse(responseCode = "400", description = "Invalid item ID")
  @ApiResponse(responseCode = "404", description = "Item not found")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<ItemDetailsDto> getItemDetails(
      @Parameter(description = "ID of the item to retrieve details for", required = true) @Positive
      @PathVariable Long id) {
    logger.info("Received request for item details ID: {}", id);
    ItemDetailsDto itemDetails = itemService.getItemDetailsById(id);
    logger.info("Returning details for item ID: {}", id);
    return ResponseEntity.ok(itemDetails);
  }

  /**
   * <h3>Get Paged Items by Category</h3>
   * <p>Retrieves a paginated list of item previews for a specific category.</p>
   *
   * @param categoryId the ID of the category
   * @param pageable   pagination information
   * @return {@link ResponseEntity} containing a paginated list of {@link ItemPreviewDto} in the specified category
   */
  @GetMapping("/category/{categoryId}/page")
  @Operation(summary = "Get Paged Items by Category", description = "Retrieves a paginated list of item previews for a specific category.")
  @ApiResponse(responseCode = "200", description = "Paginated list of items in the category", content = @Content(schema = @Schema(implementation = Page.class, subTypes = {
      ItemPreviewDto.class})))
  @ApiResponse(responseCode = "400", description = "Invalid category ID")
  @ApiResponse(responseCode = "404", description = "Category not found")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<Page<ItemPreviewDto>> getPagedItemsByCategory(
      @Parameter(description = "ID of the category to retrieve items from", required = true)
      @Positive @PathVariable Long categoryId,
      @Parameter(description = "Pagination information (page number, size, sort)")
      Pageable pageable) {
    Page<ItemPreviewDto> pagedItems = itemService.getItemsByCategoryId(categoryId, pageable);
    return ResponseEntity.ok(pagedItems);
  }

  /**
   * <h3>Get My Items</h3>
   * <p>Retrieves items listed by the currently authenticated user in a paginated manner.</p>
   *
   * @param principal the authenticated user
   * @param pageable  pagination information
   * @return {@link ResponseEntity} containing a paginated list of {@link ItemPreviewDto} listed by the user
   */
  @GetMapping("/my")
  @Operation(summary = "Get My Items (Paged)", description = "Retrieves a paginated list of items listed by the authenticated user.")
  @ApiResponse(responseCode = "200", description = "Paginated list of user's items", content = @Content(schema = @Schema(implementation = Page.class, subTypes = {
      ItemPreviewDto.class})))
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<Page<ItemPreviewDto>> getMyItems(
      @Parameter(hidden = true) Principal principal,
      @Parameter(description = "Pagination information (page number, size, sort)")
      Pageable pageable) {
    User currentUser = userService.getCurrentUser(principal);
    Page<ItemPreviewDto> items = itemService.getItemsBySellerId(currentUser.getId(), pageable);
    return ResponseEntity.ok(items);
  }

  /**
   * <h3>Record Item View</h3>
   * <p>Records when a user views an item.</p>
   *
   * @param id        the ID of the viewed item
   * @param principal the authenticated user
   * @return {@link ResponseEntity} with no content status upon successful recording
   */
  @PostMapping("/view/post/{id}")
  @Operation(summary = "Record Item View", description = "Records when the authenticated user views a specific item.")
  @ApiResponse(responseCode = "204", description = "Item view recorded successfully")
  @ApiResponse(responseCode = "400", description = "Invalid item ID")
  @ApiResponse(responseCode = "404", description = "Item not found")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<Void> recordItemView(
      @Parameter(description = "ID of the item being viewed", required = true) @Positive
      @PathVariable Long id, @Parameter(hidden = true) Principal principal) {
    logger.info("Received request to record view for item ID: {}", id);
    User currentUser = userService.getCurrentUser(principal);
    itemService.recordView(id, currentUser);
    logger.info("View recorded successfully for item ID: {} by user ID: {}", id,
        currentUser.getId());
    return ResponseEntity.noContent().build();
  }

  /**
   * <h3>Get Recommended Items</h3>
   * <p>Retrieves recommended items based on the provided distribution criteria.</p>
   *
   * @param requestDto the recommendation request parameters including the distribution type and optional limit
   * @param pageable   the pagination and sorting information
   * @return {@link ResponseEntity} containing a paginated list of recommended {@link ItemPreviewDto}
   */
  @PostMapping("/view/recommended_items")
  @Operation(summary = "Get Recommended Items", description = "Retrieves recommended items based on the specified distribution.")
  @ApiResponse(responseCode = "200", description = "List of recommended items", content = @Content(schema = @Schema(implementation = Page.class, subTypes = {
      ItemPreviewDto.class})))
  @ApiResponse(responseCode = "400", description = "Invalid recommendation request")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<Page<ItemPreviewDto>> getRecommendedItems(@Valid @RequestBody
                                                                  @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Recommendation request parameters", required = true, content = @Content(schema = @Schema(implementation = RecommendedItemsRequestDto.class)))
                                                                  RecommendedItemsRequestDto requestDto,
                                                                  @Parameter(description = "Pagination information (page number, size, sort)")
                                                                  Pageable pageable) {
    logger.info("Received request for recommended items with pageable: {}", pageable);
    Integer limit = requestDto.getLimit();
    Page<ItemPreviewDto> items =
        itemService.getItemsByDistribution(requestDto.getDistribution(), pageable, limit);
    logger.info("Returning {} recommended items", items.getNumberOfElements());
    return ResponseEntity.ok(items);
  }
}