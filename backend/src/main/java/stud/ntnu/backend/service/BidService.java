package stud.ntnu.backend.service;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional; // Import Optional
import java.util.stream.Collectors;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.data.bid.BidCreateDto;
import stud.ntnu.backend.data.bid.BidUpdateDto;
import stud.ntnu.backend.data.bid.BidResponseDto;
import stud.ntnu.backend.model.Bid;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.model.enums.BidStatus;
import stud.ntnu.backend.repository.BidRepository;
import stud.ntnu.backend.repository.ItemRepository;

/**
 * <h2>OrderService</h2>
 * <p>Service for managing order and bid operations.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BidService {

  /**
   * <h3>Bid Repository</h3>
   * <p>Repository for accessing bid data.</p>
   */
  private final BidRepository bidRepository;

  /**
   * <h3>Item Repository</h3>
   * <p>Repository for accessing item data.</p>
   */
  private final ItemRepository itemRepository;

  /**
   * Creates a new bid or potentially updates an existing PENDING bid.
   * Prevents placing new bids if an ACCEPTED/REJECTED one exists.
   *
   * @param bidCreateDto DTO containing bid details.
   * @param currentUser  The user placing the bid.
   * @return The created or updated Bid entity.
   * @throws IllegalArgumentException if the user already has a non-pending bid or is the seller.
   * @throws EntityNotFoundException  if the item doesn't exist.
   */
  @Transactional
  public BidResponseDto createBid(BidCreateDto bidCreateDto, User currentUser) {
    log.info("Attempting to create/update bid for item ID {} by user ID {}",
        bidCreateDto.getItemId(), currentUser.getId());
    Item item = itemRepository.findById(bidCreateDto.getItemId())
        .orElseThrow(() -> {
          log.error("Item not found with ID: {}", bidCreateDto.getItemId());
          return new EntityNotFoundException(
              "Item with ID " + bidCreateDto.getItemId() + " not found");
        });

    // Prevent seller from bidding on their own item
    if (item.getSeller().getId().equals(currentUser.getId())) {
      log.warn("Seller (User ID {}) attempted to bid on their own item (Item ID {})",
          currentUser.getId(), item.getId());
      throw new IllegalArgumentException("Sellers cannot bid on their own items.");
    }

    // --- Check for existing bid ---
    Optional<Bid>
        existingBidOptional =
        bidRepository.findByItemIdAndBidderId(item.getId(), currentUser.getId());

    if (existingBidOptional.isPresent()) {
      Bid existingBid = existingBidOptional.get();
      log.info("Existing bid (ID {}) found for item {} / bidder {}", existingBid.getId(),
          item.getId(), currentUser.getId());

      // If existing bid is PENDING, update it instead of creating a new one
      if (existingBid.getStatus() == BidStatus.PENDING) {
        log.info("Updating existing PENDING bid ID {}", existingBid.getId());
        existingBid.setAmount(bidCreateDto.getAmount());
        existingBid.setComment(bidCreateDto.getComment());
        // No status change needed here, it remains PENDING until accepted/rejected
        return mapToBidResponseDto(bidRepository.save(existingBid));
      } else {
        // If bid is ACCEPTED or REJECTED, prevent placing a new bid
        log.warn(
            "User {} attempted to place a new bid on item {} but already has a bid with status {}",
            currentUser.getId(), item.getId(), existingBid.getStatus());
        throw new IllegalArgumentException(
            "You already have a bid on this item with status " + existingBid.getStatus() +
                ". Cannot place a new bid.");
      }
    } else {
      // No existing bid found, create a new one
      log.info("No existing bid found for item {} / bidder {}. Creating new bid.", item.getId(),
          currentUser.getId());
      Bid newBid = Bid.builder()
          .item(item)
          .bidder(currentUser)
          .amount(bidCreateDto.getAmount())
          .comment(bidCreateDto.getComment())
          .status(BidStatus.PENDING) // New bids are always PENDING
          .build();

      return mapToBidResponseDto(bidRepository.save(newBid));
    }
  }

  public Page<BidResponseDto> getBidsForItem(Long itemId, User currentUser, Pageable pageable) {
    log.info("User ID {} attempting to fetch bids for item ID {}", currentUser.getId(), itemId);
    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> {
          log.error("Item not found with ID: {} during bid fetch", itemId);
          return new EntityNotFoundException("Item not found with id: " + itemId);
        });

    if (!item.getSeller().getId().equals(currentUser.getId())) {
      log.warn("Access denied: User ID {} is not the seller of item ID {}", currentUser.getId(),
          itemId);
      throw new BadCredentialsException("You are not authorized to view bids for this item.");
    }

    return bidRepository.findByItemId(itemId, pageable).map(this::mapToBidResponseDto);
  }

  /**
   * <h3>Delete Bid By Item ID And Bidder</h3>
   * <p>Deletes a bid placed by the specified bidder on the specified item.</p>
   *
   * @param itemId the ID of the item
   * @param bidder the user who placed the bid
   * @throws EntityNotFoundException if no matching bid is found
   */
  @Transactional
  public void deleteBidByItemIdAndBidder(Long itemId, User bidder) {
    log.info("Attempting to delete bid for item ID {} by bidder ID {}", itemId, bidder.getId());
    // Now findByItemIdAndBidderId should reliably return 0 or 1 result due to constraint
    Bid bid = bidRepository.findByItemIdAndBidderId(itemId, bidder.getId())
        .orElseThrow(() -> {
          log.warn("Cannot delete bid: Bid not found for item ID {} and bidder ID {}", itemId, bidder.getId());
          // Return EntityNotFoundException or just log and return if delete is non-critical
          return new EntityNotFoundException("Bid not found for item ID: " + itemId + " and bidder ID: " + bidder.getId());
        });

    // Optional: Add check if bid status allows deletion (e.g., only PENDING bids?)
    // if (bid.getStatus() != BidStatus.PENDING) {
    //    logger.warn("Cannot delete bid ID {}: Status is {}", bid.getId(), bid.getStatus());
    //    throw new IllegalStateException("Cannot delete a bid that is not PENDING.");
    // }


    bidRepository.delete(bid);
    log.info("Bid deleted successfully for item ID {} by bidder ID {}", itemId, bidder.getId());
  }

  @Transactional
  public void acceptBid(Long itemId, Long bidderId, User currentUser) {
    log.info("User ID {} attempting to accept bid from bidder ID {} for item ID {}", currentUser.getId(), bidderId, itemId);
    // findByItemIdAndBidderId should now be unique
    Bid bid = bidRepository.findByItemIdAndBidderId(itemId, bidderId)
        .orElseThrow(() -> {
          log.error("Accept Bid Error: Bid not found for item {} / bidder {}", itemId, bidderId);
          return new EntityNotFoundException("Bid not found");
        });

    Item item = bid.getItem();

    if (!item.getSeller().getId().equals(currentUser.getId())) {
      log.warn("Access Denied: User {} attempted to accept bid on item {} owned by {}", currentUser.getId(), itemId, item.getSeller().getId());
      throw new BadCredentialsException("Only the seller can accept bids");
    }

    // Check if the bid is PENDING before accepting
    if (bid.getStatus() != BidStatus.PENDING) {
      log.warn("Attempted to accept bid ID {} which is not PENDING (status: {})", bid.getId(), bid.getStatus());
      throw new IllegalStateException("Only PENDING bids can be accepted.");
    }


    // Optionally: Reject other pending bids for the same item
    List<Bid> otherBids = bidRepository.findByItemId(itemId);
    otherBids.stream()
        .filter(other -> other.getStatus() == BidStatus.PENDING && !other.getId().equals(bid.getId()))
        .forEach(other -> {
          log.info("Auto-rejecting other pending bid ID {} for item {}", other.getId(), itemId);
          other.setStatus(BidStatus.REJECTED);
          bidRepository.save(other); // Save change for each rejected bid
        });

    bid.setStatus(BidStatus.ACCEPTED);
    bidRepository.save(bid);
    // Optionally update item status
    // item.setStatus(ItemStatus.SOLD);
    // itemRepository.save(item);
    log.info("Bid ID {} accepted successfully for item ID {}", bid.getId(), itemId);
  }

  @Transactional
  public void rejectBid(Long itemId, Long bidderId, User currentUser) {
    log.info("User ID {} attempting to reject bid from bidder ID {} for item ID {}", currentUser.getId(), bidderId, itemId);
    // findByItemIdAndBidderId should now be unique
    Bid bid = bidRepository.findByItemIdAndBidderId(itemId, bidderId)
        .orElseThrow(() -> {
          log.error("Reject Bid Error: Bid not found for item {} / bidder {}", itemId, bidderId);
          return new EntityNotFoundException("Bid not found");
        });

    Item item = bid.getItem();

    if (!item.getSeller().getId().equals(currentUser.getId())) {
      log.warn("Access Denied: User {} attempted to reject bid on item {} owned by {}", currentUser.getId(), itemId, item.getSeller().getId());
      throw new BadCredentialsException("Only the seller can reject bids");
    }

    if (bid.getStatus() == BidStatus.PENDING) {
      bid.setStatus(BidStatus.REJECTED);
      bidRepository.save(bid);
      log.info("Bid ID {} rejected successfully for item ID {}", bid.getId(), itemId);
    } else {
      log.warn("Attempted to reject bid ID {} which is already in status {}", bid.getId(), bid.getStatus());
      // If already handled, maybe just return successfully without error
      // throw new IllegalStateException("Bid is not in PENDING status.");
    }
  }

  @Transactional
  public BidResponseDto updateBid(BidUpdateDto bidUpdateDto, Long itemId, User bidder) {
    if (!itemRepository.existsById(itemId)) {
      throw new EntityNotFoundException("Item not found with ID: " + itemId);
    }

    Bid bid = bidRepository.findByItemIdAndBidderId(itemId, bidder.getId())
        .orElseThrow(() -> new EntityNotFoundException(
            "Bid not found with item ID: " + itemId + " and bidder ID: " + bidder.getId()));


    bid.setAmount(bidUpdateDto.getAmount());
    bid.setComment(bidUpdateDto.getComment());

    // Save the updated bid
    return mapToBidResponseDto(bidRepository.save(bid));
  }

  /**
   * <h3>Get Bids By Bidder ID</h3>
   * <p>Retrieves all bids placed by a specific user.</p>
   *
   * @param bidderId the ID of the bidder
   * @return list of {@link Bid} entities placed by the user
   */
  public Page<BidResponseDto> getBidsByBidderId(Long bidderId, Pageable pageable) {
    return bidRepository.findByBidderId(bidderId, pageable).map(this::mapToBidResponseDto);
  }

  private BidResponseDto mapToBidResponseDto(Bid bid) {
    if (bid == null) return null;
    return BidResponseDto.builder()
        .id(bid.getId())
        .itemId(bid.getItem() != null ? bid.getItem().getId() : null)
        .bidderId(bid.getBidder() != null ? bid.getBidder().getId() : null)
        .bidderUsername(bid.getBidder() != null ? bid.getBidder().getDisplayName() : "Unknown Bidder")
        .amount(bid.getAmount())
        .comment(bid.getComment())
        .status(bid.getStatus())
        .createdAt(bid.getCreatedAt())
        .updatedAt(bid.getUpdatedAt())
        .build();
  }
}