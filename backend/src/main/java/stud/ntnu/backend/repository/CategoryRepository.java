package stud.ntnu.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // Import Modifying
import org.springframework.data.jpa.repository.Query; // Import Query
import org.springframework.data.repository.query.Param; // Import Param
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Import Transactional
import stud.ntnu.backend.model.Category;

/**
 * Repository interface for accessing and managing {@link Category} entities.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> { //

  Optional<Category> findByName(String name); //

  boolean existsByName(String name); //

  /**
   * Updates the details of a Category directly in the database.
   *
   * @param id        The ID of the category to update.
   * @param name      The new name for the category.
   * @param imageUrl  The new image URL for the category.
   * @param parent    The new parent Category entity (can be null).
   * @return The number of rows affected (should be 1 if successful).
   */
  @Modifying // Indicates this query modifies data
  @Transactional // Recommended for modifying queries within repository
  @Query("UPDATE Category c SET c.name = :name, c.imageUrl = :imageUrl, c.parent = :parent WHERE c.id = :id")
  int updateCategoryDetails(@Param("id") Long id,
      @Param("name") String name,
      @Param("imageUrl") String imageUrl,
      @Param("parent") Category parent); // Pass the parent entity or null
}