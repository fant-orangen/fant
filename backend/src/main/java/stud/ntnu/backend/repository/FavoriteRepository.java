package stud.ntnu.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.Favorite;
import stud.ntnu.backend.model.Item;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
  List<Favorite> findAllByUserId(Long userId);
  Optional<Favorite> findByUserIdAndItemId(Long itemId, Long userId);
  Long countByItemId(Long itemId);
}
