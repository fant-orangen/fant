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

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private CategoryService categoryService;

  private Category category;
  private CategoryRequestDto categoryRequestDto;

  @BeforeEach
  void setUp() {
    // Set up the test category and DTO
    category = new Category();
    category.setId(1L);
    category.setName("Test Category");

    categoryRequestDto = new CategoryRequestDto();
    categoryRequestDto.setName("Test Category");
  }

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

  @Test
  void updateCategoryNotFound() {
    // Arrange
    when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> categoryService.update(categoryRequestDto, 1L));
    verify(categoryRepository, times(1)).findById(1L);
  }

  @Test
  void deleteCategory() {
    // Arrange
    doNothing().when(categoryRepository).deleteById(1L);

    // Act
    categoryService.delete(1L);

    // Assert
    verify(categoryRepository, times(1)).deleteById(1L);
  }

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

  @Test
  void getCategoryByIdNotFound() {
    // Arrange
    when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> categoryService.getCategoryById(1L));
    verify(categoryRepository, times(1)).findById(1L);
  }

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

  @Test
  void getCategoryByNameNotFound() {
    // Arrange
    when(categoryRepository.findByName("Test Category")).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> categoryService.getCategoryByName("Test Category"));
    verify(categoryRepository, times(1)).findByName("Test Category");
  }
}
