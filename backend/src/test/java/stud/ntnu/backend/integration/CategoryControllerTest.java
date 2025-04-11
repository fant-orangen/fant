package stud.ntnu.backend.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.model.Category;
import stud.ntnu.backend.repository.CategoryRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <h2>Integration Tests for Category Controller</h2>
 * <p>This class contains integration tests for the category-related endpoints of the application.</p>
 * <p>These tests verify the functionality for retrieving a category by its ID and retrieving all categories.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CategoryControllerTest {

  /**
   * <h3>MockMvc</h3>
   * <p>A Spring MVC Test framework client used to perform HTTP requests and assertions.</p>
   */
  @Autowired
  private MockMvc mockMvc;

  /**
   * <h3>CategoryRepository</h3>
   * <p>Spring Data JPA repository for {@link Category} entities.</p>
   */
  @Autowired
  private CategoryRepository categoryRepository;

  /**
   * <h3>Saved Test Category</h3>
   * <p>A category entity saved in the database before each test to ensure data availability.</p>
   */
  private Category savedCategory;

  /**
   * <h3>Setup Method</h3>
   * <p>This method is executed before each test case. It seeds the database with a test category.</p>
   */
  @BeforeEach
  void setUp() {
    // Seed a test category
    savedCategory = categoryRepository.save(
        Category.builder().name("Electronics").imageUrl("https://example.com/electronics.jpg")
            .build());
  }

  /**
   * <h3>Test Get Category By ID Endpoint</h3>
   * <p>Tests the {@code GET /api/category/{id}} endpoint to ensure a category can be retrieved by its unique identifier.</p>
   * <p>The test verifies that the HTTP status code is 200 (OK) and that the returned JSON response contains the correct ID, name, and image URL of the requested category.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  void testGetCategoryById() throws Exception {
    // Perform the get category by ID request
    mockMvc.perform(
            get("/api/category/{id}", savedCategory.getId()).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(savedCategory.getId().intValue())))
        .andExpect(jsonPath("$.name", is("Electronics")))
        .andExpect(jsonPath("$.imageUrl", is("https://example.com/electronics.jpg")));
  }

  /**
   * <h3>Test Get All Categories Endpoint</h3>
   * <p>Tests the {@code GET /api/category/all} endpoint to ensure all categories are retrieved.</p>
   * <p>The test first adds a few more categories to the database and then verifies that the HTTP status code is 200 (OK) and that the returned JSON array contains the expected number of categories.</p>
   * <p>The expected size includes the category created in the {@code setUp()} method and any additional categories seeded within this test.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  void testGetAllCategories() throws Exception {
    // Add additional categories to the database
    categoryRepository.save(Category.builder().name("Books").imageUrl("img2").build());
    categoryRepository.save(Category.builder().name("Clothing").imageUrl("img3").build());

    // Perform the get all categories request
    mockMvc.perform(get("/api/category/all").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(15))); // 1 from @BeforeEach + 2 more + 12 from test data
  }
}