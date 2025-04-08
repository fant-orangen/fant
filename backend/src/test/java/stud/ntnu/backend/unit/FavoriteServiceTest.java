package stud.ntnu.backend.unit;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stud.ntnu.backend.data.favorite.FavoriteResponseDto;
import stud.ntnu.backend.exception.AlreadyFavoritedException;
import stud.ntnu.backend.model.Favorite;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.FavoriteRepository;
import stud.ntnu.backend.repository.ItemRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import stud.ntnu.backend.service.FavoriteService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavoriteServiceTest {

  private FavoriteRepository favoriteRepository;
  private ItemRepository itemRepository;
  private FavoriteService favoriteService;

  @BeforeEach
  void setUp() {
    favoriteRepository = mock(FavoriteRepository.class);
    itemRepository = mock(ItemRepository.class);
    favoriteService = new FavoriteService(favoriteRepository, itemRepository);
  }

  @Test
  void add_shouldSaveFavorite() {
    User user = new User();
    user.setId(1L);
    Item item = new Item();
    item.setId(2L);

    when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
    when(favoriteRepository.existsByUserIdAndItemId(1L, 2L)).thenReturn(false);

    favoriteService.add(user, 2L);

    verify(favoriteRepository).save(argThat(fav ->
        fav.getUser().equals(user) && fav.getItem().equals(item)
    ));
  }

  @Test
  void add_shouldThrowIfItemNotFound() {
    User user = new User();
    user.setId(1L);

    when(itemRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> favoriteService.add(user, 99L));
  }

  @Test
  void add_shouldThrowIfAlreadyFavorited() {
    User user = new User();
    user.setId(1L);
    Item item = new Item();
    item.setId(2L);

    when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
    when(favoriteRepository.existsByUserIdAndItemId(1L, 2L)).thenReturn(true);

    assertThrows(AlreadyFavoritedException.class, () -> favoriteService.add(user, 2L));
  }

  @Test
  void delete_shouldRemoveFavorite() {
    Long userId = 1L;
    Long itemId = 2L;
    Favorite favorite = new Favorite();

    when(favoriteRepository.findByUserIdAndItemId(userId, itemId)).thenReturn(Optional.of(favorite));

    favoriteService.delete(userId, itemId);

    verify(favoriteRepository).delete(favorite);
  }

  @Test
  void delete_shouldThrowIfFavoriteNotFound() {
    when(favoriteRepository.findByUserIdAndItemId(1L, 2L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> favoriteService.delete(1L, 2L));
  }

  @Test
  void countByItemId_shouldReturnCount() {
    when(favoriteRepository.countByItemId(42L)).thenReturn(5L);

    Long result = favoriteService.countByItemId(42L);

    assertEquals(5L, result);
  }

  @Test
  void toDto_shouldMapCorrectly() {
    Item item = new Item();
    item.setId(7L);
    LocalDateTime createdAt = LocalDateTime.now();

    Favorite favorite = new Favorite();
    favorite.setItem(item);
    favorite.setCreatedAt(createdAt);

    FavoriteResponseDto dto = favoriteService.toDto(favorite);

    assertEquals(7L, dto.getItemId());
    assertEquals(createdAt, dto.getCreatedAt());
  }
}
