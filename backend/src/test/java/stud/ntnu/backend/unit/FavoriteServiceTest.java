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

/**
 * <h2>Unit Tests for Favorite Service</h2>
 * <p>This class contains unit tests for the {@link FavoriteService} class, focusing on the business logic for adding, deleting, and counting favorite items for users.</p>
 * <p>The tests utilize Mockito to mock the dependencies of the {@code FavoriteService}, such as {@link FavoriteRepository} and {@link ItemRepository}, allowing for isolated testing of the service's methods.</p>
 */
@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTest {

  /**
   * <h3>Mock FavoriteRepository</h3>
   * <p>A Mockito mock for the {@link FavoriteRepository} dependency, used to simulate database interactions related to user favorites.</p>
   */
  @Mock
  private FavoriteRepository favoriteRepository;

  /**
   * <h3>Mock ItemRepository</h3>
   * <p>A Mockito mock for the {@link ItemRepository} dependency, used to simulate database interactions related to items.</p>
   */
  @Mock
  private ItemRepository itemRepository;

  /**
   * <h3>InjectMocks FavoriteService</h3>
   * <p>The instance of {@link FavoriteService} being tested, with its dependencies injected via Mockito.</p>
   */
  @InjectMocks
  private FavoriteService favoriteService;

  /**
   * <h3>Test User</h3>
   * <p>A {@link User} entity used for testing favorite item operations.</p>
   */
  private User user;

  /**
   * <h3>Test Item</h3>
   * <p>An {@link Item} entity used for testing adding and deleting from favorites.</p>
   */
  private Item item;

  /**
   * <h3>Test Favorite Entity</h3>
   * <p>A {@link Favorite} entity representing the relationship between a user and a favorited item.</p>
   */
  private Favorite favorite;

  /**
   * <h3>Setup Method</h3>
   * <p>This method is executed before each test case. It initializes a test user, a test item, and a test favorite entity.</p>
   */
  @BeforeEach
  void setUp() {
    user = User.builder().id(1L).displayName("Test User").build();

    item = Item.builder().id(1L).briefDescription("Test Item").build();

    favorite =
        Favorite.builder().id(1L).user(user).item(item).createdAt(LocalDateTime.now()).build();
  }

  /**
   * <h3>Test Add New Favorite - Success</h3>
   * <p>Tests the scenario where a user successfully adds an item to their favorites. It verifies that the item exists, is not already favorited by the user, and that a new {@link Favorite} entity is saved to the repository.</p>
   */
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

  /**
   * <h3>Test Add Favorite - Item Not Found - Throws Exception</h3>
   * <p>Tests the scenario where the item to be favorited does not exist in the database. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void add_ItemNotFound_ThrowsException() {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> favoriteService.add(user, item.getId()));
    verify(favoriteRepository, never()).save(any(Favorite.class));
  }

  /**
   * <h3>Test Add Favorite - Already Favorited - Throws Exception</h3>
   * <p>Tests the scenario where a user attempts to add an item to their favorites that is already favorited. It verifies that an {@link AlreadyFavoritedException} is thrown.</p>
   */
  @Test
  void add_AlreadyFavorited_ThrowsException() {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
    when(favoriteRepository.existsByUserIdAndItemId(anyLong(), anyLong())).thenReturn(true);

    // Act & Assert
    assertThrows(AlreadyFavoritedException.class, () -> favoriteService.add(user, item.getId()));
    verify(favoriteRepository, never()).save(any(Favorite.class));
  }

  /**
   * <h3>Test Delete Existing Favorite - Success</h3>
   * <p>Tests the successful deletion of an item from a user's favorites. It verifies that the corresponding {@link Favorite} entity is retrieved and then deleted from the repository.</p>
   */
  @Test
  void delete_ExistingFavorite_Success() {
    // Arrange
    when(favoriteRepository.findByUserIdAndItemId(anyLong(), anyLong())).thenReturn(
        Optional.of(favorite));

    // Act
    favoriteService.delete(user.getId(), item.getId());

    // Assert
    verify(favoriteRepository).delete(favorite);
  }

  /**
   * <h3>Test Delete Favorite - Not Found - Throws Exception</h3>
   * <p>Tests the scenario where an attempt is made to delete a favorite that does not exist (i.e., the user has not favorited the item). It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void delete_FavoriteNotFound_ThrowsException() {
    // Arrange
    when(favoriteRepository.findByUserIdAndItemId(anyLong(), anyLong())).thenReturn(
        Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class,
        () -> favoriteService.delete(user.getId(), item.getId()));
    verify(favoriteRepository, never()).delete(any(Favorite.class));
  }

  /**
   * <h3>Test Count Favorites By Item ID - Returns Correct Count</h3>
   * <p>Tests the retrieval of the number of users who have favorited a specific item. It verifies that the {@link FavoriteRepository}'s {@code countByItemId} method is called and returns the expected count.</p>
   */
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