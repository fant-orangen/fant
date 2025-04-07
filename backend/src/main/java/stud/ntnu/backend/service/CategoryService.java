package stud.ntnu.backend.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.data.category.CategoryRequestDto;
import stud.ntnu.backend.model.Category;
import stud.ntnu.backend.repository.CategoryRepository;

/**
 * <h2>CategoryService</h2>
 * <p>Service for category management operations.</p>
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

  /**
   * <h3>Category Repository</h3>
   * <p>Data access component for categories.</p>
   */
  private final CategoryRepository categoryRepository;

  /**
   * <h3>Model Mapper</h3>
   * <p>Object mapper for DTO conversions.</p>
   */
  private final ModelMapper modelMapper;

  /**
   * <h3>Create Category</h3>
   * <p>Creates a new category from the provided DTO.</p>
   *
   * @param categoryRequestDto the category data
   * @return the created {@link Category}
   */
  @Transactional
  public Category create(CategoryRequestDto categoryRequestDto) {
    return categoryRepository.save(fromDto(categoryRequestDto));
  }

  /**
   * <h3>Update Category</h3>
   * <p>Updates an existing category with new data.</p>
   *
   * @param categoryRequestDto the updated category data
   * @param id                 the ID of the category to update
   * @return the updated {@link Category}
   */
  @Transactional
  public Category update(CategoryRequestDto categoryRequestDto, Long id) {
    Category category = fromDto(categoryRequestDto);
    category.setId(id);
    return categoryRepository.save(category);
  }

  /**
   * <h3>Delete Category</h3>
   * <p>Deletes a category by its ID.</p>
   *
   * @param id the ID of the category to delete
   */
  @Transactional
  public void delete(Long id) {
    categoryRepository.deleteById(id);
  }

  /**
   * <h3>Get All Categories</h3>
   * <p>Retrieves all categories in the system.</p>
   *
   * @return list of all {@link Category} entities
   */
  public Page<Category> getAll(Pageable pageable) {
    return categoryRepository.findAll(pageable);
  }

  /**
   * <h3>Get Category By ID</h3>
   * <p>Retrieves a category by its unique identifier.</p>
   *
   * @param id the category ID
   * @return the found {@link Category}
   * @throws EntityNotFoundException if category not found
   */
  public Category getCategoryById(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
  }

  /**
   * <h3>Get Category By Name</h3>
   * <p>Retrieves a category by its exact name.</p>
   *
   * @param name the category name
   * @return the found {@link Category}
   * @throws EntityNotFoundException if category not found
   */
  public Category getCategoryByName(String name) {
    return categoryRepository.findByName(name)
        .orElseThrow(() -> new EntityNotFoundException("Category not found with name: " + name));
  }

  /**
   * <h3>Convert DTO to Entity</h3>
   * <p>Maps a CategoryRequestDto to a Category entity.</p>
   *
   * @param categoryRequestDto the DTO to convert
   * @return the mapped {@link Category} entity
   */
  private Category fromDto(CategoryRequestDto categoryRequestDto) {
    return modelMapper.map(categoryRequestDto, Category.class);
  }
}