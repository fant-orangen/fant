package stud.ntnu.backend.repository;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.enums.ItemStatus;

/**
 * <h2>ItemRepository</h2>
 * <p>Repository interface for accessing and managing {@link Item} entities in the database.</p>
 * <p>Extends Spring Data JPA's {@link JpaRepository} for basic CRUD operations and
 * {@link JpaSpecificationExecutor} to allow for complex, criteria-based queries.</p>
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

  /**
   * <h3>Find By Category ID</h3>
   * <p>Retrieves all {@link Item} entities that belong to a specific category.</p>
   *
   * @param categoryId the unique identifier of the {@link stud.ntnu.backend.model.Category}.
   * @return a {@link List} of {@link Item} entities associated with the specified category. Returns
   * an empty list if no items are found in the category.
   */
  List<Item> findByCategoryId(Long categoryId);

  /**
   * <h3>Find By Category ID (Paged)</h3>
   * <p>Retrieves a paginated list of {@link Item} entities that belong to a specific category.</p>
   *
   * @param categoryId the unique identifier of the {@link stud.ntnu.backend.model.Category}.
   * @param pageable   the pagination information, including page number, size, and sort order.
   * @return a {@link Page} of {@link Item} entities associated with the specified category,
   * according to the provided pagination.
   */
  Page<Item> findByCategoryId(Long categoryId, Pageable pageable);


  /**
   * <h3>Find By Seller ID (Paged)</h3>
   * <p>Retrieves a paginated list of {@link Item} entities that have been listed by a specific
   * seller.</p>
   *
   * @param sellerId the unique identifier of the {@link stud.ntnu.backend.model.User} who is the
   *                 seller of the items.
   * @param pageable the pagination information, including page number, size, and sort order.
   * @return a {@link Page} of {@link Item} entities listed by the specified seller, according to
   * the provided pagination.
   */
  Page<Item> findBySellerId(Long sellerId, Pageable pageable);
}