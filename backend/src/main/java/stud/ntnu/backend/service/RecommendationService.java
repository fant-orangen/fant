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

  private final ItemViewRepository itemViewRepository;
  private final UserRepository userRepository;

  /**
   * <h3>Generate category recommendations based on user's view history</h3>
   * <p>Creates a probability distribution over categories based on what the user has viewed.</p>
   *
   * @param email The email of the user to generate recommendations for
   * @return A DTO containing category probability distribution
   */
  public CategoryRecommendationDto generateCategoryRecommendations(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

    // Get all items viewed by this user
    List<ItemView> userViews = itemViewRepository.findByUserId(user.getId());

    // Count category occurrences
    Map<String, Integer> categoryCounts = new HashMap<>();
    int totalViews = 0;

    for (ItemView view : userViews) {
      Long categoryId = view.getItem().getCategory().getId();
      String categoryKey = categoryId.toString();

      categoryCounts.put(categoryKey,
          categoryCounts.getOrDefault(categoryKey, 0) + 1);
      totalViews++;
    }

    // Convert counts to probabilities
    Map<String, Double> distribution = new HashMap<>();

    if (totalViews > 0) {
      for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
        distribution.put(entry.getKey(),
            (double) entry.getValue() / totalViews);
      }
    }

    return new CategoryRecommendationDto(distribution);
  }

  /**
   * <h3>Get the total number of item views for a user</h3>
   * <p>Counts the total number of items viewed by the specified user.</p>
   *
   * @param email Email of the user
   * @return Total number of item views
   */
  public int getUserViewCount(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

    List<ItemView> userViews = itemViewRepository.findByUserId(user.getId());
    return userViews.size();
  }
}