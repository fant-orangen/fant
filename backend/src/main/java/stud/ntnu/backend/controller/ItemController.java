package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.item.ItemCreateDto;
import stud.ntnu.backend.data.item.ItemPreviewDto;
import stud.ntnu.backend.data.item.ItemDetailsDto;
import stud.ntnu.backend.data.item.ItemSearchDto;
import stud.ntnu.backend.data.item.RecommendedItemsRequestDto;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.service.ItemService;
import stud.ntnu.backend.service.UserService;

import java.util.List;

/**
 * <h2>ItemController</h2>
 * <p>Controller for item management operations.</p>
 */
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
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

  // Logger instance - Ensure this line exists
  private static final Logger logger = LoggerFactory.getLogger(ItemController.class);


  @PostMapping
  public ResponseEntity<Long> createItem(@Valid @RequestBody ItemCreateDto requestDto,
                                         Principal principal) {
    logger.info("Received request to create item");
    User currentUser = userService.getCurrentUser(principal);
    ItemDetailsDto createdItem = itemService.createItem(currentUser, requestDto);
    logger.info("Item created with ID: {}",
        createdItem.getId()); // TODO: simplify this code per the new requirements of just id
    return ResponseEntity.status(HttpStatus.CREATED).body(createdItem.getId());
  }

  @PutMapping("/{id}")
  public ResponseEntity<ItemDetailsDto> updateItem(@Valid @RequestBody ItemCreateDto requestDto,
                                                   @Positive @PathVariable Long id,
                                                   Principal principal) {
    logger.info("Received request to update item ID: {}", id);
    User currentUser = userService.getCurrentUser(principal);
    ItemDetailsDto updatedItem = itemService.updateItem(currentUser, requestDto, id);
    logger.info("Item updated successfully for ID: {}", id);
    return ResponseEntity.ok(updatedItem);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteItem(@Positive @PathVariable Long id, Principal principal) {
    logger.info("Received request to delete item ID: {}", id);
    User currentUser = userService.getCurrentUser(principal);
    itemService.deleteItem(currentUser, id);
    logger.info("Item deleted successfully for ID: {}", id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/search")
  public ResponseEntity<Page<ItemPreviewDto>> searchItems(
      @Valid @ModelAttribute ItemSearchDto searchDto, Pageable pageable) {
    return ResponseEntity.ok(itemService.searchItems(searchDto, pageable));
  }

  @GetMapping("/page")
  public ResponseEntity<Page<ItemPreviewDto>> getPagedItems(Pageable pageable) {
    return ResponseEntity.ok(itemService.getAllItemPreviews(pageable));
  }

  /**
   * <h3>Get All Items</h3>
   * <p>Retrieves preview information for all items.</p>
   *
   * @return list of {@link ItemPreviewDto}
   */
  @GetMapping("/all")
  public ResponseEntity<Page<ItemPreviewDto>> getAllItems(Pageable pageable) {
    return ResponseEntity.ok(itemService.getAllItemPreviews(pageable));
  }

  /**
   * <h3>Get Item Details</h3>
   * <p>Retrieves detailed information for a specific item.</p>
   *
   * @param id the ID of the item
   * @return {@link ItemDetailsDto} for the requested item
   */
  @GetMapping("/details/{id}")
  public ResponseEntity<ItemDetailsDto> getItemDetails(@Positive @PathVariable Long id) {
    logger.info("Received request for item details ID: {}", id);
    ItemDetailsDto itemDetails = itemService.getItemDetailsById(id);
    logger.info("Returning details for item ID: {}", id);
    return ResponseEntity.ok(itemDetails);
  }

  @GetMapping("/category/{categoryId}/page")
  public ResponseEntity<Page<ItemPreviewDto>> getPagedItemsByCategory(
      @Positive @PathVariable Long categoryId,
      Pageable pageable) {
    Page<ItemPreviewDto> pagedItems = itemService.getItemsByCategoryId(categoryId, pageable);
    return ResponseEntity.ok(pagedItems);
  }

  /**
   * <h3>Get My Items</h3>
   * <p>Retrieves items listed by the currently authenticated user.</p>
   *
   * @param principal the authenticated user
   * @return list of {@link ItemPreviewDto} listed by the user
   */
  @GetMapping("/my")
  public ResponseEntity<Page<ItemPreviewDto>> getMyItems(Principal principal, Pageable pageable) {
    // Use the imported User class here
    User currentUser = userService.getCurrentUser(principal);
    Page<ItemPreviewDto> items = itemService.getItemsBySellerId(currentUser.getId(), pageable);
    // Use the declared logger variable here
    return ResponseEntity.ok(items);
  }

  /**
   * <h3>Record Item View</h3>
   * <p>Records when a user views an item.</p>
   *
   * @param id        the ID of the viewed item
   * @param principal the authenticated user
   * @return empty response with no content status
   */
  @PostMapping("/view/post/{id}")
  public ResponseEntity<Void> recordItemView(@Positive @PathVariable Long id, Principal principal) {
    logger.info("Received request to record view for item ID: {}", id);
    User currentUser = userService.getCurrentUser(principal);
    itemService.recordView(id, currentUser);
    logger.info("View recorded successfully for item ID: {} by user ID: {}", id,
        currentUser.getId());
    return ResponseEntity.noContent().build();
  }

  /**
   * <h3>Get Recommended Items</h3>
   * <p>Retrieves recommended items based on distribution.</p>
   *
   * @param requestDto the recommendation request parameters
   * @param pageable   the pagination and sorting information
   * @return paginated response of recommended {@link ItemPreviewDto}
   */
  @PostMapping("/view/recommended_items")
  public ResponseEntity<Page<ItemPreviewDto>> getRecommendedItems(
      @Valid @RequestBody RecommendedItemsRequestDto requestDto,
      Pageable pageable) {
    logger.info("Received request for recommended items with pageable: {}", pageable);
    Page<ItemPreviewDto> items = itemService.getItemsByDistribution(requestDto.getDistribution(),
        pageable);
    logger.info("Returning {} recommended items", items.getNumberOfElements());
    return ResponseEntity.ok(items);
  }
}