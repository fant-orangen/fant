package stud.ntnu.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.Image;

/**
 * <h2>ImageRepository</h2>
 * <p>Repository interface for accessing and managing {@link Image} entities in the database.</p>
 * <p>Extends Spring Data JPA's {@link JpaRepository} to provide basic CRUD operations and allows
 * for the definition of custom query methods related to item images.</p>
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
  /**
   * <h3>Find By Item ID And URL</h3>
   * <p>Retrieves an {@link Image} entity based on its associated item's ID and the image URL.</p>
   * <p>This method is useful for finding a specific image that belongs to a particular item and has
   * a given URL.</p>
   *
   * @param itemId the unique identifier of the {@link stud.ntnu.backend.model.Item} to which the
   *               image belongs.
   * @param url    the URL of the image. This should be the full URL where the image is hosted.
   * @return an {@link Optional} containing the {@link Image} entity if found for the specified
   * item ID and URL; otherwise, an empty {@code Optional}.
   */
  Optional<Image> findByItemIdAndUrl(Long itemId, String url);

}