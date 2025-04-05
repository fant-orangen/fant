package stud.ntnu.backend.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.data.CategoryRequestDto;
import stud.ntnu.backend.model.Category;
import stud.ntnu.backend.repository.CategoryRepository;

/**
 * Service class for handling category-related business logic.
 * <p>
 * This service provides methods for CRUD operations and other business logic
 * related to categories. It acts as a middle layer between the controller
 * and the repository layer.
 * </p>
 *
 * @see CategoryRepository
 * @see Category
 * @see CategoryRequestDto
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

  /**
   * Repository for handling database operations for {@link Category} entities.
   */
  private final CategoryRepository categoryRepository;

  /**
   * Mapper for converting between {@link CategoryRequestDto} and {@link Category} entities.
   */
  private final ModelMapper modelMapper;

  /**
   * Creates a new {@link Category}.
   *
   * @param categoryRequestDto the {@link CategoryRequestDto} containing category data to create
   * @return the newly created {@link Category} entity
   */
  public Category create(CategoryRequestDto categoryRequestDto) {
    return categoryRepository.save(modelMapper.map(categoryRequestDto, Category.class));
  }

  /**
   * Updates an existing {@link Category}.
   *
   * @param categoryRequestDto the {@link CategoryRequestDto} containing updated category data
   * @param id                 the ID of the category to update
   * @return the updated {@link Category} entity
   */
  public Category update(CategoryRequestDto categoryRequestDto, Long id) {
    Category category = modelMapper.map(categoryRequestDto, Category.class);
    category.setId(id);
    return categoryRepository.save(category);
  }

  /**
   * Deletes a {@link Category} by its ID.
   *
   * @param id the ID of the category to delete
   */
  public void delete(Long id) {
    categoryRepository.deleteById(id);
  }

  /**
   * Retrieves all {@link Category} entities.
   *
   * @return a list of all {@link Category} entities
   */
  public List<Category> getAll() {
    return categoryRepository.findAll();
  }

  /**
   * Retrieves a {@link Category} by its ID.
   *
   * @param id the ID of the category to retrieve
   * @return the found {@link Category} entity
   * @throws EntityNotFoundException if no category is found with the given ID
   */
  public Category getCategoryById(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
  }

  /**
   * Finds a {@link Category} by its name or throws an exception if not found.
   *
   * @param name the name of the category to find (case-sensitive)
   * @return the found {@link Category} entity
   * @throws EntityNotFoundException if no category exists with the specified name
   */
  public Category getCategoryByName(String name) {
    return categoryRepository.findByName(name)
        .orElseThrow(() -> new EntityNotFoundException("Category not found with name: " + name));
  }
}