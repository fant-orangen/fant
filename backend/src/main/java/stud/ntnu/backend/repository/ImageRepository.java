package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.Image;

/**
 * <h2>ImageRepository</h2>
 * <p>Repository for image-related data operations in the marketplace application.</p>
 * <p>Provides standard CRUD operations for {@link Image} entities through JPA inheritance.</p>
 * <p>Used by {@link stud.ntnu.backend.service.ImageService} to persist image metadata after upload
 * to Cloudinary.</p>
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

}