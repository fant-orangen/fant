package stud.ntnu.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.data.CategoryRecommendationDto;
import stud.ntnu.backend.model.ItemView;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.ItemViewRepository;
import stud.ntnu.backend.repository.UserRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h2>RecommendationService</h2>
 * <p>Service for generating personalized recommendations based on user behavior.</p>
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

  /**
   * <h3>Item View Repository</h3>
   * <p>Data access component for item view tracking.</p>
   */
  private final ItemViewRepository itemViewRepository;

  /**
   * <h3>User Repository</h3>
   * <p>Data access component for user accounts.</p>
   */
  private final UserRepository userRepository;

  /**
   * <h3>Generate Category Recommendations</h3>
   * <p>Creates a probability distribution of preferred categories based on user's viewing history.</p>
   *
   * @param user the user to analyze
   * @return {@link CategoryRecommendationDto} with category probabilities
   */
  public CategoryRecommendationDto generateCategoryRecommendations(User user) {
    List<ItemView> userViews = itemViewRepository.findByUserId(user.getId());
    Map<String, Integer> categoryCounts = new HashMap<>();
    int totalViews = userViews.size();

    userViews.forEach(view -> {
      String categoryKey = view.getItem().getCategory().getId().toString();
      categoryCounts.put(categoryKey, categoryCounts.getOrDefault(categoryKey, 0) + 1);
    });

    Map<String, Double> distribution = new HashMap<>();
    if (totalViews > 0) {
      categoryCounts.forEach((key, count) ->
          distribution.put(key, (double) count / totalViews));
    }

    return new CategoryRecommendationDto(distribution);
  }

  /**
   * <h3>Get User View Count</h3>
   * <p>Counts total items viewed by a user.</p>
   *
   * @param user the user to check
   * @return total number of viewed items
   */
  public int getUserViewCount(User user) {
    return itemViewRepository.findByUserId(user.getId()).size();
  }
}