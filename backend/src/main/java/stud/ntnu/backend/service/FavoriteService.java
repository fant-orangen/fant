package stud.ntnu.backend.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.exception.AlreadyFavoritedException;
import stud.ntnu.backend.model.Favorite;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.FavoriteRepository;
import stud.ntnu.backend.repository.ItemRepository;

/**
 * <h2>FavoriteService</h2>
 * <p>Service for managing user's favorite items within the marketplace.</p>
 * <p>This service provides functionalities for adding, removing, and counting favorite items for
 * users. It interacts with the {@link FavoriteRepository} and {@link ItemRepository} to persist and
 * retrieve data.</p>
 */
@Service
@RequiredArgsConstructor
public class FavoriteService {

  /**
   * <h3>Favorite Repository</h3>
   * <p>Data access component for {@link Favorite} entities.</p>
   * <p>Provides methods for interacting with the database to perform operations on user's favorite
   * items.</p>
   */
  private final FavoriteRepository favoriteRepository;

  /**
   * <h3>Item Repository</h3>
   * <p>Data access component for {@link Item} entities.</p>
   * <p>Provides methods for interacting with the database to retrieve item information.</p>
   */
  private final ItemRepository itemRepository;

  /**
   * <h3>Add Favorite</h3>
   * <p>Adds a specific {@link Item} to a user's list of favorite items.</p>
   *
   * @param user   the {@link User} who is adding the item to their favorites.
   * @param itemId the unique identifier of the {@link Item} to be added to favorites.
   * @throws EntityNotFoundException   if an item with the specified ID does not exist.
   * @throws AlreadyFavoritedException if the user has already favorited the specified item.
   */
  @Transactional
  public void add(User user, Long itemId) {
    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new EntityNotFoundException("Item not found with id " + itemId));
    if (favoriteRepository.existsByUserIdAndItemId(user.getId(), itemId)) {
      throw new AlreadyFavoritedException(
          "User " + user.getId() + " already has favorited item " + itemId);
    }
    favoriteRepository.save(Favorite.builder().user(user).item(item).build());
  }

  /**
   * <h3>Remove Favorite</h3>
   * <p>Removes a specific {@link Item} from a user's list of favorite items.</p>
   *
   * @param userId the unique identifier of the {@link User} whose favorite item is being removed.
   * @param itemId the unique identifier of the {@link Item} to be removed from favorites.
   * @throws EntityNotFoundException if no favorite relationship is found between the specified user
   *                                 and item.
   */
  @Transactional
  public void delete(Long userId, Long itemId) {
    favoriteRepository.delete(favoriteRepository.findByUserIdAndItemId(userId, itemId).orElseThrow(
        () -> new EntityNotFoundException(
            "Favorite not found with user id " + userId + "and item id " + itemId)));
  }

  /**
   * <h3>Count Item Favorites</h3>
   * <p>Counts the total number of users who have added a specific {@link Item} to their favorites.</p>
   *
   * @param itemId the unique identifier of the {@link Item} for which to count the favorites.
   * @return the total number of {@link Favorite} entities associated with the specified item.
   */
  public Long countByItemId(Long itemId) {
    return favoriteRepository.countByItemId(itemId);
  }

  /**
   * <h3>Check Favorite Status</h3>
   * <p>Checks if a user has favorited a specific item.</p>
   *
   * @param userId the ID of the user
   * @param itemId the ID of the item
   * @return true if the item is favorited by the user, false otherwise
   */
  public boolean checkFavoriteStatus(Long userId, Long itemId) {
    return favoriteRepository.existsByUserIdAndItemId(userId, itemId);
  }
}
