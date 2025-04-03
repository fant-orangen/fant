package stud.ntnu.backend.controller;

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
}