package stud.ntnu.backend.controller;

import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.FavoriteResponseDto;
import stud.ntnu.backend.model.Favorite;
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

  @PostMapping
  public ResponseEntity<FavoriteResponseDto> addFavorite(@RequestParam long id, Principal principal) {
    Favorite favorite = new Favorite();
    favorite.setItem(itemService.findById(id));
    favorite.setUser(userService.getCurrentUser(principal));
    return ResponseEntity.ok(favoriteService.save(favorite));
  }

  @DeleteMapping
  public ResponseEntity<Void> removeFavorite(@RequestParam long id) {
    favoriteService.delete(favoriteService.findById(id));
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/user")
  public ResponseEntity<List<FavoriteResponseDto>> getFavoritesByUser(@RequestParam long userId) {
    return ResponseEntity.ok(favoriteService.findByUserId(userId));
  }

  @GetMapping("/item")
  public ResponseEntity<List<FavoriteResponseDto>> getFavoritesByItem(@RequestParam long itemId) {
    return ResponseEntity.ok(favoriteService.findByItemId(itemId));
  }
}
