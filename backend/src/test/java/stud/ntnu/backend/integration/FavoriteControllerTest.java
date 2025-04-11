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

/**
 * <h2>Integration Tests for Favorite Controller</h2>
 * <p>This class contains integration tests for the user's favorite items functionality.</p>
 * <p>These tests verify the ability of an authenticated user to add, attempt to add again, and delete items from their favorites list.</p>
 * <p>The tests utilize Spring Security's {@code @WithMockUser} annotation to simulate an authenticated user.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class FavoriteControllerTest {

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
   * <h3>ItemRepository</h3>
   * <p>Spring Data JPA repository for {@link Item} entities.</p>
   */
  @Autowired
  private ItemRepository itemRepository;

  /**
   * <h3>CategoryRepository</h3>
   * <p>Spring Data JPA repository for {@link Category} entities.</p>
   */
  @Autowired
  private CategoryRepository categoryRepository;

  /**
   * <h3>Test User</h3>
   * <p>A regular user entity used for testing favorite item operations.</p>
   */
  private User user;

  /**
   * <h3>Test Item</h3>
   * <p>An item entity used for testing adding and deleting from favorites.</p>
   */
  private Item item;

  /**
   * <h3>Setup Method</h3>
   * <p>This method is executed before each test case. It sets up a test user and a test item in the database.</p>
   */
  @BeforeEach
  void setUp() {
    // Create and save a test user
    user = userRepository.save(
        User.builder().email("favuser@example.com").displayName("Fav User").firstName("Fav")
            .lastName("User").passwordHash("$2a$10$hashedpassword").phone("+4711223344")
            .role(Role.USER).build());

    // Create and save a test category
    Category category =
        categoryRepository.save(Category.builder().name("Favorites Cat").imageUrl("img").build());

    // Create and save a test item
    item = itemRepository.save(
        Item.builder().briefDescription("Favoritable Item").fullDescription("Description")
            .price(BigDecimal.valueOf(100.0)).category(category).seller(user).build());
  }

  /**
   * <h3>Test Add Item to Favorites (Authenticated User)</h3>
   * <p>Tests the {@code POST /api/favorite/{itemId}} endpoint to ensure an authenticated user can successfully add an item to their favorites.</p>
   * <p>This test uses {@code @WithMockUser} to simulate an authenticated user.</p>
   * <p>It verifies that the HTTP status code is 204 (No Content), indicating a successful operation.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "favuser@example.com", roles = {"USER"})
  public void testAddFavorite() throws Exception {
    // Perform the add favorite request
    mockMvc.perform(post("/api/favorite/{itemId}", item.getId()).with(csrf()))
        .andExpect(status().isNoContent());
  }

  /**
   * <h3>Test Add Item to Favorites Twice (Authenticated User) - Expects Error</h3>
   * <p>Tests the scenario where an authenticated user attempts to add the same item to their favorites list twice.</p>
   * <p>This test uses {@code @WithMockUser} to simulate an authenticated user.</p>
   * <p>It first adds the item successfully and then attempts to add it again, expecting an HTTP status code of 400 (Bad Request), indicating an {@code AlreadyFavoritedException} or similar constraint violation.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "favuser@example.com", roles = {"USER"})
  public void testAddFavoriteTwice_ThrowsException() throws Exception {
    // First attempt to add the item to favorites
    mockMvc.perform(post("/api/favorite/{itemId}", item.getId()).with(csrf()))
        .andExpect(status().isNoContent());

    // Second attempt to add the same item - expect a Bad Request status
    mockMvc.perform(post("/api/favorite/{itemId}", item.getId()).with(csrf()))
        .andExpect(status().isBadRequest());
  }

  /**
   * <h3>Test Delete Item from Favorites (Authenticated User)</h3>
   * <p>Tests the {@code DELETE /api/favorite/{itemId}} endpoint to ensure an authenticated user can successfully remove an item from their favorites list.</p>
   * <p>This test uses {@code @WithMockUser} to simulate an authenticated user.</p>
   * <p>It first adds the item to favorites and then attempts to delete it, verifying that the HTTP status code is 204 (No Content), indicating a successful deletion.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "favuser@example.com", roles = {"USER"})
  public void testDeleteFavorite() throws Exception {
    // Add the item to favorites first
    mockMvc.perform(post("/api/favorite/{itemId}", item.getId()).with(csrf()))
        .andExpect(status().isNoContent());

    // Now perform the delete favorite request
    mockMvc.perform(delete("/api/favorite/{itemId}", item.getId()).with(csrf()))
        .andExpect(status().isNoContent());
  }

  /**
   * <h3>Test Delete Item from Favorites (Authenticated User) - Not Found</h3>
   * <p>Tests the scenario where an authenticated user attempts to delete an item from their favorites that was never added.</p>
   * <p>This test uses {@code @WithMockUser} to simulate an authenticated user.</p>
   * <p>It attempts to delete an item without adding it first, expecting an HTTP status code of 404 (Not Found), indicating that the favorite relationship does not exist.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "favuser@example.com", roles = {"USER"})
  public void testDeleteFavorite_NotFound() throws Exception {
    // Attempt to delete a favorite that does not exist
    mockMvc.perform(delete("/api/favorite/{itemId}", item.getId()).with(csrf()))
        .andExpect(status().isNotFound());
  }
}