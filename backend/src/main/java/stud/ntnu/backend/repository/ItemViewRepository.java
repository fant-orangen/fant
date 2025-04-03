package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.ItemView;

@Repository
public interface ItemViewRepository extends JpaRepository<ItemView, Long> {
}