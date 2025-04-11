package stud.ntnu.backend.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional; // Ensure Optional is imported

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper; // Keep ModelMapper for create
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
   */
  private final CategoryRepository categoryRepository;

  /**
   * <h3>Model Mapper</h3>
   * <p>Utility for object-to-object mapping, used here for converting DTOs to entities during creation.</p>
   */
  private final ModelMapper modelMapper; // Still needed for create

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
    // Map DTO to a new Category entity for creation
    Category category = modelMapper.map(categoryRequestDto, Category.class);

    // Handle parent category if parentId is provided
    if (categoryRequestDto.getParentId() != null && categoryRequestDto.getParentId() > 0) {
      Category parent = categoryRepository.findById(categoryRequestDto.getParentId())
          .orElseThrow(() -> new EntityNotFoundException("Parent category with id " + categoryRequestDto.getParentId() + " not found"));
      category.setParent(parent);
    } else {
      category.setParent(null); // Explicitly set parent to null if ID is null or 0
    }

    // Ensure ID is null before saving a new entity (should be handled by modelMapper mapping to a new object)
    category.setId(null);

    return categoryRepository.save(category);
  }


  /**
   * <h3>Update Category</h3>
   * <p>Updates an existing {@link Category} identified by its ID with the new information provided
   * in the {@link CategoryRequestDto}.</p>
   * <p>Maps fields manually to avoid potential ModelMapper issues with IDs during updates.</p>
   *
   * @param categoryRequestDto the data transfer object containing the updated details for the
   * category.
   * @param id                 the unique identifier of the {@link Category} to be updated.
   * @return the updated {@link Category} entity, as saved in the database after applying the
   * changes.
   * @throws EntityNotFoundException if a category with the specified ID does not exist, or if a specified parent ID does not correspond to an existing category.
   */
  @Transactional
  public Category update(CategoryRequestDto categoryRequestDto, Long id) {
    // 1. Fetch the existing category by ID
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));

    // 2. Manually update fields from the DTO onto the fetched entity
    category.setName(categoryRequestDto.getName());
    category.setImageUrl(categoryRequestDto.getImageUrl()); // Update image URL

    // 3. Handle parent category update
    Long parentId = categoryRequestDto.getParentId();
    Category parentCategory = null;
    if (parentId != null && parentId > 0) {
      // If a valid parentId is provided, fetch the parent category
      parentCategory = categoryRepository.findById(parentId)
          .orElseThrow(() -> new EntityNotFoundException("Parent category with id " + parentId + " not found"));
      // Prevent setting a category as its own parent (optional check)
      if (parentCategory.getId().equals(category.getId())) {
        throw new IllegalArgumentException("A category cannot be its own parent.");
      }
    }
    // Set the parent (will be null if parentId was null, 0, or invalid)
    category.setParent(parentCategory);


    // 4. Save the modified, existing entity (ID should remain unchanged)
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
    // Check if category exists before attempting deletion (optional, deleteById handles not found)
    if (!categoryRepository.existsById(id)) {
      throw new EntityNotFoundException("Category with id " + id + " not found, cannot delete.");
    }
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
   * the database collation.
   * @return the found {@link Category} entity.
   * @throws EntityNotFoundException if no category with the specified name exists in the database.
   */
  public Category getCategoryByName(String name) {
    return categoryRepository.findByName(name)
        .orElseThrow(() -> new EntityNotFoundException("Category not found with name: " + name));
  }
}