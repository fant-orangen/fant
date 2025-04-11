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
  private Category parentCategory;


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
    category.setImageUrl("old_url");
    category.setParent(null);

    parentCategory = new Category();
    parentCategory.setId(2L);
    parentCategory.setName("Parent Category");

    categoryRequestDto = new CategoryRequestDto();
    categoryRequestDto.setName("Updated Category");
    categoryRequestDto.setImageUrl("new_url");
    categoryRequestDto.setParentId(2L); // Default to having a parent for update tests
  }

  /**
   * <h3>Test Create Category</h3>
   * <p>Tests the successful creation of a new category. It verifies that the {@link CategoryRequestDto} is mapped to a {@link Category} entity, saved to the repository, and the saved entity is returned.</p>
   */
  @Test
  void createCategory() {
    // Arrange
    // Reset parentId for create test without parent
    categoryRequestDto.setParentId(null);
    category.setName(categoryRequestDto.getName()); // Align names for mapping simulation
    category.setImageUrl(categoryRequestDto.getImageUrl());

    when(modelMapper.map(categoryRequestDto, Category.class)).thenReturn(category);
    when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
      // Simulate ID generation on save
      Category catToSave = invocation.getArgument(0);
      catToSave.setId(1L); // Assign an ID as the real save would
      return catToSave;
    });

    // Act
    Category createdCategory = categoryService.create(categoryRequestDto);

    // Assert
    assertNotNull(createdCategory);
    assertEquals("Updated Category", createdCategory.getName()); // Check name from DTO
    assertNull(createdCategory.getParent()); // Ensure parent is null
    assertNotNull(createdCategory.getId()); // Ensure ID is assigned
    verify(modelMapper).map(categoryRequestDto, Category.class);
    verify(categoryRepository).save(any(Category.class)); // Changed from times(1) for clarity
  }


  /**
   * <h3>Test Create Category With Parent</h3>
   * <p>Tests the successful creation of a new category with a parent. It verifies that the parent category is fetched and set correctly.</p>
   */
  @Test
  void createCategoryWithParent() {
    // Arrange
    categoryRequestDto.setParentId(2L); // Set parent ID in DTO
    category.setName(categoryRequestDto.getName());
    category.setImageUrl(categoryRequestDto.getImageUrl());

    when(modelMapper.map(categoryRequestDto, Category.class)).thenReturn(category);
    when(categoryRepository.findById(2L)).thenReturn(Optional.of(parentCategory)); // Mock fetching parent
    when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
      Category catToSave = invocation.getArgument(0);
      catToSave.setId(1L);
      return catToSave;
    });

    // Act
    Category createdCategory = categoryService.create(categoryRequestDto);

    // Assert
    assertNotNull(createdCategory);
    assertNotNull(createdCategory.getParent());
    assertEquals(2L, createdCategory.getParent().getId());
    assertEquals("Updated Category", createdCategory.getName());
    verify(categoryRepository).findById(2L); // Verify parent was fetched
    verify(categoryRepository).save(any(Category.class));
  }


  /**
   * <h3>Test Update Category</h3>
   * <p>Tests the successful update of an existing category. It verifies that the category exists, the parent is fetched, the update query is executed, and the *final* category state is fetched and returned.</p>
   */
  @Test
  void updateCategory() {
    // Arrange
    Long categoryIdToUpdate = 1L;
    Long parentId = 2L;
    categoryRequestDto.setParentId(parentId);
    categoryRequestDto.setName("Successfully Updated");
    categoryRequestDto.setImageUrl("updated_url");

    // Mock the sequence of repository calls within the service's update method
    when(categoryRepository.existsById(categoryIdToUpdate)).thenReturn(true); // 1. Check existence
    when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parentCategory)); // 2. Fetch parent
    when(categoryRepository.updateCategoryDetails( // 3. Simulate successful custom update
        eq(categoryIdToUpdate),
        eq(categoryRequestDto.getName()),
        eq(categoryRequestDto.getImageUrl()),
        eq(parentCategory)
    )).thenReturn(1); // Indicate 1 row affected

    // 4. Mock the *final* findById call to return the *expected updated state*
    Category expectedUpdatedCategory = new Category();
    expectedUpdatedCategory.setId(categoryIdToUpdate);
    expectedUpdatedCategory.setName(categoryRequestDto.getName());
    expectedUpdatedCategory.setImageUrl(categoryRequestDto.getImageUrl());
    expectedUpdatedCategory.setParent(parentCategory);
    when(categoryRepository.findById(categoryIdToUpdate)).thenReturn(Optional.of(expectedUpdatedCategory));

    // Act
    Category updatedCategoryResult = categoryService.update(categoryRequestDto, categoryIdToUpdate);

    // Assert
    assertNotNull(updatedCategoryResult);
    assertEquals(categoryIdToUpdate, updatedCategoryResult.getId());
    assertEquals("Successfully Updated", updatedCategoryResult.getName());
    assertEquals("updated_url", updatedCategoryResult.getImageUrl());
    assertNotNull(updatedCategoryResult.getParent());
    assertEquals(parentId, updatedCategoryResult.getParent().getId());

    // Verify the sequence of mock interactions
    verify(categoryRepository).existsById(categoryIdToUpdate);
    verify(categoryRepository).findById(parentId); // Fetch parent
    verify(categoryRepository).updateCategoryDetails(
        eq(categoryIdToUpdate),
        eq(categoryRequestDto.getName()),
        eq(categoryRequestDto.getImageUrl()),
        eq(parentCategory)
    );
    verify(categoryRepository, times(2)).findById(anyLong()); // Called once for parent, once for final fetch
  }


  /**
   * <h3>Test Update Category - Not Found</h3>
   * <p>Tests the scenario where an attempt is made to update a category with an ID that does not exist in the repository. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   * Corrected to mock existsById instead of findById.
   */
  @Test
  void updateCategoryNotFound() {
    // Arrange
    Long categoryId = 1L;
    // Mock the existsById check, which is the first step in the service method
    when(categoryRepository.existsById(categoryId)).thenReturn(false);

    // Act & Assert
    assertThrows(EntityNotFoundException.class,
        () -> categoryService.update(categoryRequestDto, categoryId));

    // Verify that existsById was called, and other methods were not
    verify(categoryRepository).existsById(categoryId);
    verify(categoryRepository, never()).findById(anyLong()); // Should not be called if existsById is false
    verify(categoryRepository, never()).updateCategoryDetails(anyLong(), anyString(), anyString(), any());
    verify(categoryRepository, never()).save(any(Category.class)); // Save is not used in this update path
  }

  /**
   * <h3>Test Delete Category</h3>
   * <p>Tests the successful deletion of a category by its ID.
   * It verifies that existsById is checked and then deleteById is called.</p> // Updated description
   */
  @Test
  void deleteCategory() {
    // Arrange
    Long categoryIdToDelete = 1L;

    // Tell the mock repository that the category *does* exist
    when(categoryRepository.existsById(categoryIdToDelete)).thenReturn(true);

    // Mock the actual deletion call (doesn't need to do anything specific for void return)
    doNothing().when(categoryRepository).deleteById(categoryIdToDelete);

    // Act
    // Now, when delete is called, the existsById check inside it will pass
    categoryService.delete(categoryIdToDelete);

    // Assert
    // Verify both methods were called in the service
    verify(categoryRepository).existsById(categoryIdToDelete); // Verify the check
    verify(categoryRepository).deleteById(categoryIdToDelete); // Verify the deletion
  }


  /**
   * <h3>Test Delete Category - Not Found</h3>
   * <p>Tests attempting to delete a category that doesn't exist. It verifies that existsById is checked and an exception is thrown.</p>
   */
  @Test
  void deleteCategoryNotFound() {
    // Arrange
    Long categoryIdToDelete = 99L;
    when(categoryRepository.existsById(categoryIdToDelete)).thenReturn(false); // Simulate category not existing

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> categoryService.delete(categoryIdToDelete));

    // Verify existsById was checked, but deleteById was not called
    verify(categoryRepository).existsById(categoryIdToDelete);
    verify(categoryRepository, never()).deleteById(anyLong());
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
    verify(categoryRepository).findAll(); // Verify findAll was called exactly once
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
    verify(categoryRepository).findById(1L); // Verify findById was called exactly once with the correct ID
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
    verify(categoryRepository).findById(1L); // Verify findById was called exactly once
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
    verify(categoryRepository).findByName("Test Category"); // Verify findByName was called exactly once
  }


  /**
   * <h3>Test Get Category By Name - Not Found</h3>
   * <p>Tests the scenario where an attempt is made to retrieve a category by a name that does not exist. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void getCategoryByNameNotFound() {
    // Arrange
    when(categoryRepository.findByName("NonExistent Category")).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class,
        () -> categoryService.getCategoryByName("NonExistent Category"));
    verify(categoryRepository).findByName("NonExistent Category"); // Verify findByName was called exactly once
  }

}