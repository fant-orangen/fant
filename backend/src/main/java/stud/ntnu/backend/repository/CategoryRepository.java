package stud.ntnu.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.Category;

/**
 * <h2>CategoryRepository</h2>
 * <p>Repository for accessing and managing category data.</p>
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

  /**
   * <h3>Find By Name</h3>
   * <p>Retrieves a category by its exact name.</p>
   *
   * @param name the category name to search for
   * @return {@link Optional} containing the matching category if found
   */
  Optional<Category> findByName(String name);

  boolean existsByName(String name);
}