package stud.ntnu.backend.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;

import jakarta.persistence.EntityNotFoundException;
import stud.ntnu.backend.data.bid.BidCreateDto;
import stud.ntnu.backend.data.bid.BidResponseDto;
import stud.ntnu.backend.data.bid.BidUpdateDto;
import stud.ntnu.backend.model.Bid;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.model.enums.BidStatus;
import stud.ntnu.backend.repository.BidRepository;
import stud.ntnu.backend.repository.ItemRepository;
import stud.ntnu.backend.service.BidService;

/**
 * <h2>Unit Tests for Bid Service</h2>
 * <p>This class contains unit tests for the {@link BidService} class, focusing on the business logic for creating, retrieving, updating, and deleting bids.</p>
 * <p>The tests utilize Mockito to mock the dependencies of the {@code BidService}, such as {@link BidRepository} and {@link ItemRepository}, allowing for isolated testing of the service's methods.</p>
 */
@ExtendWith(MockitoExtension.class)
public class BidServiceTest {

  /**
   * <h3>Mock BidRepository</h3>
   * <p>A Mockito mock for the {@link BidRepository} dependency, used to simulate database interactions related to bids.</p>
   */
  @Mock
  private BidRepository bidRepository;

  /**
   * <h3>Mock ItemRepository</h3>
   * <p>A Mockito mock for the {@link ItemRepository} dependency, used to simulate database interactions related to items.</p>
   */
  @Mock
  private ItemRepository itemRepository;

  /**
   * <h3>InjectMocks BidService</h3>
   * <p>The instance of {@link BidService} being tested, with its dependencies injected via Mockito.</p>
   */
  @InjectMocks
  private BidService bidService;

  /**
   * <h3>ArgumentCaptor for Bid</h3>
   * <p>A Mockito {@link ArgumentCaptor} used to capture and inspect {@link Bid} objects passed to the mocked {@link BidRepository}.</p>
   */
  @Captor
  private ArgumentCaptor<Bid> bidCaptor;

  /**
   * <h3>Test Seller User</h3>
   * <p>A {@link User} entity representing the seller in the test scenarios.</p>
   */
  private User seller;

  /**
   * <h3>Test Bidder User</h3>
   * <p>A {@link User} entity representing the bidder in the test scenarios.</p>
   */
  private User bidder;

  /**
   * <h3>Test Item</h3>
   * <p>An {@link Item} entity representing the item being bid on in the test scenarios.</p>
   */
  private Item item;

  /**
   * <h3>Test Bid</h3>
   * <p>A {@link Bid} entity representing a bid in the test scenarios.</p>
   */
  private Bid bid;

  /**
   * <h3>Test BidCreateDto</h3>
   * <p>A {@link BidCreateDto} used as input for creating bids in the test scenarios.</p>
   */
  private BidCreateDto bidCreateDto;

  /**
   * <h3>Test BidUpdateDto</h3>
   * <p>A {@link BidUpdateDto} used as input for updating bids in the test scenarios.</p>
   */
  private BidUpdateDto bidUpdateDto;

  /**
   * <h3>Test Pageable</h3>
   * <p>A {@link Pageable} instance used for testing paginated results.</p>
   */
  private Pageable pageable;

  /**
   * <h3>Setup Method</h3>
   * <p>This method is executed before each test case. It initializes the test data, including user entities (seller and bidder), an item entity, a bid entity, and DTOs for creating and updating bids.</p>
   */
  @BeforeEach
  void setUp() {
    // Set up test data
    seller = User.builder().id(1L).displayName("Seller").build();
    bidder = User.builder().id(2L).displayName("Bidder").build();
    item = Item.builder().id(1L).seller(seller).build();
    bid = Bid.builder().id(1L).item(item).bidder(bidder).amount(BigDecimal.valueOf(100.00))
        .comment("Test comment").status(BidStatus.PENDING).createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now()).build();

    bidCreateDto = new BidCreateDto();
    bidCreateDto.setItemId(1L);
    bidCreateDto.setAmount(BigDecimal.valueOf(100.00));
    bidCreateDto.setComment("Test comment");

    bidUpdateDto = new BidUpdateDto();
    bidUpdateDto.setAmount(BigDecimal.valueOf(150.00));
    bidUpdateDto.setComment("Updated comment");

