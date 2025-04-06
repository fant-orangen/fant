package stud.ntnu.backend.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.category.CategoryRecommendationDto;
import stud.ntnu.backend.service.RecommendationService;
import stud.ntnu.backend.service.UserService;

/**
 * <h2>RecommendationController</h2>
 * <p>Controller for personalized recommendation operations.</p>
 */
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

  /**
   * <h3>Recommendation Service</h3>
   * <p>Service handling recommendation logic.</p>
   *
   * @see RecommendationService
   */
  private final RecommendationService recommendationService;

  /**
   * <h3>User Service</h3>
   * <p>Service handling user-related operations.</p>
   *
   * @see UserService
   */
  private final UserService userService;

  /**
   * <h3>Get Category Recommendations</h3>
   * <p>Generates personalized category recommendations based on user's view history.</p>
   *
   * @param principal the authenticated user
   * @return {@link CategoryRecommendationDto} with category probabilities
   */
  @GetMapping("/categories")
  public ResponseEntity<CategoryRecommendationDto> getCategoryRecommendations(Principal principal) {
    CategoryRecommendationDto recommendations =
        recommendationService.generateCategoryRecommendations(
            userService.getCurrentUser(principal));
    return ResponseEntity.ok(recommendations);
  }

  /**
   * <h3>Get User View Count</h3>
   * <p>Retrieves total number of items viewed by the user.</p>
   *
   * @param principal the authenticated user
   * @return map containing the view count
   */
  @GetMapping("/views/count")
  public ResponseEntity<Map<String, Integer>> getUserViewCount(Principal principal) {
    int totalViews = recommendationService.getUserViewCount(userService.getCurrentUser(principal));
    Map<String, Integer> response = new HashMap<>();
    response.put("totalViews", totalViews);
    return ResponseEntity.ok(response);
  }
}