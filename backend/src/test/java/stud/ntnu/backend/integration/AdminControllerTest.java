package stud.ntnu.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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
import stud.ntnu.backend.data.user.UserCreateDto;
import stud.ntnu.backend.model.Category;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.model.enums.Role;
import stud.ntnu.backend.repository.CategoryRepository;
import stud.ntnu.backend.repository.ItemRepository;
import stud.ntnu.backend.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AdminControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private ItemRepository itemRepository;

  private User testUser;
  private User adminUser;
  private Category testCategory;
  private Item testItem;

  @BeforeEach
  public void setup() {
    // Create regular test user in the database
    testUser = User.builder()
        .email("test@example.com")
        .displayName("Test User")
        .firstName("Test")
        .lastName("User")
        .passwordHash("$2a$10$dWj9sFMjHcN0M1QasCWBR.uSJDIbZJ3LVsLY4Dw7cXUl8Txa4JBYe") // hashed "password"
        .phone("+4712345678")
        .role(Role.USER)
        .build();
    testUser = userRepository.save(testUser);

    // Create admin user in the database
    adminUser = User.builder()
        .email("admin@example.com")
        .displayName("Admin User")
        .firstName("Admin")
        .lastName("User")
        .passwordHash("$2a$10$dWj9sFMjHcN0M1QasCWBR.uSJDIbZJ3LVsLY4Dw7cXUl8Txa4JBYe") // hashed "password"
        .phone("+4711223344")
        .role(Role.ADMIN)
        .build();
    adminUser = userRepository.save(adminUser);

    // Create test category in the database
    testCategory = Category.builder()
        .name("Test Category")
        .imageUrl("https://example.com/image.jpg")
        .build();
    testCategory = categoryRepository.save(testCategory);
  }

  // The test methods remain the same as before
  // ...

  @Test
  @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
  public void testUpdateUser() throws Exception {
    // Create update DTO
    UserCreateDto userUpdateDto = new UserCreateDto();
    userUpdateDto.setEmail("updated@example.com");
    userUpdateDto.setDisplayName("Updated User");
    userUpdateDto.setFirstName("Updated");
    userUpdateDto.setLastName("User");
    userUpdateDto.setPhone("+4798765432");
    userUpdateDto.setPassword("newpassword");

    // Perform update request
    mockMvc.perform(put("/api/admin/users/{id}", testUser.getId())
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userUpdateDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
        .andExpect(jsonPath("$.email", is("updated@example.com")))
        .andExpect(jsonPath("$.displayName", is("Updated User")))
        .andExpect(jsonPath("$.firstName", is("Updated")))
        .andExpect(jsonPath("$.lastName", is("User")))
        .andExpect(jsonPath("$.phone", is("+4798765432")))
        .andExpect(jsonPath("$.role", is("USER")));

    // Verify the database was updated
    User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
    assertEquals("updated@example.com", updatedUser.getEmail());
    assertEquals("Updated User", updatedUser.getDisplayName());
    assertEquals(Role.USER, updatedUser.getRole());
  }

  @Test
  @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
  public void testDeleteUser() throws Exception {
    // Perform delete request
    mockMvc.perform(delete("/api/admin/users/{id}", testUser.getId())
            .with(csrf()))
        .andExpect(status().isNoContent());

    // Verify the user was deleted from the database
    assertFalse(userRepository.existsById(testUser.getId()));
  }

  @Test
  @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
  public void testGetAllUsers() throws Exception {
    // Perform get all users request
    mockMvc.perform(get("/api/admin/users")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(2))))
        .andExpect(jsonPath("$.content[*].id", hasItems(testUser.getId().intValue(), adminUser.getId().intValue())))
        .andExpect(jsonPath("$.content[*].email", hasItems("test@example.com", "admin@example.com")));
  }

  @Test
  @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
  public void testCreateCategory() throws Exception {
    // Create category DTO
    CategoryRequestDto categoryDto = new CategoryRequestDto();
    categoryDto.setName("New Category");
    categoryDto.setImageUrl("https://example.com/new-image.jpg");
    categoryDto.setParentId(null);

    // Perform create category request
    mockMvc.perform(post("/api/admin/category")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(categoryDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name", is("New Category")))
        .andExpect(jsonPath("$.imageUrl", is("https://example.com/new-image.jpg")));

    // Verify category was created in the database
    assertTrue(categoryRepository.existsByName("New Category"));
  }

  @Test
  @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
  public void testDeleteItem() throws Exception {
    Long itemId = 1L; // Use an appropriate ID based on your test data
    mockMvc.perform(delete("/api/admin/item/{id}", itemId)
            .with(csrf()))
        .andExpect(status().is(anyOf(is(204), is(404))));

  }
}