    pageable = PageRequest.of(0, 10);
  }

  /**
   * <h3>Test Create Bid - New Bid - Success</h3>
   * <p>Tests the scenario where a new bid is successfully created. It verifies that the correct {@link Bid} object is saved to the repository with the expected attributes and that a {@link BidResponseDto} is returned.</p>
   */
  @Test
  void createBid_NewBid_Success() {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.empty());
    when(bidRepository.save(any(Bid.class))).thenReturn(bid);

    // Act
    BidResponseDto result = bidService.createBid(bidCreateDto, bidder);

    // Assert
    verify(bidRepository).save(bidCaptor.capture());
    Bid capturedBid = bidCaptor.getValue();

    assertEquals(item, capturedBid.getItem());
    assertEquals(bidder, capturedBid.getBidder());
    assertEquals(bidCreateDto.getAmount(), capturedBid.getAmount());
    assertEquals(bidCreateDto.getComment(), capturedBid.getComment());
    assertEquals(BidStatus.PENDING, capturedBid.getStatus());

    assertNotNull(result);
    assertEquals(bid.getId(), result.getId());
  }

  /**
   * <h3>Test Create Bid - Update Existing Pending Bid - Success</h3>
   * <p>Tests the scenario where an existing pending bid by the same bidder for the same item is updated. It verifies that the bid's amount and comment are updated and saved.</p>
   */
  @Test
  void createBid_UpdateExistingPendingBid_Success() {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.of(bid));
    when(bidRepository.save(any(Bid.class))).thenReturn(bid);

    // Act
    BidResponseDto result = bidService.createBid(bidCreateDto, bidder);

    // Assert
    verify(bidRepository).save(bidCaptor.capture());
    Bid capturedBid = bidCaptor.getValue();

    assertEquals(bidCreateDto.getAmount(), capturedBid.getAmount());
    assertEquals(bidCreateDto.getComment(), capturedBid.getComment());
    assertEquals(BidStatus.PENDING, capturedBid.getStatus());

    assertNotNull(result);
  }

  /**
   * <h3>Test Create Bid - Existing Non-Pending Bid - Throws Exception</h3>
   * <p>Tests the scenario where a bidder attempts to create a bid for an item they already have a non-pending bid on (e.g., ACCEPTED). It verifies that an {@link IllegalArgumentException} is thrown.</p>
   */
  @Test
  void createBid_ExistingNonPendingBid_ThrowsException() {
    // Arrange
    Bid acceptedBid =
        Bid.builder().id(1L).item(item).bidder(bidder).status(BidStatus.ACCEPTED).build();

    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(
        Optional.of(acceptedBid));

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> bidService.createBid(bidCreateDto, bidder));
    verify(bidRepository, never()).save(any(Bid.class));
  }

  /**
   * <h3>Test Create Bid - Item Not Found - Throws Exception</h3>
   * <p>Tests the scenario where the item specified in the {@link BidCreateDto} does not exist in the database. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void createBid_ItemNotFound_ThrowsException() {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> bidService.createBid(bidCreateDto, bidder));
    verify(bidRepository, never()).findByItemIdAndBidderId(anyLong(), anyLong());
    verify(bidRepository, never()).save(any(Bid.class));
  }

  /**
   * <h3>Test Create Bid - Seller Bidding - Throws Exception</h3>
   * <p>Tests the scenario where the seller of the item attempts to place a bid on their own item. It verifies that an {@link IllegalArgumentException} is thrown.</p>
   */
  @Test
  void createBid_SellerBidding_ThrowsException() {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> bidService.createBid(bidCreateDto, seller));
    verify(bidRepository, never()).findByItemIdAndBidderId(anyLong(), anyLong());
    verify(bidRepository, never()).save(any(Bid.class));
  }

  /**
   * <h3>Test Get Bids For Item - Seller Accessing - Success</h3>
   * <p>Tests the scenario where the seller of an item successfully retrieves the bids for that item. It verifies that a paginated list of {@link BidResponseDto} is returned.</p>
   */
  @Test
  void getBidsForItem_SellerAccessing_Success() {
    // Arrange
    List<Bid> bids = Collections.singletonList(bid);
    Page<Bid> bidPage = new PageImpl<>(bids, pageable, bids.size());

    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
    when(bidRepository.findByItemId(anyLong(), any(Pageable.class))).thenReturn(bidPage);

    // Act
    Page<BidResponseDto> result = bidService.getBidsForItem(1L, seller, pageable);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(bid.getId(), result.getContent().getFirst().getId());
  }

  /**
   * <h3>Test Get Bids For Item - Non-Seller Accessing - Throws Exception</h3>
   * <p>Tests the scenario where a user who is not the seller of the item attempts to retrieve the bids for that item. It verifies that a {@link BadCredentialsException} is thrown due to unauthorized access.</p>
   */
  @Test
  void getBidsForItem_NonSellerAccessing_ThrowsException() {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

    // Act & Assert
    assertThrows(BadCredentialsException.class,
        () -> bidService.getBidsForItem(1L, bidder, pageable));
    verify(bidRepository, never()).findByItemId(anyLong(), any(Pageable.class));
  }

  /**
   * <h3>Test Get Bids For Item - Item Not Found - Throws Exception</h3>
   * <p>Tests the scenario where the item for which bids are being requested does not exist. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void getBidsForItem_ItemNotFound_ThrowsException() {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class,
        () -> bidService.getBidsForItem(1L, seller, pageable));
  }

  /**
   * <h3>Test Delete Bid By Item ID and Bidder - Success</h3>
   * <p>Tests the successful deletion of a bid by its item ID and the ID of the bidder who placed the bid.</p>
   */
  @Test
  void deleteBidByItemIdAndBidder_Success() {
    // Arrange
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.of(bid));

    // Act
    bidService.deleteBidByItemIdAndBidder(1L, bidder);

    // Assert
    verify(bidRepository).delete(bid);
  }

  /**
   * <h3>Test Delete Bid By Item ID and Bidder - Bid Not Found - Throws Exception</h3>
   * <p>Tests the scenario where the bid to be deleted does not exist. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void deleteBidByItemIdAndBidder_BidNotFound_ThrowsException() {
    // Arrange
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class,
        () -> bidService.deleteBidByItemIdAndBidder(1L, bidder));
    verify(bidRepository, never()).delete(any(Bid.class));
  }

  /**
   * <h3>Test Accept Bid - Success</h3>
   * <p>Tests the successful acceptance of a bid by the seller. It verifies that the accepted bid's status is updated to ACCEPTED and other pending bids for the same item are rejected.</p>
   */
  @Test
  void acceptBid_Success() {
    // Arrange
    List<Bid> otherBids = Arrays.asList(
        Bid.builder().id(2L).item(item).bidder(User.builder().id(3L).build())
            .status(BidStatus.PENDING).build(), bid);

    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.of(bid));
    when(bidRepository.findByItemId(anyLong())).thenReturn(otherBids);

    // Act
    bidService.acceptBid(1L, 2L, seller);

    // Assert
    verify(bidRepository, times(2)).save(bidCaptor.capture());
    List<Bid> capturedBids = bidCaptor.getAllValues();

    // The first saved bid should be the other pending bid that gets rejected
    assertEquals(BidStatus.REJECTED, capturedBids.get(0).getStatus());

    // The second saved bid should be the accepted bid
    assertEquals(BidStatus.ACCEPTED, capturedBids.get(1).getStatus());
  }

  /**
   * <h3>Test Accept Bid - Not Seller - Throws Exception</h3>
   * <p>Tests the scenario where a user who is not the seller attempts to accept a bid. It verifies that a {@link BadCredentialsException} is thrown.</p>
   */
  @Test
  void acceptBid_NotSeller_ThrowsException() {
    // Arrange
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.of(bid));

    // Act & Assert
    assertThrows(BadCredentialsException.class, () -> bidService.acceptBid(1L, 2L, bidder));
    verify(bidRepository, never()).save(any(Bid.class));
  }

  /**
   * <h3>Test Accept Bid - Bid Not Found - Throws Exception</h3>
   * <p>Tests the scenario where the bid to be accepted does not exist. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void acceptBid_BidNotFound_ThrowsException() {
    // Arrange
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> bidService.acceptBid(1L, 2L, seller));
  }

  /**
   * <h3>Test Reject Bid - Success</h3>
   * <p>Tests the successful rejection of a bid by the seller. It verifies that the rejected bid's status is updated to REJECTED.</p>
   */
  @Test
  void rejectBid_Success() {
    // Arrange
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.of(bid));

    // Act
    bidService.rejectBid(1L, 2L, seller);

    // Assert
    verify(bidRepository).save(bidCaptor.capture());
    Bid capturedBid = bidCaptor.getValue();

    assertEquals(BidStatus.REJECTED, capturedBid.getStatus());
  }

  /**
   * <h3>Test Reject Bid - Not Seller - Throws Exception</h3>
   * <p>Tests the scenario where a user who is not the seller attempts to reject a bid. It verifies that a {@link BadCredentialsException} is thrown.</p>
   */
  @Test
  void rejectBid_NotSeller_ThrowsException() {
    // Arrange
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.of(bid));

    // Act & Assert
    assertThrows(BadCredentialsException.class, () -> bidService.rejectBid(1L, 2L, bidder));
    verify(bidRepository, never()).save(any(Bid.class));
  }

  /**
   * <h3>Test Reject Bid - Bid Not Found - Throws Exception</h3>
   * <p>Tests the scenario where the bid to be rejected does not exist. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void rejectBid_BidNotFound_ThrowsException() {
    // Arrange
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> bidService.rejectBid(1L, 2L, seller));
  }

  /**
   * <h3>Test Reject Bid - Already Processed - No Error</h3>
   * <p>Tests the scenario where the bid to be rejected has already been processed (e.g., ACCEPTED). It verifies that the service does not attempt to modify the bid.</p>
   */
  @Test
  void rejectBid_AlreadyProcessed_NoError() {
    // Arrange
    Bid acceptedBid =
        Bid.builder().id(1L).item(item).bidder(bidder).status(BidStatus.ACCEPTED).build();

    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(
        Optional.of(acceptedBid));

    // Act
    bidService.rejectBid(1L, 2L, seller);

    // Assert - Should not save or modify the bid if it's not PENDING
    verify(bidRepository, never()).save(any(Bid.class));
  }

  /**
   * <h3>Test Update Bid - Success</h3>
   * <p>Tests the successful update of an existing bid. It verifies that the bid's amount and comment are updated and saved.</p>
   */
  @Test
  void updateBid_Success() {
    // Arrange
    when(itemRepository.existsById(anyLong())).thenReturn(true);
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.of(bid));
    when(bidRepository.save(any(Bid.class))).thenReturn(bid);

    // Act
    BidResponseDto result = bidService.updateBid(bidUpdateDto, 1L, bidder);

    // Assert
    verify(bidRepository).save(bidCaptor.capture());
    Bid capturedBid = bidCaptor.getValue();

    assertEquals(bidUpdateDto.getAmount(), capturedBid.getAmount());
    assertEquals(bidUpdateDto.getComment(), capturedBid.getComment());

    assertNotNull(result);
  }

  /**
   * <h3>Test Update Bid - Item Not Found - Throws Exception</h3>
   * <p>Tests the scenario where the item associated with the bid update does not exist. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void updateBid_ItemNotFound_ThrowsException() {
    // Arrange
    when(itemRepository.existsById(anyLong())).thenReturn(false);

    // Act & Assert
    assertThrows(EntityNotFoundException.class,
        () -> bidService.updateBid(bidUpdateDto, 1L, bidder));
    verify(bidRepository, never()).findByItemIdAndBidderId(anyLong(), anyLong());
  }

  /**
   * <h3>Test Update Bid - Bid Not Found - Throws Exception</h3>
   * <p>Tests the scenario where the bid to be updated does not exist. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void updateBid_BidNotFound_ThrowsException() {
    // Arrange
    when(itemRepository.existsById(anyLong())).thenReturn(true);
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class,
        () -> bidService.updateBid(bidUpdateDto, 1L, bidder));
    verify(bidRepository, never()).save(any(Bid.class));
  }

  /**
   * <h3>Test Get Bids By Bidder ID - Success</h3>
   * <p>Tests the successful retrieval of bids for a specific bidder, returning a paginated list of {@link BidResponseDto}.</p>
   */
  @Test
  void getBidsByBidderId_Success() {
    // Arrange
    List<Bid> bids = Collections.singletonList(bid);
    Page<Bid> bidPage = new PageImpl<>(bids, pageable, bids.size());

    when(bidRepository.findByBidderId(anyLong(), any(Pageable.class))).thenReturn(bidPage);

    // Act
    Page<BidResponseDto> result = bidService.getBidsByBidderId(2L, pageable);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(bid.getId(), result.getContent().getFirst().getId());
  }

  /**
   * <h3>Test Get Bids By Bidder ID - No Bids - Returns Empty Page</h3>
   * <p>Tests the scenario where a bidder has no bids. It verifies that an empty paginated list of {@link BidResponseDto} is returned.</p>
   */
  @Test
  void getBidsByBidderId_NoBids_ReturnsEmptyPage() {
    // Arrange
    Page<Bid> emptyPage = new PageImpl<>(List.of(), pageable, 0);
    when(bidRepository.findByBidderId(anyLong(), any(Pageable.class))).thenReturn(emptyPage);

    // Act
    Page<BidResponseDto> result = bidService.getBidsByBidderId(2L, pageable);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.getTotalElements());
    assertTrue(result.getContent().isEmpty());
  }
}