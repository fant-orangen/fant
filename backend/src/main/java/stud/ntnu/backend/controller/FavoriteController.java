package stud.ntnu.backend.controller;

import jakarta.validation.constraints.Positive;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.FavoriteResponseDto;
import stud.ntnu.backend.service.FavoriteService;
import stud.ntnu.backend.service.ItemService;
import stud.ntnu.backend.service.UserService;

@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController {

  private final FavoriteService favoriteService;
  private final UserService userService;
  private final ItemService itemService;

  @PostMapping("/{itemId}")
  public ResponseEntity<Void> addFavorite(@Positive @PathVariable long itemId,
                                          Principal principal) {
    favoriteService.add(userService.getCurrentUserId(principal), itemId);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{itemId}")
  public ResponseEntity<Void> removeFavorite(@Positive @PathVariable long itemId,
                                             Principal principal) {
    favoriteService.delete(userService.getCurrentUserId(principal), itemId);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<List<FavoriteResponseDto>> getFavorites(Principal principal) {
    return ResponseEntity.ok(
        favoriteService.getFavoritesByUserId(userService.getCurrentUserId(principal)));
  }

  @GetMapping("/{itemId}")
  public ResponseEntity<Long> getFavoriteCountByItem(@Positive @PathVariable long itemId) {
    return ResponseEntity.ok(favoriteService.countByItemId(itemId));
  }
}
