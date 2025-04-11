package stud.ntnu.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.data.message.MessageReadRequestDto;
import stud.ntnu.backend.model.Category;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.model.enums.Role;
import stud.ntnu.backend.repository.CategoryRepository;
import stud.ntnu.backend.repository.ItemRepository;
import stud.ntnu.backend.repository.UserRepository;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <h2>Integration Tests for Message Controller</h2>
 * <p>This class contains integration tests for the messaging functionalities between users regarding items.</p>
 * <p>These tests verify the ability of an authenticated user to initiate conversations about an item, retrieve their conversations, retrieve messages related to a specific item, and mark messages as read.</p>
 * <p>The tests utilize Spring Security's {@code @WithMockUser} annotation to simulate an authenticated user.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MessageControllerTest {

  /**
   * <h3>MockMvc</h3>
   * <p>A Spring MVC Test framework client used to perform HTTP requests and assertions.</p>
   */
  @Autowired
  private MockMvc mockMvc;

  /**
   * <h3>UserRepository</h3>
   * <p>Spring Data JPA repository for {@link User} entities.</p>
   */
  @Autowired
  private UserRepository userRepository;

  /**
   * <h3>CategoryRepository</h3>
   * <p>Spring Data JPA repository for {@link Category} entities.</p>
   */
  @Autowired
  private CategoryRepository categoryRepository;

  /**
   * <h3>ItemRepository</h3>
   * <p>Spring Data JPA repository for {@link Item} entities.</p>
   */
  @Autowired
  private ItemRepository itemRepository;

  /**
   * <h3>ObjectMapper</h3>
   * <p>Jackson's ObjectMapper for converting Java objects to JSON and vice versa.</p>
   */
  @Autowired
  private ObjectMapper objectMapper;

  /**
   * <h3>Test User (Sender)</h3>
   * <p>A regular user entity used for initiating and participating in conversations.</p>
   */
  private User user;

  /**
   * <h3>Test User (Seller)</h3>
   * <p>Another regular user entity who is the seller of the item being discussed.</p>
   */
  private User user1;

  /**
   * <h3>Test Item</h3>
   * <p>An item entity about which conversations can be initiated.</p>
   */
  private Item item;

  /**
   * <h3>Setup Method</h3>
   * <p>This method is executed before each test case. It sets up two test users (one buyer, one seller) and a test item in the database.</p>
   */
  @BeforeEach
  void setUp() {
    // Create and save a test user (potential buyer)
    user = userRepository.save(
        User.builder().email("msguser@example.com").displayName("MsgUser").firstName("Msg")
            .lastName("User").passwordHash("secret") // assume encoded
            .phone("+4712345678").role(Role.USER).build());

    // Create and save another test user (seller)
    user1 = userRepository.save(
        User.builder().email("msguser1@example.com").displayName("MsgUser1").firstName("Msg1")
            .lastName("User1").passwordHash("secret1") // assume encoded
            .phone("+4712345679").role(Role.USER).build());

    // Create and save a test category
    Category category =
        categoryRepository.save(Category.builder().name("Messages Cat").imageUrl("img").build());

    // Create and save a test item, sold by user1
    item = itemRepository.save(
        Item.builder().briefDescription("Item for Messaging").fullDescription("Message-worthy")
            .price(BigDecimal.valueOf(100.0)).seller(user1).category(category).build());
  }

  /**
   * <h3>Test Initiate Conversation Endpoint (Authenticated User)</h3>
   * <p>Tests the {@code POST /api/messaging/conversations/initiate} endpoint to ensure an authenticated user can successfully initiate a new conversation regarding a specific item.</p>
   * <p>This test uses {@code @WithMockUser} to simulate the user initiating the conversation.</p>
   * <p>It verifies that the HTTP status code is 200 (OK) and that the response contains a 'conversationId', indicating a successful initiation.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "msguser@example.com", roles = {"USER"})
  public void testInitiateConversation() throws Exception {
    // Perform the initiate conversation request
    mockMvc.perform(
            post("/api/messaging/conversations/initiate").param("itemId", item.getId().toString())
                .with(csrf())).andExpect(status().isOk())
        .andExpect(jsonPath("$.conversationId").exists());
  }

  /**
   * <h3>Test Get User Conversations Endpoint (Authenticated User)</h3>
   * <p>Tests the {@code GET /api/messaging/conversations} endpoint to ensure an authenticated user can retrieve a list of their conversations.</p>
   * <p>This test uses {@code @WithMockUser} to simulate the user retrieving their conversations.</p>
   * <p>It verifies that the HTTP status code is 200 (OK), implying a successful retrieval of conversations. The content of the conversations is not specifically asserted here.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "msguser@example.com", roles = {"USER"})
  public void testGetConversations() throws Exception {
    // Perform the get conversations request
    mockMvc.perform(get("/api/messaging/conversations")).andExpect(status().isOk());
  }

  /**
   * <h3>Test Get Messages for Item Endpoint (Authenticated User)</h3>
   * <p>Tests the {@code GET /api/messaging/messages} endpoint to ensure an authenticated user can retrieve messages related to a specific item, with pagination.</p>
   * <p>This test uses {@code @WithMockUser} to simulate the user retrieving messages.</p>
   * <p>It verifies that the HTTP status code is 200 (OK), implying a successful retrieval of messages. The content of the messages is not specifically asserted here.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "msguser@example.com", roles = {"USER"})
  public void testGetItemMessages() throws Exception {
    // Perform the get item messages request with pagination parameters
    mockMvc.perform(
        get("/api/messaging/messages").param("itemId", item.getId().toString()).param("page", "0")
            .param("size", "10")).andExpect(status().isOk());
  }

  /**
   * <h3>Test Mark Messages as Read Endpoint (Authenticated User)</h3>
   * <p>Tests the {@code POST /api/messaging/readall} endpoint to ensure an authenticated user can mark a list of messages as read.</p>
   * <p>This test uses {@code @WithMockUser} to simulate the user marking messages as read.</p>
   * <p>It creates a {@link MessageReadRequestDto} with a list of message IDs (assuming they exist) and verifies that the HTTP status code is 200 (OK), implying a successful update of the message statuses.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "msguser@example.com", roles = {"USER"})
  public void testMarkMessagesAsRead() throws Exception {
    // Create a request DTO with a list of message IDs to mark as read
    MessageReadRequestDto request =
        new MessageReadRequestDto(List.of(1L, 2L, 3L)); // assuming mock IDs

    // Perform the mark messages as read request
    mockMvc.perform(post("/api/messaging/readall").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)).with(csrf())).andExpect(status().isOk());
  }
}