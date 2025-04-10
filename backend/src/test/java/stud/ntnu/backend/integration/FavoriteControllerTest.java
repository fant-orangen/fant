package stud.ntnu.backend.integration;

import java.math.BigDecimal;
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
import stud.ntnu.backend.model.Category;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.model.enums.Role;
import stud.ntnu.backend.repository.CategoryRepository;
import stud.ntnu.backend.repository.ItemRepository;
import stud.ntnu.backend.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class FavoriteControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  @Autowired private ItemRepository itemRepository;

  @Autowired private CategoryRepository categoryRepository;

  private User user;
  private Item item;

  @BeforeEach
  void setUp() {
    user = userRepository.save(User.builder()
        .email("favuser@example.com")
        .displayName("Fav User")
        .firstName("Fav")
        .lastName("User")
        .passwordHash("$2a$10$hashedpassword")
        .phone("+4711223344")
        .role(Role.USER)
        .build());

    Category category = categoryRepository.save(Category.builder()
        .name("Favorites Cat")
        .imageUrl("img")
        .build());

    item = itemRepository.save(Item.builder()
        .briefDescription("Favoritable Item")
        .fullDescription("Description")
        .price(BigDecimal.valueOf(100.0))
        .category(category)
        .seller(user)
        .build());
  }

  @Test
  @WithMockUser(username = "favuser@example.com", roles = {"USER"})
  public void testAddFavorite() throws Exception {
    mockMvc.perform(post("/api/favorite/{itemId}", item.getId())
            .with(csrf()))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(username = "favuser@example.com", roles = {"USER"})
  public void testAddFavoriteTwice_ThrowsException() throws Exception {
    // First attempt
    mockMvc.perform(post("/api/favorite/{itemId}", item.getId())
            .with(csrf()))
        .andExpect(status().isNoContent());

    // Second attempt - expect 400 (AlreadyFavoritedException)
    mockMvc.perform(post("/api/favorite/{itemId}", item.getId())
            .with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "favuser@example.com", roles = {"USER"})
  public void testDeleteFavorite() throws Exception {
    // Add favorite first
    mockMvc.perform(post("/api/favorite/{itemId}", item.getId())
            .with(csrf()))
        .andExpect(status().isNoContent());

    // Now delete it
    mockMvc.perform(delete("/api/favorite/{itemId}", item.getId())
            .with(csrf()))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(username = "favuser@example.com", roles = {"USER"})
  public void testDeleteFavorite_NotFound() throws Exception {
    // Try to delete a non-existent favorite
    mockMvc.perform(delete("/api/favorite/{itemId}", item.getId())
            .with(csrf()))
        .andExpect(status().isNotFound());
  }
}
