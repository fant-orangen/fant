package stud.ntnu.backend.service;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional; // Import Optional

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.extern.slf4j.Slf4j;
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
 * <h2>BidService</h2>
 * <p>Service for managing bid operations on marketplace items.</p>
 * <p>This service provides functionalities for creating, retrieving, updating, accepting, rejecting,
 * and deleting bids. It also handles business logic and validations associated with these
 * operations.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BidService {

  /**
   * <h3>Bid Repository</h3>
   * <p>Repository for accessing and managing {@link Bid} entities in the database.</p>
   */
  private final BidRepository bidRepository;

  /**
   * <h3>Item Repository</h3>
   * <p>Repository for accessing and managing {@link Item} entities in the database.</p>
   */
  private final ItemRepository itemRepository;

  /**
   * <h3>Create Bid</h3>
   * <p>Creates a new bid for an item by the current user. If a PENDING bid already exists, it will
   * be updated. Prevents users from having multiple non-pending bids on the same item and sellers
   * from bidding on their own items.</p>
   *
   * @param bidCreateDto DTO containing the details of the bid to be created or updated, including
   *                     the item ID and bid amount.
   * @param currentUser  the {@link User} placing the bid.
   * @return a {@link BidResponseDto} representing the created or updated {@link Bid} entity.
   * @throws IllegalArgumentException if the current user (seller) tries to bid on their own item or
   *                                  if the user already has a non-pending bid on the item.
   * @throws EntityNotFoundException  if the item specified in the {@code bidCreateDto} does not
   *                                  exist.
   */
  @Transactional
  public BidResponseDto createBid(BidCreateDto bidCreateDto, User currentUser) {
    log.info("Attempting to create/update bid for item ID {} by user ID {}",
        bidCreateDto.getItemId(), currentUser.getId());
    Item item = itemRepository.findById(bidCreateDto.getItemId()).orElseThrow(() -> {
      log.error("Item not found with ID: {}", bidCreateDto.getItemId());
      return new EntityNotFoundException("Item with ID " + bidCreateDto.getItemId() + " not found");
    });

    // Prevent seller from bidding on their own item
    if (item.getSeller().getId().equals(currentUser.getId())) {
      log.warn("Seller (User ID {}) attempted to bid on their own item (Item ID {})",
          currentUser.getId(), item.getId());
      throw new IllegalArgumentException("Sellers cannot bid on their own items.");
    }

    // --- Check for existing bid ---
    Optional<Bid> existingBidOptional =
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
      Bid newBid = Bid.builder().item(item).bidder(currentUser).amount(bidCreateDto.getAmount())
          .comment(bidCreateDto.getComment())
          .status(BidStatus.PENDING) // New bids are always PENDING
          .build();

      return mapToBidResponseDto(bidRepository.save(newBid));
    }
  }

  /**
   * <h3>Get Bids For Item</h3>
   * <p>Retrieves a paginated list of bids for a specific item. Only the seller of the item is
   * authorized to view these bids.</p>
   *
   * @param itemId      the unique identifier of the {@link Item} for which to retrieve bids.
   * @param currentUser the {@link User} attempting to view the bids. Must be the seller of the item.
   * @param pageable    the pagination information, including page number, size, and sort order.
   * @return a {@link Page} of {@link BidResponseDto} representing the bids for the specified item.
   * @throws EntityNotFoundException if the item with the given ID does not exist.
   * @throws BadCredentialsException if the {@code currentUser} is not the seller of the item.
   */
  public Page<BidResponseDto> getBidsForItem(Long itemId, User currentUser, Pageable pageable) {
    log.info("User ID {} attempting to fetch bids for item ID {}", currentUser.getId(), itemId);
    Item item = itemRepository.findById(itemId).orElseThrow(() -> {
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
   * <p>Deletes a bid placed by a specific bidder on a specific item. Currently, there are no
   * restrictions on the status of the bid for deletion.</p>
   *
   * @param itemId the unique identifier of the {@link Item} on which the bid was placed.
   * @param bidder the {@link User} who placed the bid.
   * @throws EntityNotFoundException if no bid is found for the given item ID and bidder.
   */
  @Transactional
  public void deleteBidByItemIdAndBidder(Long itemId, User bidder) {
    log.info("Attempting to delete bid for item ID {} by bidder ID {}", itemId, bidder.getId());
    Bid bid = bidRepository.findByItemIdAndBidderId(itemId, bidder.getId()).orElseThrow(() -> {
      log.warn("Cannot delete bid: Bid not found for item ID {} and bidder ID {}", itemId,
          bidder.getId());
      return new EntityNotFoundException(
          "Bid not found for item ID: " + itemId + " and bidder ID: " + bidder.getId());
    });

    bidRepository.delete(bid);
    log.info("Bid deleted successfully for item ID {} by bidder ID {}", itemId, bidder.getId());
  }

  /**
   * <h3>Accept Bid</h3>
   * <p>Accepts a specific bid for an item. Only the seller of the item can accept a bid. Upon
   * accepting a bid, all other PENDING bids for the same item are automatically rejected.</p>
   *
   * @param itemId      the unique identifier of the {@link Item} for which the bid is being accepted.
   * @param bidderId    the unique identifier of the {@link User} whose bid is being accepted.
   * @param currentUser the {@link User} performing the action (must be the seller).
   * @throws EntityNotFoundException if the bid for the given item and bidder does not exist.
   * @throws BadCredentialsException if the {@code currentUser} is not the seller of the item.
   * @throws IllegalStateException   if the bid being accepted is not in {@link BidStatus#PENDING}
   *                                 status.
   */
  @Transactional
  public void acceptBid(Long itemId, Long bidderId, User currentUser) {
    log.info("User ID {} attempting to accept bid from bidder ID {} for item ID {}",
        currentUser.getId(), bidderId, itemId);
    Bid bid = bidRepository.findByItemIdAndBidderId(itemId, bidderId).orElseThrow(() -> {
      log.error("Accept Bid Error: Bid not found for item {} / bidder {}", itemId, bidderId);
      return new EntityNotFoundException("Bid not found");
    });

    Item item = bid.getItem();

    if (!item.getSeller().getId().equals(currentUser.getId())) {
      log.warn("Access Denied: User {} attempted to accept bid on item {} owned by {}",
          currentUser.getId(), itemId, item.getSeller().getId());
      throw new BadCredentialsException("Only the seller can accept bids");
    }

    // Check if the bid is PENDING before accepting
    if (bid.getStatus() != BidStatus.PENDING) {
      log.warn("Attempted to accept bid ID {} which is not PENDING (status: {})", bid.getId(),
          bid.getStatus());
      throw new IllegalStateException("Only PENDING bids can be accepted.");
    }

    // Optionally: Reject other pending bids for the same item
    List<Bid> otherBids = bidRepository.findByItemId(itemId);
    otherBids.stream().filter(
            other -> other.getStatus() == BidStatus.PENDING && !other.getId().equals(bid.getId()))
        .forEach(other -> {
          log.info("Auto-rejecting other pending bid ID {} for item {}", other.getId(), itemId);
          other.setStatus(BidStatus.REJECTED);
          bidRepository.save(other); // Save change for each rejected bid
        });

    bid.setStatus(BidStatus.ACCEPTED);
    bidRepository.save(bid);
    log.info("Bid ID {} accepted successfully for item ID {}", bid.getId(), itemId);
  }

  /**
   * <h3>Reject Bid</h3>
   * <p>Rejects a specific bid for an item. Only the seller of the item can reject a bid. This
   * operation is only applicable to bids in {@link BidStatus#PENDING} status.</p>
   *
   * @param itemId      the unique identifier of the {@link Item} for which the bid is being rejected.
   * @param bidderId    the unique identifier of the {@link User} whose bid is being rejected.
   * @param currentUser the {@link User} performing the action (must be the seller).
   * @throws EntityNotFoundException if the bid for the given item and bidder does not exist.
   * @throws BadCredentialsException if the {@code currentUser} is not the seller of the item.
   */
  @Transactional
  public void rejectBid(Long itemId, Long bidderId, User currentUser) {
    log.info("User ID {} attempting to reject bid from bidder ID {} for item ID {}",
        currentUser.getId(), bidderId, itemId);
    Bid bid = bidRepository.findByItemIdAndBidderId(itemId, bidderId).orElseThrow(() -> {
      log.error("Reject Bid Error: Bid not found for item {} / bidder {}", itemId, bidderId);
      return new EntityNotFoundException("Bid not found");
    });

    Item item = bid.getItem();

    if (!item.getSeller().getId().equals(currentUser.getId())) {
      log.warn("Access Denied: User {} attempted to reject bid on item {} owned by {}",
          currentUser.getId(), itemId, item.getSeller().getId());
      throw new BadCredentialsException("Only the seller can reject bids");
    }

    if (bid.getStatus() == BidStatus.PENDING) {
      bid.setStatus(BidStatus.REJECTED);
      bidRepository.save(bid);
      log.info("Bid ID {} rejected successfully for item ID {}", bid.getId(), itemId);
    } else {
      log.warn("Attempted to reject bid ID {} which is already in status {}", bid.getId(),
          bid.getStatus());
    }
  }

  /**
   * <h3>Update Bid</h3>
   * <p>Updates an existing bid for an item placed by a specific bidder. Allows the bidder to change
   * the bid amount and comment.</p>
   *
   * @param bidUpdateDto DTO containing the updated bid details (amount and comment).
   * @param itemId       the unique identifier of the {@link Item} for which the bid is being updated.
   * @param bidder       the {@link User} who placed the bid and is now updating it.
   * @return a {@link BidResponseDto} representing the updated {@link Bid} entity.
   * @throws EntityNotFoundException  if the item with the given ID does not exist or if no bid is
   *                                  found for the given item ID and bidder.
   * @throws IllegalArgumentException if the bid amount is lower than the current bid.
   */
  @Transactional
  public BidResponseDto updateBid(BidUpdateDto bidUpdateDto, Long itemId, User bidder) {
    if (!itemRepository.existsById(itemId)) {
      throw new EntityNotFoundException("Item not found with ID: " + itemId);
    }

    Bid bid = bidRepository.findByItemIdAndBidderId(itemId, bidder.getId()).orElseThrow(
        () -> new EntityNotFoundException(
            "Bid not found with item ID: " + itemId + " and bidder ID: " + bidder.getId()));
    if (bidUpdateDto.getAmount().compareTo(bid.getAmount()) < 1) {
      throw new IllegalArgumentException(
          "You can't lower the bid amount! (Old: " + bid.getAmount());
    }
    bid.setAmount(bidUpdateDto.getAmount());
    bid.setComment(bidUpdateDto.getComment());

    return mapToBidResponseDto(bidRepository.save(bid));
  }

  /**
   * <h3>Get Bids By Bidder ID</h3>
   * <p>Retrieves a paginated list of all bids placed by a specific user.</p>
   *
   * @param bidderId the unique identifier of the {@link User} whose bids are being retrieved.
   * @param pageable the pagination information, including page number, size, and sort order.
   * @return a {@link Page} of {@link BidResponseDto} representing the bids placed by the specified
   * user.
   */
  public Page<BidResponseDto> getBidsByBidderId(Long bidderId, Pageable pageable) {
    return bidRepository.findByBidderId(bidderId, pageable).map(this::mapToBidResponseDto);
  }

  /**
   * <h3>Map To Bid Response DTO</h3>
   * <p>Maps a {@link Bid} entity to a {@link BidResponseDto} for API responses.</p>
   *
   * @param bid the {@link Bid} entity to be mapped.
   * @return a {@link BidResponseDto} containing the relevant information from the {@code Bid}
   * entity, or {@code null} if the input {@code Bid} is {@code null}.
   */
  private BidResponseDto mapToBidResponseDto(Bid bid) {
    if (bid == null) {
      return null;
    }
    return BidResponseDto.builder().id(bid.getId())
        .itemId(bid.getItem() != null ? bid.getItem().getId() : null)
        .bidderId(bid.getBidder() != null ? bid.getBidder().getId() : null).bidderUsername(
            bid.getBidder() != null ? bid.getBidder().getDisplayName() : "Unknown Bidder")
        .amount(bid.getAmount()).comment(bid.getComment()).status(bid.getStatus())
        .createdAt(bid.getCreatedAt()).updatedAt(bid.getUpdatedAt()).build();
  }
}