package stud.ntnu.backend.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.data.bid.BidCreateDto;
import stud.ntnu.backend.model.Bid;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.model.enums.BidStatus;
import stud.ntnu.backend.model.enums.ItemStatus;
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

  /**
   * <h3>Accept Bid</h3>
   * <p>Changes a bid's status to ACCEPTED if the current user is the seller of the item.</p>
   *
   * @param itemId      ID of the item being bid on
   * @param bidderId    ID of the user who made the bid
   * @param currentUser the authenticated user (must be seller)
   * @throws AccessDeniedException   if the current user is not the seller
   * @throws EntityNotFoundException if the bid or item doesn't exist
   */
  public void acceptBid(Long itemId, Long bidderId, User currentUser) {
    // Find the bid by item ID and bidder ID
    Bid bid = bidRepository.findByItemIdAndBidderId(itemId, bidderId)
        .orElseThrow(() -> new EntityNotFoundException("Bid not found"));

    // Get the item to check ownership
    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new EntityNotFoundException("Item not found"));

    // Check if current user is the seller
    if (!item.getSeller().getId().equals(currentUser.getId())) {
      throw new AccessDeniedException("Only the seller can accept bids");
    }

    // Update the bid status
    bid.setStatus(BidStatus.ACCEPTED);
    bidRepository.save(bid);
  }

  /**
   * <h3>Reject Bid</h3>
   * <p>Changes a bid's status to REJECTED if the current user is the seller of the item.</p>
   *
   * @param itemId      ID of the item being bid on
   * @param bidderId    ID of the user who made the bid
   * @param currentUser the authenticated user (must be seller)
   * @throws AccessDeniedException   if the current user is not the seller
   * @throws EntityNotFoundException if the bid or item doesn't exist
   */
  public void rejectBid(Long itemId, Long bidderId, User currentUser) {
    // Find the bid by item ID and bidder ID
    Bid bid = bidRepository.findByItemIdAndBidderId(itemId, bidderId)
        .orElseThrow(() -> new EntityNotFoundException("Bid not found"));

    // Get the item to check ownership
    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new EntityNotFoundException("Item not found"));

    // Check if current user is the seller
    if (!item.getSeller().getId().equals(currentUser.getId())) {
      throw new AccessDeniedException("Only the seller can reject bids");
    }

    // Update the bid status
    bid.setStatus(BidStatus.REJECTED);
    bidRepository.save(bid);
  }

  /**
   * <h3>Get Bids By Bidder ID</h3>
   * <p>Retrieves all bids placed by a specific user.</p>
   *
   * @param bidderId the ID of the bidder
   * @return list of {@link Bid} entities placed by the user
   */
  public List<Bid> getBidsByBidderId(Long bidderId) {
    return bidRepository.findByBidderId(bidderId);
  }
}