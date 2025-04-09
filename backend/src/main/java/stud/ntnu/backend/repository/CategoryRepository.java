package stud.ntnu.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.Category;

/**
 * <h2>CategoryRepository</h2>
 * <p>Repository interface for accessing and managing {@link Category} entities in the database.</p>
 * <p>Extends Spring Data JPA's {@link JpaRepository} to provide basic CRUD operations and allows
 * for the definition of custom query methods.</p>
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

  /**
   * <h3>Find By Name</h3>
   * <p>Retrieves a {@link Category} entity from the database based on its exact name.</p>
   *
   * @param name the category name to search for. This name is case-sensitive based on the database
   *             collation.
   * @return {@link Optional} containing the matching {@link Category} entity if found; otherwise,
   * an empty {@code Optional}.
   */
  Optional<Category> findByName(String name);

  /**
   * <h3>Exists By Name</h3>
   * <p>Checks if a {@link Category} entity with the given name exists in the database.</p>
   *
   * @param name the category name to check for existence. This name is case-sensitive based on the
   *             database collation.
   * @return {@code true} if a category with the given name exists; {@code false} otherwise.
   */
  boolean existsByName(String name);
}