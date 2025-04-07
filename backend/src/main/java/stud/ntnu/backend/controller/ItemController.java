package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.item.ItemCreateDto;
import stud.ntnu.backend.data.item.ItemPreviewDto;
import stud.ntnu.backend.data.item.ItemDetailsDto;
import stud.ntnu.backend.data.item.RecommendedItemsRequestDto;
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

  @PostMapping
  public ResponseEntity<ItemDetailsDto> createItem(@Valid ItemCreateDto requestDto,
                                                   Principal principal) {
    return ResponseEntity.ok(
        itemService.createItem(userService.getCurrentUser(principal), requestDto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ItemDetailsDto> updateItem(@Valid ItemCreateDto requestDto,
                                                   @Positive @PathVariable Long id,
                                                   Principal principal) {
    return ResponseEntity.ok(
        itemService.updateItem(userService.getCurrentUser(principal), requestDto, id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteItem(@Positive @PathVariable Long id, Principal principal) {
    itemService.deleteItem(userService.getCurrentUser(principal), id);
    return ResponseEntity.ok().build();
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
    return ResponseEntity.ok(itemService.getItemDetailsById(id));
  }

  /**
   * <h3>Get Items By Category</h3>
   * <p>Retrieves items belonging to a specific category.</p>
   *
   * @param categoryId the category ID
   * @return list of {@link ItemPreviewDto} in the category
   */
  @GetMapping("/category/{categoryId}")
  public ResponseEntity<Page<ItemPreviewDto>> getItemsByCategory(
      @Positive @PathVariable Long categoryId, Pageable pageable) {
    return ResponseEntity.ok(itemService.getItemsByCategoryId(categoryId, pageable));
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
    itemService.recordView(id, userService.getCurrentUser(principal));
    return ResponseEntity.noContent().build();
  }

  /**
   * <h3>Get Recommended Items</h3>
   * <p>Retrieves recommended items based on distribution.</p>
   *
   * @param requestDto the recommendation request parameters
   * @return list of recommended {@link ItemPreviewDto}
   */
  @PostMapping("/view/recommended_items")
  public ResponseEntity<List<ItemPreviewDto>> getRecommendedItems(
      @Valid @RequestBody RecommendedItemsRequestDto requestDto) {
    return ResponseEntity.ok(
        itemService.getItemsByDistribution(requestDto.getDistribution(), requestDto.getLimit())
    );
  }
}