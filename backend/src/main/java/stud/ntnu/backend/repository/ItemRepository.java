package stud.ntnu.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.Item;

/**
 * <h2>ItemRepository</h2>
 * <p>Repository for marketplace item data operations.</p>
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

  /**
   * <h3>Find By Category ID</h3>
   * <p>Retrieves all items belonging to a specific category.</p>
   *
   * @param categoryId the ID of the category
   * @return list of {@link Item} entities in the category
   */
  List<Item> findByCategoryId(Long categoryId);
}