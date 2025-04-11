package stud.ntnu.backend.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional; // Use jakarta.transaction.Transactional
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
// Remove org.springframework.transaction.annotation.Transactional if jakarta is used consistently
// import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.data.category.CategoryRequestDto; //
import stud.ntnu.backend.model.Category; //
import stud.ntnu.backend.repository.CategoryRepository; //

/**
 * Service for managing categories.
 */
@Service
@RequiredArgsConstructor
public class CategoryService { //

  private final CategoryRepository categoryRepository; //
  private final ModelMapper modelMapper; //

  @Transactional
  public Category create(CategoryRequestDto categoryRequestDto) { //
    Category category = modelMapper.map(categoryRequestDto, Category.class); //

    if (categoryRequestDto.getParentId() != null && categoryRequestDto.getParentId() > 0) { //
      Category parent = categoryRepository.findById(categoryRequestDto.getParentId()) //
          .orElseThrow(() -> new EntityNotFoundException("Parent category with id " + categoryRequestDto.getParentId() + " not found")); //
      category.setParent(parent); //
    } else {
      category.setParent(null); //
    }
    category.setId(null); //

    return categoryRepository.save(category); //
  }


  /**
   * Updates an existing Category using a direct repository query.
   *
   * @param categoryRequestDto DTO containing updated data.
   * @param id                 ID of the category to update.
   * @return The updated Category entity after fetching it again.
   * @throws EntityNotFoundException if the category or specified parent category doesn't exist.
   */
  @Transactional // Service layer transaction
  public Category update(CategoryRequestDto categoryRequestDto, Long id) { //
    // 1. Check if the category exists to provide a clear error if not
    if (!categoryRepository.existsById(id)) { //
      throw new EntityNotFoundException("Category with id " + id + " not found"); //
    }

    // 2. Fetch parent category entity if parentId is provided
    Category parentCategory = null;
    Long parentId = categoryRequestDto.getParentId(); //
    if (parentId != null && parentId > 0) { //
      parentCategory = categoryRepository.findById(parentId) //
          .orElseThrow(() -> new EntityNotFoundException("Parent category with id " + parentId + " not found")); //
      // Optional: Check for self-parenting
      if (parentCategory.getId().equals(id)) { //
        throw new IllegalArgumentException("A category cannot be its own parent."); //
      }
    }

    // 3. Call the custom repository update method
    int updatedRows = categoryRepository.updateCategoryDetails( //
        id,
        categoryRequestDto.getName(), //
        categoryRequestDto.getImageUrl(), //
        parentCategory // Pass the fetched Category object or null
    );

    if (updatedRows == 0) { //
      // This could happen in a race condition, but good to check
      throw new RuntimeException("Failed to update category with id " + id + ". Record might have been deleted."); //
    }

    // 4. Fetch and return the updated category to confirm changes
    //    (This ensures the returned object reflects the updated state)
    return categoryRepository.findById(id) //
        .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " could not be found after update.")); // Should not happen if update succeeded
  }


  @Transactional
  public void delete(Long id) { //
    // Check if category exists before attempting deletion
    if (!categoryRepository.existsById(id)) { //
      throw new EntityNotFoundException("Category with id " + id + " not found, cannot delete."); //
    }
    categoryRepository.deleteById(id); //
  }

  public List<Category> getAll() { //
    return categoryRepository.findAll(); //
  }

  public Category getCategoryById(Long id) { //
    return categoryRepository.findById(id) //
        .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id)); //
  }

  public Category getCategoryByName(String name) { //
    return categoryRepository.findByName(name) //
        .orElseThrow(() -> new EntityNotFoundException("Category not found with name: " + name)); //
  }
}