package stud.ntnu.backend.controller;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.CategoryRecommendationDto;
import stud.ntnu.backend.service.RecommendationService;

/**
 * <h2>RecommendationController</h2>
 * <p>Controller for handling personalized recommendation requests.</p>
 */
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

  private final RecommendationService recommendationService;

  /**
   * <h3>Get personalized category recommendations for current user</h3>
   * <p>Returns a probability distribution over categories based on user's view history.</p>
   *
   * @return A probability distribution of categories
   */
  @GetMapping("/categories")
  public ResponseEntity<CategoryRecommendationDto> getCategoryRecommendations() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    CategoryRecommendationDto recommendations =
        recommendationService.generateCategoryRecommendations(email);

    return ResponseEntity.ok(recommendations);
  }

  /**
   * <h3>Get the total number of item views for the authenticated user</h3>
   * <p>Returns the count of all items viewed by the user.</p>
   *
   * @return A map containing the totalViews count
   */
  @GetMapping("/views/count")
  public ResponseEntity<Map<String, Integer>> getUserViewCount() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    int totalViews = recommendationService.getUserViewCount(email);

    Map<String, Integer> response = new HashMap<>();
    response.put("totalViews", totalViews);

    return ResponseEntity.ok(response);
  }
}