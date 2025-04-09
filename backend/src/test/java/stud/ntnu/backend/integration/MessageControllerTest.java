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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MessageControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UserRepository userRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private ItemRepository itemRepository;
  @Autowired private ObjectMapper objectMapper;

  private User user;
  private User user1;
  private Item item;

  @BeforeEach
  void setUp() {
    user = userRepository.save(User.builder()
        .email("msguser@example.com")
        .displayName("MsgUser")
        .firstName("Msg")
        .lastName("User")
        .passwordHash("secret") // assume encoded
        .phone("+4712345678")
        .role(Role.USER)
        .build());

    user1 = userRepository.save(User.builder()
        .email("msguser1@example.com")
        .displayName("MsgUser1")
        .firstName("Msg1")
        .lastName("User1")
        .passwordHash("secret1") // assume encoded
        .phone("+4712345679")
        .role(Role.USER)
        .build());

    Category category = categoryRepository.save(Category.builder()
        .name("Messages Cat")
        .imageUrl("img")
        .build());

    item = itemRepository.save(Item.builder()
        .briefDescription("Item for Messaging")
        .fullDescription("Message-worthy")
        .price(BigDecimal.valueOf(100.0))
        .seller(user1)
        .category(category)
        .build());
  }

  @Test
  @WithMockUser(username = "msguser@example.com", roles = {"USER"})
  public void testInitiateConversation() throws Exception {
    mockMvc.perform(post("/api/messaging/conversations/initiate")
            .param("itemId", item.getId().toString())
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.conversationId").exists());
  }

  @Test
  @WithMockUser(username = "msguser@example.com", roles = {"USER"})
  public void testGetConversations() throws Exception {
    mockMvc.perform(get("/api/messaging/conversations"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "msguser@example.com", roles = {"USER"})
  public void testGetItemMessages() throws Exception {
    mockMvc.perform(get("/api/messaging/messages")
            .param("itemId", item.getId().toString())
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "msguser@example.com", roles = {"USER"})
  public void testMarkMessagesAsRead() throws Exception {
    MessageReadRequestDto request = new MessageReadRequestDto(List.of(1L, 2L, 3L)); // assuming mock IDs

    mockMvc.perform(post("/api/messaging/readall")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .with(csrf()))
        .andExpect(status().isOk());
  }
}
