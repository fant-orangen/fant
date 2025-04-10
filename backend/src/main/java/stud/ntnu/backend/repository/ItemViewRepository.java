package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.ItemView;
import java.util.List;

/**
 * <h2>ItemViewRepository</h2>
 * <p>Repository interface for accessing and managing {@link ItemView} entities in the database.</p>
 * <p>Extends Spring Data JPA's {@link JpaRepository} to provide basic CRUD operations for tracking
 * when users view items.</p>
 */
@Repository
public interface ItemViewRepository extends JpaRepository<ItemView, Long> {

  /**
   * <h3>Find By User ID</h3>
   * <p>Retrieves all {@link ItemView} records associated with a specific user.</p>
   * <p>This method allows querying the database to find all instances where a particular user has
   * viewed marketplace items.</p>
   *
   * @param userId the unique identifier of the {@link stud.ntnu.backend.model.User}.
   * @return a {@link List} of {@link ItemView} entities representing all the items viewed by the
   * specified user. Returns an empty list if the user has not viewed any items or if no records are
   * found for that user.
   */
  List<ItemView> findByUserId(Long userId);
}