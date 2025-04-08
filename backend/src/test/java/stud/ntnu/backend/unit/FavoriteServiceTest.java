package stud.ntnu.backend.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stud.ntnu.backend.exception.AlreadyFavoritedException;
import stud.ntnu.backend.model.Favorite;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.FavoriteRepository;
import stud.ntnu.backend.repository.ItemRepository;
import stud.ntnu.backend.service.FavoriteService;

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTest {

  @Mock
  private FavoriteRepository favoriteRepository;

  @Mock
  private ItemRepository itemRepository;

  @InjectMocks
  private FavoriteService favoriteService;

  private User user;
  private Item item;
  private Favorite favorite;

  @BeforeEach
  void setUp() {
    user = User.builder()
        .id(1L)
        .displayName("Test User")
        .build();

    item = Item.builder()
        .id(1L)
        .briefDescription("Test Item")
        .build();

    favorite = Favorite.builder()
        .id(1L)
        .user(user)
        .item(item)
        .createdAt(LocalDateTime.now())
        .build();
  }

  @Test
  void add_NewFavorite_Success() {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
    when(favoriteRepository.existsByUserIdAndItemId(anyLong(), anyLong())).thenReturn(false);
    when(favoriteRepository.save(any(Favorite.class))).thenReturn(favorite);

    // Act
    favoriteService.add(user, item.getId());

    // Assert
    verify(favoriteRepository).save(any(Favorite.class));
  }

  @Test
  void add_ItemNotFound_ThrowsException() {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> favoriteService.add(user, item.getId()));
    verify(favoriteRepository, never()).save(any(Favorite.class));
  }

  @Test
  void add_AlreadyFavorited_ThrowsException() {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
    when(favoriteRepository.existsByUserIdAndItemId(anyLong(), anyLong())).thenReturn(true);

    // Act & Assert
    assertThrows(AlreadyFavoritedException.class, () -> favoriteService.add(user, item.getId()));
    verify(favoriteRepository, never()).save(any(Favorite.class));
  }

  @Test
  void delete_ExistingFavorite_Success() {
    // Arrange
    when(favoriteRepository.findByUserIdAndItemId(anyLong(), anyLong())).thenReturn(Optional.of(favorite));

    // Act
    favoriteService.delete(user.getId(), item.getId());

    // Assert
    verify(favoriteRepository).delete(favorite);
  }

  @Test
  void delete_FavoriteNotFound_ThrowsException() {
    // Arrange
    when(favoriteRepository.findByUserIdAndItemId(anyLong(), anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class,
        () -> favoriteService.delete(user.getId(), item.getId()));
    verify(favoriteRepository, never()).delete(any(Favorite.class));
  }

  @Test
  void countByItemId_ReturnsCorrectCount() {
    // Arrange
    long expectedCount = 5L;
    when(favoriteRepository.countByItemId(anyLong())).thenReturn(expectedCount);

    // Act
    long actualCount = favoriteService.countByItemId(item.getId());

    // Assert
    assertEquals(expectedCount, actualCount);
  }
}