package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.ItemView;

import java.util.List;

/**
 * <h2>ItemViewRepository</h2>
 * <p>Repository interface for accessing and managing {@link ItemView} entities in the
 * database.</p>
 */
@Repository
public interface ItemViewRepository extends JpaRepository<ItemView, Long> {

  /**
   * Find all item views for a specific user
   *
   * @param userId the ID of the user
   * @return list of item views by the user
   */
  List<ItemView> findByUserId(Long userId);
}