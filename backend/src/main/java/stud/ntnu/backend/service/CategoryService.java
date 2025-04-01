package stud.ntnu.backend.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
  public final CategoryRepository categoryRepository;

  public Category findById(long id) {
    return categoryRepository.findById(id).orElse(null);
  }

  public Category save(Category category) {
    return categoryRepository.save(category);
  }

  public Category delete(long id) {
    Optional<Category> category = categoryRepository.findById(id);
    if (category.isPresent()) {
      categoryRepository.delete(category.get());
      return category.get();
    }
    return null;
  }

  public List<Category> findAll() {
    return categoryRepository.findAll();
  }

  public Optional<Category> findByName(String name) {
    return categoryRepository.findByName(name);
  }
}
