package stud.ntnu.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.data.bid.BidCreateDto;
import stud.ntnu.backend.data.bid.BidUpdateDto;
import stud.ntnu.backend.model.*;
import stud.ntnu.backend.model.enums.BidStatus;
import stud.ntnu.backend.model.enums.Role;
import stud.ntnu.backend.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <h2>Integration Tests for Bid Controller</h2>
 * <p>This class contains integration tests for the bidding-related endpoints of the application.</p>
 * <p>These tests verify the functionality for creating, updating, deleting, and retrieving bids, as well as accepting and rejecting bids by the item seller.</p>
 * <p>The tests utilize Spring Security's {@code @WithMockUser} annotation to simulate authenticated users with different roles to ensure proper authorization.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class BidControllerTest {

  /**
   * <h3>MockMvc</h3>
   * <p>A Spring MVC Test framework client used to perform HTTP requests and assertions.</p>
   */
  @Autowired
  private MockMvc mockMvc;

  /**
   * <h3>ObjectMapper</h3>
   * <p>Jackson's ObjectMapper for converting Java objects to JSON and vice versa.</p>
   */
  @Autowired
  private ObjectMapper objectMapper;

  /**
   * <h3>UserRepository</h3>
   * <p>Spring Data JPA repository for {@link User} entities.</p>
   */
  @Autowired
  private UserRepository userRepository;

  /**
   * <h3>ItemRepository</h3>
   * <p>Spring Data JPA repository for {@link Item} entities.</p>
   */
  @Autowired
  private ItemRepository itemRepository;

  /**
   * <h3>BidRepository</h3>
   * <p>Spring Data JPA repository for {@link Bid} entities.</p>
   */
  @Autowired
  private BidRepository bidRepository;

  /**
   * <h3>CategoryRepository</h3>
   * <p>Spring Data JPA repository for {@link Category} entities.</p>
   */
  @Autowired
  private CategoryRepository categoryRepository;

  /**
   * <h3>Test Bidder User</h3>
   * <p>A regular user entity used for placing bids in the tests.</p>
   */
  private User bidder;

  /**
   * <h3>Test Item</h3>
   * <p>An item entity used for testing bidding operations.</p>
   */
  private Item item;

  /**
   * <h3>Setup Method</h3>
   * <p>This method is executed before each test case. It sets up the necessary test data, including a seller, a bidder, and an item in the database.</p>
   */
  @BeforeEach
  void setUp() {
    // Create and save a seller user
    User seller = userRepository.save(
        User.builder().email("seller@example.com").passwordHash("hashed").displayName("Seller")
            .firstName("Sell").lastName("Er").phone("12345678").role(Role.USER).build());

    // Create and save a bidder user
    bidder = userRepository.save(
        User.builder().email("bidder@example.com").passwordHash("hashed").displayName("Bidder")
            .firstName("Bid").lastName("Der").phone("87654321").role(Role.USER).build());

    // Create and save a category
    Category category =
        categoryRepository.save(Category.builder().name("Test Category").imageUrl("url").build());

    // Create and save an item associated with the seller and category
    item = itemRepository.save(
        Item.builder().briefDescription("Item 1").fullDescription("Test item")
            .price(BigDecimal.valueOf(100)).seller(seller).category(category)
            .publishedAt(LocalDateTime.now()).build());
  }

  /**
   * <h3>Test Create Bid Endpoint (Authenticated User)</h3>
   * <p>Tests the {@code POST /api/orders/bid} endpoint to ensure an authenticated user can successfully create a new bid for an item.</p>
   * <p>This test uses {@code @WithMockUser} to simulate an authenticated bidder.</p>
   * <p>It verifies that the HTTP status code is 201 (Created) and that the created bid details are correctly returned in the response.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "bidder@example.com")
  void testCreateBid() throws Exception {
    // Create a BidCreateDto
    BidCreateDto dto = new BidCreateDto();
    dto.setItemId(item.getId());
    dto.setAmount(BigDecimal.valueOf(150));
    dto.setComment("This is my bid");

    // Perform the create bid request
    mockMvc.perform(post("/api/orders/bid").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))).andExpect(status().isCreated())
        .andExpect(jsonPath("$.amount", is(150)))
        .andExpect(jsonPath("$.comment", is("This is my bid")))
        .andExpect(jsonPath("$.itemId", is(item.getId().intValue())));
  }

  /**
   * <h3>Test Update Bid Endpoint (Authenticated User)</h3>
   * <p>Tests the {@code PUT /api/orders/update/{itemId}} endpoint to ensure an authenticated user can successfully update their existing bid for an item.</p>
   * <p>This test uses {@code @WithMockUser} to simulate the bidder who placed the initial bid.</p>
   * <p>It first creates a bid, then attempts to update it, verifying that the HTTP status code is 200 (OK) and the updated bid details are returned.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "bidder@example.com")
  void testUpdateBid() throws Exception {
    // Create initial bid
    bidRepository.save(
        Bid.builder().amount(BigDecimal.valueOf(100)).comment("Initial bid").bidder(bidder)
            .item(item).createdAt(LocalDateTime.now()).build());

    // Create a BidUpdateDto with updated bid details
    BidUpdateDto updateDto = new BidUpdateDto();
    updateDto.setAmount(BigDecimal.valueOf(200));
    updateDto.setComment("Updated offer");

    // Perform the update bid request
    mockMvc.perform(put("/api/orders/update/{itemId}", item.getId()).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDto))).andExpect(status().isOk())
        .andExpect(jsonPath("$.amount", is(200)))
        .andExpect(jsonPath("$.comment", is("Updated offer")));
  }

  /**
   * <h3>Test Delete Bid Endpoint (Authenticated User)</h3>
   * <p>Tests the {@code DELETE /api/orders/delete/{itemId}} endpoint to ensure an authenticated user can successfully delete their bid for an item.</p>
   * <p>This test uses {@code @WithMockUser} to simulate the bidder who placed the bid.</p>
   * <p>It creates a bid and then attempts to delete it, verifying that the HTTP status code is 204 (No Content) and that the bid is no longer present in the database.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "bidder@example.com")
  void testDeleteBid() throws Exception {
    // Save a bid to be deleted
    bidRepository.save(
        Bid.builder().amount(BigDecimal.valueOf(120)).comment("To delete").bidder(bidder).item(item)
            .createdAt(LocalDateTime.now()).build());

    // Perform the delete bid request
    mockMvc.perform(delete("/api/orders/delete/{itemId}", item.getId()).with(csrf()))
        .andExpect(status().isNoContent());

    // Verify that the bid is no longer in the database
    assertTrue(bidRepository.findByItemIdAndBidderId(item.getId(), bidder.getId()).isEmpty());
  }

  /**
   * <h3>Test Get User Bids Endpoint (Authenticated User)</h3>
   * <p>Tests the {@code GET /api/orders/bids} endpoint to ensure an authenticated user can retrieve a paginated list of their own bids.</p>
   * <p>This test uses {@code @WithMockUser} to simulate the bidder.</p>
   * <p>It creates a bid and then retrieves the user's bids, verifying that the HTTP status code is 200 (OK) and that the response contains at least one bid associated with the authenticated user.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "bidder@example.com")
  void testGetUserBids() throws Exception {
    // Save a bid by the test bidder
    bidRepository.save(
        Bid.builder().amount(BigDecimal.valueOf(130)).comment("My bid").bidder(bidder).item(item)
            .createdAt(LocalDateTime.now()).build());

    // Perform the get user bids request
    mockMvc.perform(get("/api/orders/bids").param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
        .andExpect(jsonPath("$.content[0].bidderId", is(bidder.getId().intValue())));
  }

  /**
   * <h3>Test Accept Bid Endpoint (Authenticated Seller)</h3>
   * <p>Tests the {@code POST /api/orders/accept} endpoint to ensure the seller of an item can accept a bid.</p>
   * <p>This test uses {@code @WithMockUser} to simulate the seller of the item.</p>
   * <p>It creates a pending bid and then simulates the seller accepting it, verifying that the HTTP status code is 204 (No Content) and that the bid status is updated to 'ACCEPTED' in the database.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "seller@example.com")
  void testAcceptBid() throws Exception {
    // Save a pending bid for the item
    Bid bid = bidRepository.save(
        Bid.builder().amount(BigDecimal.valueOf(140)).comment("Please accept").bidder(bidder)
            .item(item).createdAt(LocalDateTime.now()).status(BidStatus.PENDING).build());

    // Perform the accept bid request
    mockMvc.perform(post("/api/orders/accept").param("itemId", item.getId().toString())
            .param("bidderId", bidder.getId().toString()).with(csrf()))
        .andExpect(status().isNoContent());

    // Verify that the bid status has been updated to ACCEPTED
    Bid accepted = bidRepository.findById(bid.getId()).orElseThrow();
    assertEquals("ACCEPTED", accepted.getStatus().name());
  }

  /**
   * <h3>Test Reject Bid Endpoint (Authenticated Seller)</h3>
   * <p>Tests the {@code POST /api/orders/reject} endpoint to ensure the seller of an item can reject a bid.</p>
   * <p>This test uses {@code @WithMockUser} to simulate the seller of the item.</p>
   * <p>It creates a pending bid and then simulates the seller rejecting it, verifying that the HTTP status code is 204 (No Content) and that the bid status is updated to 'REJECTED' in the database.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "seller@example.com")
  void testRejectBid() throws Exception {
    // Save a pending bid for the item
    Bid bid = bidRepository.save(
        Bid.builder().amount(BigDecimal.valueOf(99)).comment("Please don't").bidder(bidder)
            .item(item).createdAt(LocalDateTime.now()).status(BidStatus.PENDING).build());

    // Perform the reject bid request
    mockMvc.perform(post("/api/orders/reject").param("itemId", item.getId().toString())
            .param("bidderId", bidder.getId().toString()).with(csrf()))
        .andExpect(status().isNoContent());

    // Verify that the bid status has been updated to REJECTED
    Bid rejected = bidRepository.findById(bid.getId()).orElseThrow();
    assertEquals("REJECTED", rejected.getStatus().name());
  }
}