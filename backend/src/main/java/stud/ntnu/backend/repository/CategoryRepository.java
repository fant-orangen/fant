package stud.ntnu.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.Category;

/**
 * <h2>Category repository</h2>
 * Repository responsible for defining the database functions related to the {@link Category}-model
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  Optional<Category> findByName(String name);
}
