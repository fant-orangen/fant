package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.bid.BidCreateDto;
import stud.ntnu.backend.data.bid.BidUpdateDto;
import stud.ntnu.backend.model.Bid;
import stud.ntnu.backend.service.BidService;
import stud.ntnu.backend.service.UserService;

import java.security.Principal;

/**
 * <h2>OrderController</h2>
 * <p>Controller for managing order and bid operations.</p>
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class BidController {

  /**
   * <h3>Order Service</h3>
   * <p>Service handling order and bid operations.</p>
   */
  private final BidService bidService;

  /**
   * <h3>User Service</h3>
   * <p>Service for retrieving user information.</p>
   */
  private final UserService userService;

  /**
   * <h3>Create Bid</h3>
   * <p>Creates a new bid for an item from the authenticated user.</p>
   *
   * @param bidCreateDto the bid details
   * @param principal    the authenticated user
   * @return empty response with 204 No Content status
   */
  @PostMapping("/bid")
  public ResponseEntity<Bid> createBid(
      @Valid @RequestBody BidCreateDto bidCreateDto,
      Principal principal) {
    return ResponseEntity.ok(
        bidService.createBid(bidCreateDto, userService.getCurrentUser(principal)));
  }

  /**
   * <h3>Get User Bids</h3>
   * <p>Retrieves all bids placed by the current user.</p>
   *
   * @param principal the authenticated user
   * @return list of {@link Bid} entities placed by the user
   */
  @GetMapping("/bids")
  public ResponseEntity<Page<Bid>> getUserBids(Principal principal, Pageable pageable) {
    return ResponseEntity.ok(
        bidService.getBidsByBidderId(userService.getCurrentUserId(principal), pageable));
  }

  /**
   * <h3>Delete Bid</h3>
   * <p>Deletes a bid for an item placed by the authenticated user.</p>
   *
   * @param itemId    the ID of the item associated with the bid
   * @param principal the authenticated user
   * @return empty response with status OK
   */
  @DeleteMapping("/{itemId}")
  public ResponseEntity<Void> deleteBid(
      @Positive @PathVariable Long itemId,
      Principal principal) {
    bidService.deleteBidByItemIdAndBidder(itemId, userService.getCurrentUser(principal));
    return ResponseEntity.ok().build();
  }

  /**
   * <h3>Accept Bid</h3>
   * <p>Changes a bid's status to ACCEPTED.</p>
   *
   * @param itemId    ID of the item being bid on
   * @param bidderId  ID of the user who made the bid
   * @param principal the authenticated user (must be seller)
   * @return empty response with OK status
   */
  @PostMapping("/accept")
  public ResponseEntity<Void> acceptBid(
      @RequestParam @Positive Long itemId,
      @RequestParam @Positive Long bidderId,
      Principal principal) {
    bidService.acceptBid(itemId, bidderId, userService.getCurrentUser(principal));
    return ResponseEntity.ok().build();
  }

  /**
   * <h3>Reject Bid</h3>
   * <p>Changes a bid's status to REJECTED.</p>
   *
   * @param itemId    ID of the item being bid on
   * @param bidderId  ID of the user who made the bid
   * @param principal the authenticated user (must be seller)
   * @return empty response with OK status
   */
  @PostMapping("/reject")
  public ResponseEntity<Void> rejectBid(
      @RequestParam @Positive Long itemId,
      @RequestParam @Positive Long bidderId,
      Principal principal) {
    bidService.rejectBid(itemId, bidderId, userService.getCurrentUser(principal));
    return ResponseEntity.ok().build();
  }

  /**
   * <h3>Update Bid</h3>
   * <p>Updates an existing bid with new amount and/or comment.</p>
   *
   * @param bidUpdateDto the bid update details
   * @param principal    the authenticated user
   * @return empty response with OK status if successful
   */
  @PutMapping("/{itemId}")
  public ResponseEntity<Bid> updateBid(
      @Valid @RequestBody BidUpdateDto bidUpdateDto, @Positive @PathVariable Long itemId,
      Principal principal) {
    return ResponseEntity.ok(
        bidService.updateBid(bidUpdateDto, itemId, userService.getCurrentUser(principal)));
  }
}