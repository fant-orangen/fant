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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class BidControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ItemRepository itemRepository;
  @Autowired
  private BidRepository bidRepository;
  @Autowired
  private CategoryRepository categoryRepository;

  private User bidder;
  private Item item;

  @BeforeEach
  void setUp() {
    User seller = userRepository.save(User.builder()
        .email("seller@example.com")
        .passwordHash("hashed")
        .displayName("Seller")
        .firstName("Sell")
        .lastName("Er")
        .phone("12345678")
        .role(Role.USER)
        .build());

    bidder = userRepository.save(User.builder()
        .email("bidder@example.com")
        .passwordHash("hashed")
        .displayName("Bidder")
        .firstName("Bid")
        .lastName("Der")
        .phone("87654321")
        .role(Role.USER)
        .build());

    Category category = categoryRepository.save(Category.builder()
        .name("Test Category")
        .imageUrl("url")
        .build());

    item = itemRepository.save(Item.builder()
        .briefDescription("Item 1")
        .fullDescription("Test item")
        .price(BigDecimal.valueOf(100))
        .seller(seller)
        .category(category)
        .publishedAt(LocalDateTime.now())
        .build());
  }

  @Test
  @WithMockUser(username = "bidder@example.com")
  void testCreateBid() throws Exception {
    BidCreateDto dto = new BidCreateDto();
    dto.setItemId(item.getId());
    dto.setAmount(BigDecimal.valueOf(150));
    dto.setComment("This is my bid");

    mockMvc.perform(post("/api/orders/bid")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amount", is(150)))
        .andExpect(jsonPath("$.comment", is("This is my bid")))
        .andExpect(jsonPath("$.itemId", is(item.getId().intValue())));
  }

  @Test
  @WithMockUser(username = "bidder@example.com")
  void testUpdateBid() throws Exception {
    // Create initial bid
    bidRepository.save(Bid.builder()
        .amount(BigDecimal.valueOf(100))
        .comment("Initial bid")
        .bidder(bidder)
        .item(item)
        .createdAt(LocalDateTime.now())
        .build());

    BidUpdateDto updateDto = new BidUpdateDto();
    updateDto.setAmount(BigDecimal.valueOf(200));
    updateDto.setComment("Updated offer");

    mockMvc.perform(put("/api/orders/{itemId}", item.getId())
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amount", is(200)))
        .andExpect(jsonPath("$.comment", is("Updated offer")));
  }

  @Test
  @WithMockUser(username = "bidder@example.com")
  void testDeleteBid() throws Exception {
    // Save bid
    bidRepository.save(Bid.builder()
        .amount(BigDecimal.valueOf(120))
        .comment("To delete")
        .bidder(bidder)
        .item(item)
        .createdAt(LocalDateTime.now())
        .build());

    mockMvc.perform(delete("/api/orders/delete/{itemId}", item.getId())
            .with(csrf()))
        .andExpect(status().isOk());

    assertTrue(bidRepository.findByItemIdAndBidderId(item.getId(), bidder.getId()).isEmpty());
  }

  @Test
  @WithMockUser(username = "bidder@example.com")
  void testGetUserBids() throws Exception {
    bidRepository.save(Bid.builder()
        .amount(BigDecimal.valueOf(130))
        .comment("My bid")
        .bidder(bidder)
        .item(item)
        .createdAt(LocalDateTime.now())
        .build());

    mockMvc.perform(get("/api/orders/bids")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
        .andExpect(jsonPath("$.content[0].bidderId", is(bidder.getId().intValue())));
  }

  @Test
  @WithMockUser(username = "seller@example.com")
  void testAcceptBid() throws Exception {
    Bid bid = bidRepository.save(Bid.builder()
        .amount(BigDecimal.valueOf(140))
        .comment("Please accept")
        .bidder(bidder)
        .item(item)
        .createdAt(LocalDateTime.now())
        .status(BidStatus.PENDING)
        .build());

    mockMvc.perform(post("/api/orders/accept")
            .param("itemId", item.getId().toString())
            .param("bidderId", bidder.getId().toString())
            .with(csrf()))
        .andExpect(status().isOk());

    Bid accepted = bidRepository.findById(bid.getId()).orElseThrow();
    assertEquals("ACCEPTED", accepted.getStatus().name());
  }

  @Test
  @WithMockUser(username = "seller@example.com")
  void testRejectBid() throws Exception {
    Bid bid = bidRepository.save(Bid.builder()
        .amount(BigDecimal.valueOf(99))
        .comment("Please don't")
        .bidder(bidder)
        .item(item)
        .createdAt(LocalDateTime.now())
        .status(BidStatus.PENDING)
        .build());

    mockMvc.perform(post("/api/orders/reject")
            .param("itemId", item.getId().toString())
            .param("bidderId", bidder.getId().toString())
            .with(csrf()))
        .andExpect(status().isOk());

    Bid rejected = bidRepository.findById(bid.getId()).orElseThrow();
    assertEquals("REJECTED", rejected.getStatus().name());
  }
}
