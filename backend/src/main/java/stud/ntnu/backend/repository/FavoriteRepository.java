package stud.ntnu.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.Favorite;

/**
 * <h2>FavoriteRepository</h2>
 * <p>Repository for accessing and managing favorite item relationships.</p>
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

  /**
   * <h3>Find All By User ID</h3>
   * <p>Retrieves all favorite items for a specific user.</p>
   *
   * @param userId the ID of the user
   * @return list of {@link Favorite} entities
   */
  Page<Favorite> findAllByUserId(Long userId, Pageable pageable);

  /**
   * <h3>Find By User And Item</h3>
   * <p>Finds a specific favorite relationship between user and item.</p>
   *
   * @param itemId the ID of the item
   * @param userId the ID of the user
   * @return {@link Optional} containing the favorite if found
   */
  Optional<Favorite> findByUserIdAndItemId(Long itemId, Long userId);

  /**
   * <h3>Count By Item ID</h3>
   * <p>Counts how many users have favorited a specific item.</p>
   *
   * @param itemId the ID of the item
   * @return total number of favorites for the item
   */
  Long countByItemId(Long itemId);

  boolean existsByUserIdAndItemId(Long userId, Long itemId);
}