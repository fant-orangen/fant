package stud.ntnu.backend.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
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

@ExtendWith(MockitoExtension.class)
public class BidServiceTest {

  @Mock
  private BidRepository bidRepository;

  @Mock
  private ItemRepository itemRepository;

  @InjectMocks
  private BidService bidService;

  @Captor
  private ArgumentCaptor<Bid> bidCaptor;

  private User seller;
  private User bidder;
  private Item item;
  private Bid bid;
  private BidCreateDto bidCreateDto;
  private BidUpdateDto bidUpdateDto;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    // Set up test data
    seller = User.builder().id(1L).displayName("Seller").build();
    bidder = User.builder().id(2L).displayName("Bidder").build();
    item = Item.builder().id(1L).seller(seller).build();
    bid = Bid.builder()
        .id(1L)
        .item(item)
        .bidder(bidder)
        .amount(BigDecimal.valueOf(100.00))
        .comment("Test comment")
        .status(BidStatus.PENDING)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    bidCreateDto = new BidCreateDto();
    bidCreateDto.setItemId(1L);
    bidCreateDto.setAmount(BigDecimal.valueOf(100.00));
    bidCreateDto.setComment("Test comment");

    bidUpdateDto = new BidUpdateDto();
    bidUpdateDto.setAmount(BigDecimal.valueOf(150.00));
    bidUpdateDto.setComment("Updated comment");

    pageable = PageRequest.of(0, 10);
  }

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

  @Test
  void createBid_ExistingNonPendingBid_ThrowsException() {
    // Arrange
    Bid acceptedBid = Bid.builder()
        .id(1L)
        .item(item)
        .bidder(bidder)
        .status(BidStatus.ACCEPTED)
        .build();

    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.of(acceptedBid));

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> bidService.createBid(bidCreateDto, bidder));
    verify(bidRepository, never()).save(any(Bid.class));
  }

  @Test
  void createBid_ItemNotFound_ThrowsException() {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> bidService.createBid(bidCreateDto, bidder));
    verify(bidRepository, never()).findByItemIdAndBidderId(anyLong(), anyLong());
    verify(bidRepository, never()).save(any(Bid.class));
  }

  @Test
  void createBid_SellerBidding_ThrowsException() {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> bidService.createBid(bidCreateDto, seller));
    verify(bidRepository, never()).findByItemIdAndBidderId(anyLong(), anyLong());
    verify(bidRepository, never()).save(any(Bid.class));
  }

  @Test
  void getBidsForItem_SellerAccessing_Success() {
    // Arrange
    List<Bid> bids = Arrays.asList(bid);
    Page<Bid> bidPage = new PageImpl<>(bids, pageable, bids.size());

    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
    when(bidRepository.findByItemId(anyLong(), any(Pageable.class))).thenReturn(bidPage);

    // Act
    Page<BidResponseDto> result = bidService.getBidsForItem(1L, seller, pageable);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(bid.getId(), result.getContent().get(0).getId());
  }

  @Test
  void getBidsForItem_NonSellerAccessing_ThrowsException() {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

    // Act & Assert
    assertThrows(BadCredentialsException.class, () -> bidService.getBidsForItem(1L, bidder, pageable));
    verify(bidRepository, never()).findByItemId(anyLong(), any(Pageable.class));
  }

  @Test
  void getBidsForItem_ItemNotFound_ThrowsException() {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> bidService.getBidsForItem(1L, seller, pageable));
  }

  @Test
  void deleteBidByItemIdAndBidder_Success() {
    // Arrange
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.of(bid));

    // Act
    bidService.deleteBidByItemIdAndBidder(1L, bidder);

    // Assert
    verify(bidRepository).delete(bid);
  }

  @Test
  void deleteBidByItemIdAndBidder_BidNotFound_ThrowsException() {
    // Arrange
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> bidService.deleteBidByItemIdAndBidder(1L, bidder));
    verify(bidRepository, never()).delete(any(Bid.class));
  }

  @Test
  void acceptBid_Success() {
    // Arrange
    List<Bid> otherBids = Arrays.asList(
        Bid.builder().id(2L).item(item).bidder(User.builder().id(3L).build()).status(BidStatus.PENDING).build(),
        bid
    );

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

  @Test
  void acceptBid_NotSeller_ThrowsException() {
    // Arrange
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.of(bid));

    // Act & Assert
    assertThrows(BadCredentialsException.class, () -> bidService.acceptBid(1L, 2L, bidder));
    verify(bidRepository, never()).save(any(Bid.class));
  }

  @Test
  void acceptBid_BidNotPending_ThrowsException() {
    // Arrange
    Bid rejectedBid = Bid.builder()
        .id(1L)
        .item(item)
        .bidder(bidder)
        .status(BidStatus.REJECTED)
        .build();

    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.of(rejectedBid));

    // Act & Assert
    assertThrows(IllegalStateException.class, () -> bidService.acceptBid(1L, 2L, seller));
    verify(bidRepository, never()).findByItemId(anyLong());
  }

  @Test
  void acceptBid_BidNotFound_ThrowsException() {
    // Arrange
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> bidService.acceptBid(1L, 2L, seller));
  }

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

  @Test
  void rejectBid_NotSeller_ThrowsException() {
    // Arrange
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.of(bid));

    // Act & Assert
    assertThrows(BadCredentialsException.class, () -> bidService.rejectBid(1L, 2L, bidder));
    verify(bidRepository, never()).save(any(Bid.class));
  }

  @Test
  void rejectBid_BidNotFound_ThrowsException() {
    // Arrange
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> bidService.rejectBid(1L, 2L, seller));
  }

  @Test
  void rejectBid_AlreadyProcessed_NoError() {
    // Arrange
    Bid acceptedBid = Bid.builder()
        .id(1L)
        .item(item)
        .bidder(bidder)
        .status(BidStatus.ACCEPTED)
        .build();

    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.of(acceptedBid));

    // Act
    bidService.rejectBid(1L, 2L, seller);

    // Assert - Should not save or modify the bid if it's not PENDING
    verify(bidRepository, never()).save(any(Bid.class));
  }

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

  @Test
  void updateBid_ItemNotFound_ThrowsException() {
    // Arrange
    when(itemRepository.existsById(anyLong())).thenReturn(false);

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> bidService.updateBid(bidUpdateDto, 1L, bidder));
    verify(bidRepository, never()).findByItemIdAndBidderId(anyLong(), anyLong());
  }

  @Test
  void updateBid_BidNotFound_ThrowsException() {
    // Arrange
    when(itemRepository.existsById(anyLong())).thenReturn(true);
    when(bidRepository.findByItemIdAndBidderId(anyLong(), anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> bidService.updateBid(bidUpdateDto, 1L, bidder));
    verify(bidRepository, never()).save(any(Bid.class));
  }

  @Test
  void getBidsByBidderId_Success() {
    // Arrange
    List<Bid> bids = Arrays.asList(bid);
    Page<Bid> bidPage = new PageImpl<>(bids, pageable, bids.size());

    when(bidRepository.findByBidderId(anyLong(), any(Pageable.class))).thenReturn(bidPage);

    // Act
    Page<BidResponseDto> result = bidService.getBidsByBidderId(2L, pageable);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(bid.getId(), result.getContent().get(0).getId());
  }

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