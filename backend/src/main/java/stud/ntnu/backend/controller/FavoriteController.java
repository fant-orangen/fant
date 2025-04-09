package stud.ntnu.backend.controller;

import jakarta.validation.constraints.Positive;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.favorite.FavoriteResponseDto;
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

  private final ItemService itemService;

  /**
   * <h3>Add Favorite</h3>
   * <p>Adds an item to the current user's favorites.</p>
   *
   * @param itemId    the ID of the item to favorite
   * @param principal the authenticated user
   * @return empty response with OK status
   */
  @PostMapping("/{itemId}")
  public ResponseEntity<Void> addFavorite(@Positive @PathVariable long itemId,
                                          Principal principal) {
    favoriteService.add(userService.getCurrentUser(principal), itemId);
    return ResponseEntity.ok().build();
  }

  /**
   * <h3>Remove Favorite</h3>
   * <p>Removes an item from the current user's favorites.</p>
   *
   * @param itemId    the ID of the item to remove
   * @param principal the authenticated user
   * @return empty response with OK status
   */
  @DeleteMapping("/{itemId}")
  public ResponseEntity<Void> removeFavorite(@Positive @PathVariable long itemId,
                                             Principal principal) {
    favoriteService.delete(userService.getCurrentUserId(principal), itemId);
    return ResponseEntity.ok().build();
  }

  /**
   * <h3>Get Favorites</h3>
   * <p>Retrieves all favorites for the current user.</p>
   *
   * @param principal the authenticated user
   * @return list of {@link FavoriteResponseDto} for the user
   */
  @GetMapping
  public ResponseEntity<Page<ItemPreviewDto>> getFavorites(Principal principal, Pageable pageable) {
    return ResponseEntity.ok(
        itemService.getFavoritesByUserId(userService.getCurrentUserId(principal), pageable));
  }

  /**
   * <h3>Get Favorite Count</h3>
   * <p>Retrieves the count of favorites for a specific item.</p>
   *
   * @param itemId the ID of the item to check
   * @return the favorite count for the item
   */
  @GetMapping("/count/{itemId}")
  public ResponseEntity<Long> getFavoriteCountByItem(@Positive @PathVariable long itemId) {
    return ResponseEntity.ok(favoriteService.countByItemId(itemId));
  }

  /**
   * <h3>Check Favorite Status</h3>
   * <p>Checks if the current user has favorited a specific item.</p>
   *
   * @param itemId    the ID of the item to check
   * @param principal the authenticated user
   * @return true if favorited, false otherwise
   */
  @GetMapping("/status/{itemId}")
  public ResponseEntity<Boolean> isFavorite(@PathVariable long itemId, Principal principal) {
    boolean isFav = favoriteService.checkFavoriteStatus(userService.getCurrentUserId(principal), itemId);
    return ResponseEntity.ok(isFav);
  }


}
