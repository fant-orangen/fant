package stud.ntnu.backend.unit;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import stud.ntnu.backend.data.category.CategoryRequestDto;
import stud.ntnu.backend.model.Category;
import stud.ntnu.backend.repository.CategoryRepository;

import java.util.Optional;
import stud.ntnu.backend.service.CategoryService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * <h2>Unit Tests for Category Service</h2>
 * <p>This class contains unit tests for the {@link CategoryService} class, focusing on the business logic for creating, retrieving, updating, and deleting categories.</p>
 * <p>The tests utilize Mockito to mock the dependencies of the {@code CategoryService}, such as {@link CategoryRepository} and {@link ModelMapper}, allowing for isolated testing of the service's methods.</p>
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  /**
   * <h3>Mock CategoryRepository</h3>
   * <p>A Mockito mock for the {@link CategoryRepository} dependency, used to simulate database interactions related to categories.</p>
   */
  @Mock
  private CategoryRepository categoryRepository;

  /**
   * <h3>Mock ModelMapper</h3>
   * <p>A Mockito mock for the {@link ModelMapper} dependency, used for mapping between DTOs and entities.</p>
   */
  @Mock
  private ModelMapper modelMapper;

  /**
   * <h3>InjectMocks CategoryService</h3>
   * <p>The instance of {@link CategoryService} being tested, with its dependencies injected via Mockito.</p>
   */
  @InjectMocks
  private CategoryService categoryService;

  /**
   * <h3>Test Category Entity</h3>
   * <p>A {@link Category} entity used for testing service methods.</p>
   */
  private Category category;

  /**
   * <h3>Test CategoryRequestDto</h3>
   * <p>A {@link CategoryRequestDto} used as input for creating and updating categories in the tests.</p>
   */
  private CategoryRequestDto categoryRequestDto;

  /**
   * <h3>Setup Method</h3>
   * <p>This method is executed before each test case. It initializes a test {@link Category} entity and a {@link CategoryRequestDto}.</p>
   */
  @BeforeEach
  void setUp() {
    // Set up the test category and DTO
    category = new Category();
    category.setId(1L);
    category.setName("Test Category");

    categoryRequestDto = new CategoryRequestDto();
    categoryRequestDto.setName("Test Category");
  }

  /**
   * <h3>Test Create Category</h3>
   * <p>Tests the successful creation of a new category. It verifies that the {@link CategoryRequestDto} is mapped to a {@link Category} entity, saved to the repository, and the saved entity is returned.</p>
   */
  @Test
  void createCategory() {
    // Arrange
    when(modelMapper.map(categoryRequestDto, Category.class)).thenReturn(category);
    when(categoryRepository.save(any(Category.class))).thenReturn(category);

    // Act
    Category createdCategory = categoryService.create(categoryRequestDto);

    // Assert
    assertNotNull(createdCategory);
    assertEquals("Test Category", createdCategory.getName());
    verify(categoryRepository, times(1)).save(any(Category.class));
  }

  /**
   * <h3>Test Update Category</h3>
   * <p>Tests the successful update of an existing category. It verifies that the category is retrieved by ID, its name is updated based on the {@link CategoryRequestDto}, and the updated entity is saved and returned.</p>
   */
  @Test
  void updateCategory() {
    // Arrange
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(categoryRepository.save(any(Category.class))).thenReturn(category);

    // Act
    Category updatedCategory = categoryService.update(categoryRequestDto, 1L);

    // Assert
    assertNotNull(updatedCategory);
    assertEquals("Test Category", updatedCategory.getName());
    verify(categoryRepository, times(1)).findById(1L);
    verify(categoryRepository, times(1)).save(any(Category.class));
  }

  /**
   * <h3>Test Update Category - Not Found</h3>
   * <p>Tests the scenario where an attempt is made to update a category with an ID that does not exist in the repository. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void updateCategoryNotFound() {
    // Arrange
    when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class,
        () -> categoryService.update(categoryRequestDto, 1L));
    verify(categoryRepository, times(1)).findById(1L);
    verify(categoryRepository, never()).save(any(Category.class));
  }

  /**
   * <h3>Test Delete Category</h3>
   * <p>Tests the successful deletion of a category by its ID. It verifies that the {@link CategoryRepository}'s {@code deleteById} method is called with the correct ID.</p>
   */
  @Test
  void deleteCategory() {
    // Arrange
    Long categoryIdToDelete = 1L; // Use a variable for clarity

    // Mock the existsById check to return true, otherwise the service throws EntityNotFoundException
    when(categoryRepository.existsById(categoryIdToDelete)).thenReturn(true);

    // Mock the actual deletion call (this doesn't throw an exception on its own)
    doNothing().when(categoryRepository).deleteById(categoryIdToDelete);

    // Act
    categoryService.delete(categoryIdToDelete);

    // Assert
    // Verify existsById was called
    verify(categoryRepository, times(1)).existsById(categoryIdToDelete);
    // Verify deleteById was called
    verify(categoryRepository, times(1)).deleteById(categoryIdToDelete);
  }

  /**
   * <h3>Test Get All Categories</h3>
   * <p>Tests the retrieval of all categories from the repository. It verifies that the {@link CategoryRepository}'s {@code findAll} method is called and returns a list of categories.</p>
   */
  @Test
  void getAllCategories() {
    // Arrange
    when(categoryRepository.findAll()).thenReturn(List.of(category));

    // Act
    var categories = categoryService.getAll();

    // Assert
    assertNotNull(categories);
    assertEquals(1, categories.size());
    verify(categoryRepository, times(1)).findAll();
  }

  /**
   * <h3>Test Get Category By ID</h3>
   * <p>Tests the successful retrieval of a category by its ID. It verifies that the {@link CategoryRepository}'s {@code findById} method is called with the correct ID and returns an {@link Optional} containing the category.</p>
   */
  @Test
  void getCategoryById() {
    // Arrange
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

    // Act
    Category foundCategory = categoryService.getCategoryById(1L);

    // Assert
    assertNotNull(foundCategory);
    assertEquals(1L, foundCategory.getId());
    verify(categoryRepository, times(1)).findById(1L);
  }

  /**
   * <h3>Test Get Category By ID - Not Found</h3>
   * <p>Tests the scenario where an attempt is made to retrieve a category with an ID that does not exist. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void getCategoryByIdNotFound() {
    // Arrange
    when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> categoryService.getCategoryById(1L));
    verify(categoryRepository, times(1)).findById(1L);
  }

  /**
   * <h3>Test Get Category By Name</h3>
   * <p>Tests the successful retrieval of a category by its name. It verifies that the {@link CategoryRepository}'s {@code findByName} method is called with the correct name and returns an {@link Optional} containing the category.</p>
   */
  @Test
  void getCategoryByName() {
    // Arrange
    when(categoryRepository.findByName("Test Category")).thenReturn(Optional.of(category));

    // Act
    Category foundCategory = categoryService.getCategoryByName("Test Category");

    // Assert
    assertNotNull(foundCategory);
    assertEquals("Test Category", foundCategory.getName());
    verify(categoryRepository, times(1)).findByName("Test Category");
  }

  /**
   * <h3>Test Get Category By Name - Not Found</h3>
   * <p>Tests the scenario where an attempt is made to retrieve a category by a name that does not exist. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void getCategoryByNameNotFound() {
    // Arrange
    when(categoryRepository.findByName("Test Category")).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class,
        () -> categoryService.getCategoryByName("Test Category"));
    verify(categoryRepository, times(1)).findByName("Test Category");
  }
}