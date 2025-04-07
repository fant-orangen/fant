package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.backend.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}