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
import stud.ntnu.backend.data.category.CategoryRequestDto;
import stud.ntnu.backend.data.user.AdminUserUpdateDto;
import stud.ntnu.backend.model.Category;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.model.enums.Role;
import stud.ntnu.backend.repository.CategoryRepository;
import stud.ntnu.backend.repository.ItemRepository;
import stud.ntnu.backend.repository.UserRepository;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <h2>Integration Tests for Admin Controller</h2>
 * <p>This class contains integration tests for the administrative endpoints of the application.</p>
 * <p>These tests verify the security and functionality of the admin-specific operations, ensuring that only users with the 'ADMIN' role can access and modify user, category, and item data.</p>
 * <p>The tests utilize Spring Security's {@code @WithMockUser} annotation to simulate authenticated users with different roles.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AdminControllerTest {

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
   * <h3>Test User</h3>
   * <p>A regular user entity used for testing operations where admin privileges are not expected.</p>
   */
  private User testUser;

  /**
   * <h3>Admin User</h3>
   * <p>An admin user entity used for testing operations requiring admin privileges.</p>
   */
  private User adminUser;

  /**
   * <h3>Test Category</h3>
   * <p>A category entity used for testing category management operations.</p>
   */
  private Category testCategory;

  /**
   * <h3>Test Item</h3>
   * <p>An item entity used for testing item management operations.</p>
   */
  private Item testItem;

  /**
   * <h3>Setup Method</h3>
   * <p>This method is executed before each test case. It sets up the necessary test data, including a regular user and an admin user in the database.</p>
   */
  @BeforeEach
  public void setup() {
    // Create regular test user in the database
    testUser = User.builder().email("test@example.com").displayName("Test User").firstName("Test")
        .lastName("User").passwordHash(
            "$2a$10$dWj9sFMjHcN0M1QasCWBR.uSJDIbZJ3LVsLY4Dw7cXUl8Txa4JBYe") // hashed "password"
        .phone("+4712345678").role(Role.USER).build();
    testUser = userRepository.save(testUser);

    // Create admin user in the database
    adminUser =
        User.builder().email("admin@example.com").displayName("Admin User").firstName("Admin")
            .lastName("User").passwordHash(
                "$2a$10$dWj9sFMjHcN0M1QasCWBR.uSJDIbZJ3LVsLY4Dw7cXUl8Txa4JBYe") // hashed "password"
            .phone("+4711223344").role(Role.ADMIN).build();
    adminUser = userRepository.save(adminUser);

    // Create test category in the database
    testCategory =
        Category.builder().name("Test Category").imageUrl("https://example.com/image.jpg").build();
    testCategory = categoryRepository.save(testCategory);
  }

  /**
   * <h3>Test Update User Endpoint (Admin Access)</h3>
   * <p>Tests the {@code PUT /api/admin/users/{id}} endpoint to ensure an admin user can successfully update an existing user's details.</p>
   * <p>This test uses {@code @WithMockUser} to simulate an authenticated admin user.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
  public void testUpdateUser() throws Exception {
    // Create update DTO
    AdminUserUpdateDto userUpdateDto = new AdminUserUpdateDto();
    userUpdateDto.setEmail("updated@example.com");
    userUpdateDto.setDisplayName("Updated User");
    userUpdateDto.setFirstName("Updated");
    userUpdateDto.setLastName("User");
    userUpdateDto.setPhone("+47987654321");
    userUpdateDto.setPassword("Password123");
    userUpdateDto.setRole(Role.ADMIN);

    // Perform update request
    mockMvc.perform(put("/api/admin/users/{id}", testUser.getId()).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userUpdateDto))).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
        .andExpect(jsonPath("$.email", is("updated@example.com")))
        .andExpect(jsonPath("$.displayName", is("Updated User")))
        .andExpect(jsonPath("$.firstName", is("Updated")))
        .andExpect(jsonPath("$.lastName", is("User")))
        .andExpect(jsonPath("$.phone", is("+47987654321")))
        .andExpect(jsonPath("$.role", is("ADMIN")));

    // Verify the database was updated
    User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
    assertEquals("updated@example.com", updatedUser.getEmail());
    assertEquals("Updated User", updatedUser.getDisplayName());
    assertEquals(Role.ADMIN, updatedUser.getRole());
  }

  /**
   * <h3>Test Delete User Endpoint (Admin Access)</h3>
   * <p>Tests the {@code DELETE /api/admin/users/{id}} endpoint to ensure an admin user can successfully delete an existing user.</p>
   * <p>This test uses {@code @WithMockUser} to simulate an authenticated admin user.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
  public void testDeleteUser() throws Exception {
    // Perform delete request
    mockMvc.perform(delete("/api/admin/users/{id}", testUser.getId()).with(csrf()))
        .andExpect(status().isNoContent());

    // Verify the user was deleted from the database
    assertFalse(userRepository.existsById(testUser.getId()));
  }

  /**
   * <h3>Test Get All Users Endpoint (Admin Access)</h3>
   * <p>Tests the {@code GET /api/admin/users} endpoint to ensure an admin user can retrieve a paginated list of all users.</p>
   * <p>This test uses {@code @WithMockUser} to simulate an authenticated admin user.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
  public void testGetAllUsers() throws Exception {
    // Perform get all users request
    mockMvc.perform(get("/api/admin/users").param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(2))));
  }

  /**
   * <h3>Test Create Category Endpoint (Admin Access)</h3>
   * <p>Tests the {@code POST /api/admin/category} endpoint to ensure an admin user can successfully create a new category.</p>
   * <p>This test uses {@code @WithMockUser} to simulate an authenticated admin user.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
  public void testCreateCategory() throws Exception {
    // Create category DTO
    CategoryRequestDto categoryDto = new CategoryRequestDto();
    categoryDto.setName("New Category");
    categoryDto.setImageUrl("https://example.com/new-image.jpg");
    categoryDto.setParentId(null);

    // Perform create category request
    mockMvc.perform(post("/api/admin/category").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(categoryDto))).andExpect(status().isCreated())
        .andExpect(jsonPath("$.name", is("New Category")))
        .andExpect(jsonPath("$.imageUrl", is("https://example.com/new-image.jpg")));

    // Verify category was created in the database
    assertTrue(categoryRepository.existsByName("New Category"));
  }

  /**
   * <h3>Test Delete Item Endpoint (Admin Access)</h3>
   * <p>Tests the {@code DELETE /api/admin/item/{id}} endpoint to ensure an admin user can successfully delete an item.</p>
   * <p>This test uses {@code @WithMockUser} to simulate an authenticated admin user.</p>
   * <p>The test expects a 204 (No Content) if the item is successfully deleted or a 404 (Not Found) if the item does not exist.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
  public void testDeleteItem() throws Exception {
    // Create a test item in the database
    testItem = Item.builder().briefDescription("Test Item").fullDescription("A test item")
        .price(BigDecimal.TEN).category(testCategory).seller(testUser).build();
    testItem = itemRepository.save(testItem);

    // Perform delete request for the created item
    mockMvc.perform(delete("/api/admin/item/{id}", testItem.getId()).with(csrf()))
        .andExpect(status().isNoContent());

    // Verify that the item was deleted from the database
    assertFalse(itemRepository.existsById(testItem.getId()));
  }
}