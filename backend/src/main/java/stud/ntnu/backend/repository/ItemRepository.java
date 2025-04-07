package stud.ntnu.backend.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.Item;

/**
 * <h2>ItemRepository</h2>
 * <p>Repository for marketplace item data operations.</p>
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

  /**
   * <h3>Find By Category ID</h3>
   * <p>Retrieves all items belonging to a specific category.</p>
   *
   * @param categoryId the ID of the category
   * @return list of {@link Item} entities in the category
   */
  List<Item> findByCategoryId(Long categoryId);

  Page<Item> findByCategoryId(Long categoryId, Pageable pageable);


  /**
   * <h3>Find By Seller ID</h3>
   * <p>Retrieves all items listed by a specific seller.</p>
   *
   * @param sellerId the ID of the seller
   * @return list of {@link Item} entities listed by the seller
   */
  Page<Item> findBySellerId(Long sellerId, Pageable pageable);
}