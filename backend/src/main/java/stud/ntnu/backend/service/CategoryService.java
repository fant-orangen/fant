package stud.ntnu.backend.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.data.category.CategoryRequestDto;
import stud.ntnu.backend.model.Category;
import stud.ntnu.backend.repository.CategoryRepository;

/**
 * <h2>CategoryService</h2>
 * <p>Service for managing categories within the marketplace.</p>
 * <p>This service provides functionalities for creating, updating, deleting, and retrieving
 * categories. It acts as an intermediary between the controller and the data access layer,
 * applying business logic and handling exceptions.</p>
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

  /**
   * <h3>Category Repository</h3>
   * <p>Data access component for {@link Category} entities.</p>
   * <p>Provides methods for interacting with the database to perform operations on categories.</p>
   */
  private final CategoryRepository categoryRepository;

  /**
   * <h3>Model Mapper</h3>
   * <p>Utility for object-to-object mapping, used here for converting between DTOs and entities.</p>
   */
  private final ModelMapper modelMapper;

  /**
   * <h3>Create Category</h3>
   * <p>Creates a new {@link Category} based on the information provided in the
   * {@link CategoryRequestDto}.</p>
   *
   * @param categoryRequestDto the data transfer object containing the details of the new category.
   * @return the newly created {@link Category} entity, as saved in the database.
   */
  @Transactional
  public Category create(CategoryRequestDto categoryRequestDto) {
    return categoryRepository.save(modelMapper.map(categoryRequestDto, Category.class));
  }

  /**
   * <h3>Update Category</h3>
   * <p>Updates an existing {@link Category} identified by its ID with the new information provided
   * in the {@link CategoryRequestDto}.</p>
   *
   * @param categoryRequestDto the data transfer object containing the updated details for the
   *                           category.
   * @param id                 the unique identifier of the {@link Category} to be updated.
   * @return the updated {@link Category} entity, as saved in the database after applying the
   * changes.
   * @throws EntityNotFoundException if a category with the specified ID does not exist.
   */
  @Transactional
  public Category update(CategoryRequestDto categoryRequestDto, Long id) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));
    modelMapper.map(categoryRequestDto, category);
    return categoryRepository.save(category);
  }

  /**
   * <h3>Delete Category</h3>
   * <p>Deletes a {@link Category} from the database based on its unique identifier.</p>
   *
   * @param id the unique identifier of the {@link Category} to be deleted.
   */
  @Transactional
  public void delete(Long id) {
    categoryRepository.deleteById(id);
  }

  /**
   * <h3>Get All Categories</h3>
   * <p>Retrieves a list of all {@link Category} entities currently stored in the database.</p>
   *
   * @return a {@link List} containing all {@link Category} entities. Returns an empty list if no
   * categories exist.
   */
  public List<Category> getAll() {
    return categoryRepository.findAll();
  }

  /**
   * <h3>Get Category By ID</h3>
   * <p>Retrieves a specific {@link Category} entity based on its unique identifier.</p>
   *
   * @param id the unique identifier of the {@link Category} to retrieve.
   * @return the found {@link Category} entity.
   * @throws EntityNotFoundException if no category with the specified ID exists in the database.
   */
  public Category getCategoryById(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
  }

  /**
   * <h3>Get Category By Name</h3>
   * <p>Retrieves a specific {@link Category} entity based on its exact name.</p>
   *
   * @param name the name of the category to retrieve. Category names are case-sensitive based on
   *             the database collation.
   * @return the found {@link Category} entity.
   * @throws EntityNotFoundException if no category with the specified name exists in the database.
   */
  public Category getCategoryByName(String name) {
    return categoryRepository.findByName(name)
        .orElseThrow(() -> new EntityNotFoundException("Category not found with name: " + name));
  }
}