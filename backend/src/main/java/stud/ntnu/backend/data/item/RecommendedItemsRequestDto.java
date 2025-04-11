package stud.ntnu.backend.data.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * <h2>RecommendedItemsRequestDto</h2>
 * <p>Data transfer object for requesting recommended items based on category distribution.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendedItemsRequestDto {

  /**
   * <h3>Category Distribution</h3>
   * <p>Map of category IDs to their probability weights (must not be blank).</p>
   */
  @NotNull
  private Map<String, Double> distribution;

  /**
   * <h3>Limit</h3>
   * <p>Maximum number of recommended items to return (must not be blank).</p>
   */
  @NotNull
  @Min(1)
  private Integer limit;
}