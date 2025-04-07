package stud.ntnu.backend.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.data.bid.BidCreateDto;
import stud.ntnu.backend.model.Bid;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.OrderRepository;
import stud.ntnu.backend.repository.ItemRepository;

/**
 * <h2>OrderService</h2>
 * <p>Service for managing order and bid operations.</p>
 */
@Service
@RequiredArgsConstructor
public class OrderService {

  /**
   * <h3>Bid Repository</h3>
   * <p>Repository for accessing bid data.</p>
   */
  private final OrderRepository bidRepository;

  /**
   * <h3>Item Repository</h3>
   * <p>Repository for accessing item data.</p>
   */
  private final ItemRepository itemRepository;

  /**
   * <h3>Create Bid</h3>
   * <p>Creates a new bid for an item from a logged-in user.</p>
   *
   * @param bidCreateDto the bid details
   * @param currentUser  the authenticated user making the bid
   * @return the created {@link Bid}
   * @throws jakarta.persistence.EntityNotFoundException if the item doesn't exist
   */
  @Transactional
  public Bid createBid(BidCreateDto bidCreateDto, User currentUser) {
    Item item = itemRepository.findById(bidCreateDto.getItemId())
        .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
            "Item with ID " + bidCreateDto.getItemId() + " not found"));

    Bid bid = Bid.builder()
        .item(item)
        .bidder(currentUser)
        .amount(bidCreateDto.getAmount())
        .comment(bidCreateDto.getComment())
        .status(bidCreateDto.getStatus() != null ? bidCreateDto.getStatus()
            : Bid.builder().build().getStatus())
        .build();

    return bidRepository.save(bid);
  }

  /**
   * <h3>Delete Bid By Item ID And Bidder</h3>
   * <p>Deletes a bid placed by the specified bidder on the specified item.</p>
   *
   * @param itemId the ID of the item
   * @param bidder the user who placed the bid
   * @throws EntityNotFoundException if no matching bid is found
   */
  public void deleteBidByItemIdAndBidder(Long itemId, User bidder) {
    Bid bid = bidRepository.findByItemIdAndBidderId(itemId, bidder.getId())
        .orElseThrow(() -> new EntityNotFoundException(
            "Bid not found for item ID: " + itemId + " and bidder ID: " + bidder.getId()));

    bidRepository.delete(bid);
  }
}