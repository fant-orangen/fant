package stud.ntnu.backend.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.Bid;

import java.util.List;

/**
 * <h2>BidRepository</h2>
 * <p>Repository interface for accessing and managing {@link Bid} entities in the database.</p>
 * <p>Extends Spring Data JPA's {@link JpaRepository} to provide basic CRUD operations and allows
 * for the definition of custom query methods related to bids.</p>
 */
@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

  /**
   * <h3>Find By Item ID</h3>
   * <p>Retrieves all {@link Bid} entities associated with a specific item.</p>
   *
   * @param itemId the unique identifier of the item for which to retrieve bids.
   * @return a {@link List} of {@link Bid} entities that have been placed on the specified item.
   * Returns an empty list if no bids are found for the item.
   */
  List<Bid> findByItemId(Long itemId);

  /**
   * <h3>Find By Item ID (Paged)</h3>
   * <p>Retrieves a paginated list of {@link Bid} entities associated with a specific item.</p>
   *
   * @param itemId   the unique identifier of the item for which to retrieve bids.
   * @param pageable the pagination information, including page number, size, and sort order.
   * @return a {@link Page} of {@link Bid} entities that have been placed on the specified item,
   * according to the provided pagination.
   */
  Page<Bid> findByItemId(Long itemId, Pageable pageable);

  /**
   * <h3>Find By Bidder ID (Paged)</h3>
   * <p>Retrieves a paginated list of {@link Bid} entities made by a specific user.</p>
   *
   * @param bidderId the unique identifier of the user who placed the bids.
   * @param pageable the pagination information, including page number, size, and sort order.
   * @return a {@link Page} of {@link Bid} entities made by the specified user, according to the
   * provided pagination.
   */
  Page<Bid> findByBidderId(Long bidderId, Pageable pageable);

  /**
   * <h3>Find By Item ID And Bidder ID</h3>
   * <p>Finds a specific {@link Bid} entity for a given item made by a specific user.</p>
   *
   * @param itemId   the unique identifier of the item.
   * @param bidderId the unique identifier of the user who placed the bid.
   * @return an {@link Optional} containing the {@link Bid} entity if found for the specified item
   * and bidder; otherwise, an empty {@code Optional}.
   */
  Optional<Bid> findByItemIdAndBidderId(Long itemId, Long bidderId);
}