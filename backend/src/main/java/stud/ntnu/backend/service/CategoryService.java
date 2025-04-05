package stud.ntnu.backend.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.data.CategoryRequestDto;
import stud.ntnu.backend.model.Category;
import stud.ntnu.backend.repository.CategoryRepository;

/**
 * <h2> CategoryService for category-related business logic. </h2>
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

  /**
   * <h3> The CategoryRepository. </h3>
   */
  private final CategoryRepository categoryRepository;

  private final ModelMapper modelMapper;


  public Category getCategoryById(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
  }

  public Category create(CategoryRequestDto categoryRequestDto) {
    return categoryRepository.save(modelMapper.map(categoryRequestDto, Category.class));
  }

  public Category update(CategoryRequestDto categoryRequestDto, Long id) {
    Category category  = modelMapper.map( categoryRequestDto, Category.class);
    category.setId(id);
    return categoryRepository.save(category);
  }

  public void delete(long id) {
    categoryRepository.deleteById(id);
  }

  public List<Category> findAll() {
    return categoryRepository.findAll();
  }

  public Optional<Category> findByName(String name) {
    return categoryRepository.findByName(name);
  }
}
