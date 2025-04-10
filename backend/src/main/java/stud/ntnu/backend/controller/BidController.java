package stud.ntnu.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import stud.ntnu.backend.data.bid.BidResponseDto;
import stud.ntnu.backend.data.bid.BidUpdateDto;
import stud.ntnu.backend.service.BidService;
import stud.ntnu.backend.service.UserService;

import java.security.Principal;

/**
 * <h2>BidController</h2>
 * <p>Controller for managing bid operations.</p>
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Bids", description = "Operations related to managing bids.")
public class BidController {

  /**
   * <h3>Bid Service</h3>
   * <p>Service handling bid operations.</p>
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
   * @return {@link ResponseEntity} containing the created {@link BidResponseDto} with HTTP status 201 (Created)
   */
  @PostMapping("/bid")
  @Operation(summary = "Create Bid", description = "Creates a new bid for an item by the authenticated user.")
  @ApiResponse(responseCode = "201", description = "Bid created successfully", content = @Content(schema = @Schema(implementation = BidResponseDto.class)))
  @ApiResponse(responseCode = "400", description = "Invalid bid details", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<BidResponseDto> createBid(@Valid @RequestBody
                                                  @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Bid details to create", required = true, content = @Content(schema = @Schema(implementation = BidCreateDto.class)))
                                                  BidCreateDto bidCreateDto,
                                                  @Parameter(hidden = true) Principal principal) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(bidService.createBid(bidCreateDto, userService.getCurrentUser(principal)));
  }

  /**
   * <h3>Get User Bids</h3>
   * <p>Retrieves all bids placed by the current user.</p>
   *
   * @param principal the authenticated user
   * @param pageable  pagination information
   * @return {@link ResponseEntity} containing a paginated list of {@link BidResponseDto}
   */
  @GetMapping("/bids")
  @Operation(summary = "Get User Bids", description = "Retrieves all bids placed by the authenticated user.")
  @ApiResponse(responseCode = "200", description = "List of user's bids", content = @Content(schema = @Schema(implementation = Page.class, subTypes = {
      BidResponseDto.class})))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Page<BidResponseDto>> getUserBids(
      @Parameter(hidden = true) Principal principal,
      @Parameter(description = "Pagination information (page number, size, sort)")
      Pageable pageable) {
    return ResponseEntity.ok(
        bidService.getBidsByBidderId(userService.getCurrentUserId(principal), pageable));
  }

  /**
   * <h3>Delete Bid</h3>
   * <p>Deletes a bid for an item placed by the authenticated user.</p>
   *
   * @param itemId    the ID of the item associated with the bid
   * @param principal the authenticated user
   * @return {@link ResponseEntity} with status NO_CONTENT if successful
   */
  @DeleteMapping("/delete/{itemId}")
  @Operation(summary = "Delete Bid", description = "Deletes a specific bid placed by the authenticated user for an item.")
  @ApiResponse(responseCode = "204", description = "Bid deleted successfully")
  @ApiResponse(responseCode = "404", description = "Bid not found or not owned by the user", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Void> deleteBid(
      @Parameter(description = "ID of the item associated with the bid to delete", required = true)
      @Positive @PathVariable Long itemId, @Parameter(hidden = true) Principal principal) {
    bidService.deleteBidByItemIdAndBidder(itemId, userService.getCurrentUser(principal));
    return ResponseEntity.noContent().build();
  }

  /**
   * <h3>Accept Bid</h3>
   * <p>Changes a bid's status to ACCEPTED.</p>
   *
   * @param itemId    ID of the item being bid on
   * @param bidderId  ID of the user who made the bid
   * @param principal the authenticated user (must be seller)
   * @return {@link ResponseEntity} with status NO_CONTENT if successful
   */
  @PostMapping("/accept")
  @Operation(summary = "Accept Bid", description = "Accepts a specific bid for an item by the seller.")
  @ApiResponse(responseCode = "204", description = "Bid accepted successfully")
  @ApiResponse(responseCode = "400", description = "Invalid item or bidder ID", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "403", description = "User not authorized to accept this bid", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "404", description = "Bid or item not found", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Void> acceptBid(
      @Parameter(description = "ID of the item for which the bid is being accepted", required = true)
      @RequestParam @Positive Long itemId,
      @Parameter(description = "ID of the bidder whose bid is being accepted", required = true)
      @RequestParam @Positive Long bidderId, @Parameter(hidden = true) Principal principal) {
    bidService.acceptBid(itemId, bidderId, userService.getCurrentUser(principal));
    return ResponseEntity.noContent().build();
  }

  /**
   * <h3>Reject Bid</h3>
   * <p>Changes a bid's status to REJECTED.</p>
   *
   * @param itemId    ID of the item being bid on
   * @param bidderId  ID of the user who made the bid
   * @param principal the authenticated user (must be seller)
   * @return {@link ResponseEntity} with status NO_CONTENT if successful
   */
  @PostMapping("/reject")
  @Operation(summary = "Reject Bid", description = "Rejects a specific bid for an item by the seller.")
  @ApiResponse(responseCode = "204", description = "Bid rejected successfully")
  @ApiResponse(responseCode = "400", description = "Invalid item or bidder ID", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "403", description = "User not authorized to reject this bid", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "404", description = "Bid or item not found", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Void> rejectBid(
      @Parameter(description = "ID of the item for which the bid is being rejected", required = true)
      @RequestParam @Positive Long itemId,
      @Parameter(description = "ID of the bidder whose bid is being rejected", required = true)
      @RequestParam @Positive Long bidderId, @Parameter(hidden = true) Principal principal) {
    bidService.rejectBid(itemId, bidderId, userService.getCurrentUser(principal));
    return ResponseEntity.noContent().build();
  }

  /**
   * <h3>Update Bid</h3>
   * <p>Updates an existing bid with new amount and/or comment.</p>
   *
   * @param bidUpdateDto the bid update details
   * @param itemId       the ID of the item associated with the bid to update
   * @param principal    the authenticated user
   * @return {@link ResponseEntity} containing the updated {@link BidResponseDto} if successful
   */
  @PutMapping("/update/{itemId}")
  @Operation(summary = "Update Bid", description = "Updates an existing bid for a specific item by the authenticated user.")
  @ApiResponse(responseCode = "200", description = "Bid updated successfully", content = @Content(schema = @Schema(implementation = BidResponseDto.class)))
  @ApiResponse(responseCode = "400", description = "Invalid bid update details", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "404", description = "Bid not found or not owned by the user", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<BidResponseDto> updateBid(@Valid @RequestBody
                                                  @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Bid details to update", required = true, content = @Content(schema = @Schema(implementation = BidUpdateDto.class)))
                                                  BidUpdateDto bidUpdateDto,
                                                  @Parameter(description = "ID of the item associated with the bid to update", required = true)
                                                  @Positive @PathVariable Long itemId,
                                                  @Parameter(hidden = true) Principal principal) {
    return ResponseEntity.ok(
        bidService.updateBid(bidUpdateDto, itemId, userService.getCurrentUser(principal)));
  }
}