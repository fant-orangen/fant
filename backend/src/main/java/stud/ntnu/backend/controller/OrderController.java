package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors; // Import Collectors

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping; // Import PutMapping
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.bid.BidCreateDto;
import stud.ntnu.backend.data.bid.BidResponseDto; // Import BidResponseDto
import stud.ntnu.backend.data.bid.BidUpdateDto;
import stud.ntnu.backend.model.Bid; // Keep Bid import if needed for getBidsByBidderId response type
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.service.OrderService;
import stud.ntnu.backend.service.UserService;

import java.security.Principal;

/**
 * <h2>OrderController</h2>
 * <p>Controller for managing order and bid operations.</p>
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;
  private final UserService userService;

  private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

  /**
   * Creates a new bid for an item from the authenticated user.
   *
   * @param bidCreateDto The bid details DTO.
   * @param principal    The authenticated user principal.
   * @return ResponseEntity indicating success (200 OK) or failure.
   */
  @PostMapping("/bid")
  public ResponseEntity<Void> createBid(
      @Valid @RequestBody BidCreateDto bidCreateDto,
      Principal principal) {
    User currentUser = userService.getCurrentUser(principal);
    logger.info("User ID {} creating bid for item ID {}", currentUser.getId(), bidCreateDto.getItemId());
    orderService.createBid(bidCreateDto, currentUser);
    // Return 200 OK on success (or consider 201 Created if returning the bid URI/ID)
    return ResponseEntity.ok().build();
  }

  /**
   * Retrieves all bids placed *by* the currently authenticated user.
   *
   * @param principal The authenticated user principal.
   * @return ResponseEntity containing a list of bids placed by the user.
   */
  @GetMapping("/bids")
  public ResponseEntity<List<Bid>> getUserBids(Principal principal) {
    Long userId = userService.getCurrentUserId(principal);
    logger.info("Fetching bids placed by user ID: {}", userId);
    List<Bid> bids = orderService.getBidsByBidderId(userId);
    // Note: Returning the raw Bid entity might expose too much info.
    // Consider mapping to BidResponseDto here as well if needed for consistency.
    return ResponseEntity.ok(bids);
  }

  /**
   * Retrieves all bids placed *on* a specific item, accessible only by the item's seller.
   *
   * @param itemId    The ID of the item whose bids are to be fetched.
   * @param principal The authenticated user principal (must be the seller).
   * @return ResponseEntity containing a list of bids for the specified item.
   */
  @GetMapping("/item/{itemId}")
  public ResponseEntity<List<BidResponseDto>> getBidsForItem(
      @Positive @PathVariable Long itemId,
      Principal principal) {
    User currentUser = userService.getCurrentUser(principal);
    logger.info("User ID {} requesting bids for item ID {}", currentUser.getId(), itemId);
    List<BidResponseDto> bids = orderService.getBidsForItem(itemId, currentUser);
    return ResponseEntity.ok(bids);
  }


  /**
   * Deletes a bid placed by the authenticated user on a specific item.
   *
   * @param itemId    The ID of the item associated with the bid.
   * @param principal The authenticated user principal (must be the bidder).
   * @return ResponseEntity indicating success (200 OK) or failure.
   */
  @DeleteMapping("/delete/{itemId}")
  public ResponseEntity<Void> deleteBid(
      @Positive @PathVariable Long itemId,
      Principal principal) {
    User currentUser = userService.getCurrentUser(principal);
    logger.info("User ID {} attempting to delete their bid for item ID {}", currentUser.getId(), itemId);
    orderService.deleteBidByItemIdAndBidder(itemId, currentUser);
    return ResponseEntity.ok().build();
  }

  /**
   * Accepts a bid on an item. Only accessible by the item's seller.
   *
   * @param itemId    The ID of the item being bid on.
   * @param bidderId  The ID of the user whose bid is being accepted.
   * @param principal The authenticated user principal (must be the seller).
   * @return ResponseEntity indicating success (200 OK) or failure.
   */
  @PostMapping("/accept") // Consider making this PUT or PATCH /bids/{bidId}/status
  public ResponseEntity<Void> acceptBid(
      @RequestParam @Positive Long itemId,
      @RequestParam @Positive Long bidderId,
      Principal principal) {
    User currentUser = userService.getCurrentUser(principal);
    logger.info("User ID {} attempting to accept bid from bidder ID {} for item ID {}", currentUser.getId(), bidderId, itemId);
    orderService.acceptBid(itemId, bidderId, currentUser);
    return ResponseEntity.ok().build();
  }

  /**
   * Rejects a bid on an item. Only accessible by the item's seller.
   *
   * @param itemId    The ID of the item being bid on.
   * @param bidderId  The ID of the user whose bid is being rejected.
   * @param principal The authenticated user principal (must be the seller).
   * @return ResponseEntity indicating success (200 OK) or failure.
   */
  @PostMapping("/reject") // Consider making this PUT or PATCH /bids/{bidId}/status
  public ResponseEntity<Void> rejectBid(
      @RequestParam @Positive Long itemId,
      @RequestParam @Positive Long bidderId,
      Principal principal) {
    User currentUser = userService.getCurrentUser(principal);
    logger.info("User ID {} attempting to reject bid from bidder ID {} for item ID {}", currentUser.getId(), bidderId, itemId);
    orderService.rejectBid(itemId, bidderId, currentUser);
    return ResponseEntity.ok().build();
  }

  /**
   * Updates an existing bid (amount/comment). Only accessible by the bidder.
   *
   * @param bidUpdateDto The DTO containing update details (itemId, new amount, new comment).
   * @param principal    The authenticated user principal (must be the bidder).
   * @return ResponseEntity indicating success (200 OK) or failure.
   */
  // Changed to PUT for semantic correctness (updating an existing resource)
  // The path could also be /bids or /bid/{itemId} if preferred
  @PutMapping("/bid")
  public ResponseEntity<Void> updateBid(
      @Valid @RequestBody BidUpdateDto bidUpdateDto,
      Principal principal) {
    User currentUser = userService.getCurrentUser(principal);
    logger.info("User ID {} attempting to update their bid for item ID {}", currentUser.getId(), bidUpdateDto.getItemId());
    // Pass bidderId from the authenticated user to the service for validation
    orderService.updateBid(
        bidUpdateDto.getItemId(),
        currentUser.getId(), // Use current user's ID as bidderId
        bidUpdateDto.getAmount(),
        bidUpdateDto.getComment());
    return ResponseEntity.ok().build();
  }
}