package stud.ntnu.backend.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendedItemsRequestDto {

  private Map<String, Double> distribution;
  private Integer limit;
}