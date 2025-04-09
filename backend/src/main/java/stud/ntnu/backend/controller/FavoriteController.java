package stud.ntnu.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.item.ItemPreviewDto;
import stud.ntnu.backend.service.FavoriteService;
import stud.ntnu.backend.service.ItemService;
import stud.ntnu.backend.service.UserService;

/**
 * <h2>FavoriteController</h2>
 * <p>Controller for managing user favorites operations.</p>
 */
@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "Operations for managing user's favorite items.")
public class FavoriteController {

  /**
   * <h3>Favorite Service</h3>
   * <p>Service handling favorite-related operations.</p>
   *
   * @see FavoriteService
   */
  private final FavoriteService favoriteService;

  /**
   * <h3>User Service</h3>
   * <p>Service handling user-related operations.</p>
   *
   * @see UserService
   */
  private final UserService userService;

  /**
   * <h3>Item Service</h3>
   * <p>Service handling item-related operations.</p>
   */
  private final ItemService itemService;

  /**
   * <h3>Add Favorite</h3>
   * <p>Adds an item to the current user's favorites.</p>
   *
   * @param itemId    the ID of the item to favorite
   * @param principal the authenticated user
   * @return {@link ResponseEntity} with status NO_CONTENT if the item was successfully added to favorites
   */
  @PostMapping("/{itemId}")
  @Operation(summary = "Add Item to Favorites", description = "Adds a specific item to the authenticated user's favorites.")
  @ApiResponse(responseCode = "204", description = "Item added to favorites successfully")
  @ApiResponse(responseCode = "400", description = "Invalid item ID", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "404", description = "Item not found", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Void> addFavorite(
      @Parameter(description = "ID of the item to add to favorites", required = true) @Positive
      @PathVariable long itemId, @Parameter(hidden = true) Principal principal) {
    favoriteService.add(userService.getCurrentUser(principal), itemId);
    return ResponseEntity.noContent().build();
  }

  /**
   * <h3>Remove Favorite</h3>
   * <p>Removes an item from the current user's favorites.</p>
   *
   * @param itemId    the ID of the item to remove
   * @param principal the authenticated user
   * @return {@link ResponseEntity} with status NO_CONTENT if the item was successfully removed from favorites
   */
  @DeleteMapping("/{itemId}")
  @Operation(summary = "Remove Item from Favorites", description = "Removes a specific item from the authenticated user's favorites.")
  @ApiResponse(responseCode = "204", description = "Item removed from favorites successfully")
  @ApiResponse(responseCode = "400", description = "Invalid item ID", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "404", description = "Item not found in favorites", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Void> removeFavorite(
      @Parameter(description = "ID of the item to remove from favorites", required = true) @Positive
      @PathVariable long itemId, @Parameter(hidden = true) Principal principal) {
    favoriteService.delete(userService.getCurrentUserId(principal), itemId);
    return ResponseEntity.noContent().build();
  }

  /**
   * <h3>Get Favorites</h3>
   * <p>Retrieves all favorites for the current user.</p>
   *
   * @param principal the authenticated user
   * @param pageable  pagination information
   * @return {@link ResponseEntity} containing a paginated list of {@link ItemPreviewDto} for the user's favorites
   */
  @GetMapping
  @Operation(summary = "Get User's Favorite Items", description = "Retrieves a paginated list of items that the authenticated user has marked as favorites.")
  @ApiResponse(responseCode = "200", description = "List of favorite items", content = @Content(schema = @Schema(implementation = Page.class, subTypes = {
      ItemPreviewDto.class})))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Page<ItemPreviewDto>> getFavorites(
      @Parameter(hidden = true) Principal principal,
      @Parameter(description = "Pagination information (page number, size, sort)")
      Pageable pageable) {
    return ResponseEntity.ok(
        itemService.getFavoritesByUserId(userService.getCurrentUserId(principal), pageable));
  }

  /**
   * <h3>Get Favorite Count</h3>
   * <p>Retrieves the count of favorites for a specific item.</p>
   *
   * @param itemId the ID of the item to check
   * @return {@link ResponseEntity} containing the count of users who have favorited the item
   */
  @GetMapping("/count/{itemId}")
  @Operation(summary = "Get Favorite Count for Item", description = "Retrieves the number of users who have added a specific item to their favorites.")
  @ApiResponse(responseCode = "200", description = "Favorite count for the item", content = @Content(schema = @Schema(implementation = Long.class)))
  @ApiResponse(responseCode = "400", description = "Invalid item ID", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "404", description = "Item not found", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Long> getFavoriteCountByItem(
      @Parameter(description = "ID of the item to get the favorite count for", required = true)
      @Positive @PathVariable long itemId) {
    return ResponseEntity.ok(favoriteService.countByItemId(itemId));
  }
}