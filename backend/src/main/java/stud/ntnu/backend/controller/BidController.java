package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.bid.BidCreateDto;
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
  public ResponseEntity<Void> createBid(
      @Valid @RequestBody BidCreateDto bidCreateDto,
      Principal principal) {
    bidService.createBid(bidCreateDto, userService.getCurrentUser(principal));
    return ResponseEntity.noContent().build();
  }
}