package stud.ntnu.backend.data.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stud.ntnu.backend.model.Category;

import java.util.Map;

/**
 * <h2>CategoryRecommendationDto</h2>
 * <p>DTO representing a probability distribution over categories for recommendations.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRecommendationDto {

  /**
   * <h3>Distribution of category probabilities</h3>
   * <p>Maps {@link Category}-IDs to their recommendation probabilities.</p>
   * <p>Values are between 0 and 1, with the sum typically equal to 1.</p>
   */
  private Map<String, Double> distribution;
}