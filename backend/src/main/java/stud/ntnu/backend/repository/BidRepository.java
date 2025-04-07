package stud.ntnu.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.Bid;

import java.util.List;

/**
 * <h2>BidRepository</h2>
 * <p>Repository for accessing and managing bid entities.</p>
 */
@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

  /**
   * <h3>Find By Item ID</h3>
   * <p>Retrieves all bids for a specific item.</p>
   *
   * @param itemId the ID of the item
   * @return list of {@link Bid} entities
   */
  List<Bid> findByItemId(Long itemId);

  /**
   * <h3>Find By Bidder ID</h3>
   * <p>Retrieves all bids made by a specific user.</p>
   *
   * @param bidderId the ID of the user
   * @return list of {@link Bid} entities
   */
  List<Bid> findByBidderId(Long bidderId);

  /**
   * <h3>Find By Item ID And Bidder ID</h3>
   * <p>Finds a bid for a specific item made by a specific user.</p>
   *
   * @param itemId   the ID of the item
   * @param bidderId the ID of the bidder
   * @return optional containing the bid if found
   */
  Optional<Bid> findByItemIdAndBidderId(Long itemId, Long bidderId);
}