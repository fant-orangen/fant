package stud.ntnu.backend.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.Favorite;

/**
 * <h2>FavoriteRepository</h2>
 * <p>Repository interface for accessing and managing {@link Favorite} entities in the database.</p>
 * <p>Extends Spring Data JPA's {@link JpaRepository} to provide basic CRUD operations and allows
 * for the definition of custom query methods related to user's favorite items.</p>
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

  /**
   * <h3>Find All By User ID (Paged)</h3>
   * <p>Retrieves a paginated list of {@link Favorite} entities for a specific user.</p>
   *
   * @param userId   the unique identifier of the user whose favorite items are being retrieved.
   * @param pageable the pagination information, including page number, size, and sort order.
   * @return a {@link Page} of {@link Favorite} entities associated with the specified user,
   * according to the provided pagination.
   */
  Page<Favorite> findAllByUserId(Long userId, Pageable pageable);

  /**
   * <h3>Find By User And Item</h3>
   * <p>Finds a specific {@link Favorite} entity based on the user and the item.</p>
   *
   * @param itemId the unique identifier of the item that is favorited.
   * @param userId the unique identifier of the user who favorited the item.
   * @return an {@link Optional} containing the {@link Favorite} entity if the relationship between
   * the user and the item exists in the favorites; otherwise, an empty {@code Optional}.
   */
  Optional<Favorite> findByUserIdAndItemId(Long itemId, Long userId);

  /**
   * <h3>Count By Item ID</h3>
   * <p>Counts the number of users who have favorited a specific item.</p>
   *
   * @param itemId the unique identifier of the item for which to count the favorites.
   * @return the total number of {@link Favorite} entities associated with the specified item. This
   * indicates how many users have added this item to their favorites.
   */
  Long countByItemId(Long itemId);

  /**
   * <h3>Exists By User ID And Item ID</h3>
   * <p>Checks if a favorite relationship exists between a specific user and a specific item.</p>
   *
   * @param userId the unique identifier of the user.
   * @param itemId the unique identifier of the item.
   * @return {@code true} if a {@link Favorite} entity exists for the given user and item IDs;
   * {@code false} otherwise.
   */
  boolean existsByUserIdAndItemId(Long userId, Long itemId);
}