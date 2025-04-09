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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CategoryControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private CategoryRepository categoryRepository;

  private Category savedCategory;

  @BeforeEach
  void setUp() {
    // Seed a test category
    savedCategory = categoryRepository.save(Category.builder()
        .name("Electronics")
        .imageUrl("https://example.com/electronics.jpg")
        .build());
  }

  @Test
  void testGetCategoryById() throws Exception {
    mockMvc.perform(get("/api/category/{id}", savedCategory.getId())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(savedCategory.getId().intValue())))
        .andExpect(jsonPath("$.name", is("Electronics")))
        .andExpect(jsonPath("$.imageUrl", is("https://example.com/electronics.jpg")));
  }

  @Test
  void testGetAllCategories() throws Exception {
    categoryRepository.save(Category.builder().name("Books").imageUrl("img2").build());
    categoryRepository.save(Category.builder().name("Clothing").imageUrl("img3").build());

    mockMvc.perform(get("/api/category/all")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(15))); // 1 from @BeforeEach + 2 more + 12 from test data
  }
}
