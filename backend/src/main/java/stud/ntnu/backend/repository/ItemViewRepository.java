package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.ItemView;
import java.util.List;

/**
 * <h2>ItemViewRepository</h2>
 * <p>Repository for item view tracking data operations.</p>
 */
@Repository
public interface ItemViewRepository extends JpaRepository<ItemView, Long> {

  /**
   * <h3>Find By User ID</h3>
   * <p>Retrieves all item views for a specific user.</p>
   *
   * @param userId the ID of the user
   * @return list of {@link ItemView} records for the user
   */
  List<ItemView> findByUserId(Long userId);
}