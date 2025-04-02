package stud.ntnu.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.Item;

/**
 * <h2>ItemRepository</h2>
 * <p>Repository interface for accessing and managing {@link Item} entities in the database.</p>
 * <p>Extends Spring Data JPA's {@link JpaRepository} to leverage built-in data access operations
 * such as saving entities, finding by ID, retrieving all items, counting, and deleting.</p>
 * <p>This repository is utilized by the {@link stud.ntnu.backend.service.ItemService} class to
 * abstract data access operations and provide a clean separation between business logic and data
 * persistence layers.</p>
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
  // No additional methods needed - we'll use the inherited findAll()

  /**
   * Find all items that belong to a specific category.
   *
   * @param categoryId the ID of the category to search for
   * @return list of items belonging to the specified category
   */
  List<Item> findByCategoryId(Long categoryId);
}