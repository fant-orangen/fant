package stud.ntnu.backend.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import stud.ntnu.backend.data.bid.BidCreateDto;
import stud.ntnu.backend.data.bid.BidUpdateDto;
import stud.ntnu.backend.data.bid.BidResponseDto;
import stud.ntnu.backend.model.*;
import stud.ntnu.backend.model.enums.BidStatus;
import stud.ntnu.backend.repository.BidRepository;
import stud.ntnu.backend.repository.ItemRepository;

import java.math.BigDecimal;
import java.util.*;
import stud.ntnu.backend.service.BidService;

class BidServiceTest {

  @Mock
  private BidRepository bidRepository;
  @Mock
  private ItemRepository itemRepository;
  @InjectMocks
  private BidService bidService;

  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @Test
  void createBid_shouldThrow_whenItemNotFound() {
    BidCreateDto dto = new BidCreateDto(1L, BigDecimal.TEN, "test comment", BidStatus.PENDING);
    User user = new User();
    user.setId(42L);

    when(itemRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> bidService.createBid(dto, user));
  }

  @Test
  void createBid_shouldThrow_whenSellerTriesToBid() {
    User user = new User();
    user.setId(1L);

    Item item = new Item();
    item.setId(1L);
    item.setSeller(user);

    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

    BidCreateDto dto = new BidCreateDto(1L, BigDecimal.TEN, "comment", BidStatus.PENDING);

    assertThrows(IllegalArgumentException.class, () -> bidService.createBid(dto, user));
  }

  @Test
  void createBid_shouldUpdateExistingPendingBid() {
    User user = new User();
    user.setId(2L);

    Item item = new Item();
    item.setId(1L);
    User seller = new User();
    seller.setId(1L);
    item.setSeller(seller);

    Bid existingBid = Bid.builder()
        .id(1L)
        .item(item)
        .bidder(user)
        .status(BidStatus.PENDING)
        .amount(BigDecimal.ONE)
        .comment("old")
        .build();

    BidCreateDto dto = new BidCreateDto(1L, BigDecimal.TEN, "new comment", BidStatus.PENDING);

    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(bidRepository.findByItemIdAndBidderId(1L, 2L)).thenReturn(Optional.of(existingBid));
    when(bidRepository.save(any(Bid.class))).thenAnswer(invocation -> invocation.getArgument(0));

    BidResponseDto response = bidService.createBid(dto, user);

    assertEquals(BigDecimal.TEN, response.getAmount());
    assertEquals("new comment", response.getComment());
  }

  @Test
  void deleteBid_shouldRemoveBid_ifExists() {
    User user = new User();
    user.setId(10L);
    Bid bid = Bid.builder().id(1L).bidder(user).build();

    when(bidRepository.findByItemIdAndBidderId(1L, 10L)).thenReturn(Optional.of(bid));

    bidService.deleteBidByItemIdAndBidder(1L, user);

    verify(bidRepository).delete(bid);
  }

  @Test
  void deleteBid_shouldThrow_ifBidNotFound() {
    User user = new User();
    user.setId(10L);

    when(bidRepository.findByItemIdAndBidderId(1L, 10L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,
        () -> bidService.deleteBidByItemIdAndBidder(1L, user));
  }

  @Test
  void getBidsForItem_shouldThrow_ifNotSeller() {
    User user = new User();
    user.setId(2L);
    User seller = new User();
    seller.setId(1L);

    Item item = new Item();
    item.setId(1L);
    item.setSeller(seller);

    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

    assertThrows(BadCredentialsException.class, () ->
        bidService.getBidsForItem(1L, user, Pageable.unpaged()));
  }

  @Test
  void acceptBid_shouldThrow_ifUserNotSeller() {
    User seller = new User();
    seller.setId(1L);
    User currentUser = new User();
    currentUser.setId(2L);
    Item item = new Item();
    item.setSeller(seller);
    item.setId(1L);

    Bid bid = Bid.builder().id(1L).status(BidStatus.PENDING).item(item).build();

    when(bidRepository.findByItemIdAndBidderId(1L, 3L)).thenReturn(Optional.of(bid));

    assertThrows(BadCredentialsException.class, () ->
        bidService.acceptBid(1L, 3L, currentUser));
  }

  @Test
  void rejectBid_shouldSetStatusToRejected_ifPending() {
    User seller = new User();
    seller.setId(1L);
    User currentUser = new User();
    currentUser.setId(1L);
    Item item = new Item();
    item.setSeller(seller);
    item.setId(1L);

    Bid bid = Bid.builder().id(1L).status(BidStatus.PENDING).item(item).build();

    when(bidRepository.findByItemIdAndBidderId(1L, 2L)).thenReturn(Optional.of(bid));

    bidService.rejectBid(1L, 2L, currentUser);

    assertEquals(BidStatus.REJECTED, bid.getStatus());
    verify(bidRepository).save(bid);
  }

  @Test
  void updateBid_shouldUpdateCommentAndAmount() {
    User user = new User();
    user.setId(10L);
    BidUpdateDto dto = new BidUpdateDto(BigDecimal.valueOf(100), "Updated");
    Item item = new Item();
    item.setId(1L);

    Bid bid = Bid.builder()
        .id(1L).item(item).bidder(user)
        .status(BidStatus.PENDING).amount(BigDecimal.TEN).comment("Old")
        .build();

    when(itemRepository.existsById(1L)).thenReturn(true);
    when(bidRepository.findByItemIdAndBidderId(1L, 10L)).thenReturn(Optional.of(bid));
    when(bidRepository.save(any(Bid.class))).thenAnswer(inv -> inv.getArgument(0));

    BidResponseDto response = bidService.updateBid(dto, 1L, user);

    assertEquals(BigDecimal.valueOf(100), response.getAmount());
    assertEquals("Updated", response.getComment());
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }
}
