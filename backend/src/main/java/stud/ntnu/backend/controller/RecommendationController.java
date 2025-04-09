package stud.ntnu.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@Tag(name = "Recommendations", description = "Operations for generating personalized recommendations.")
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
   * @return {@link ResponseEntity} containing {@link CategoryRecommendationDto} with category probabilities
   */
  @GetMapping("/categories")
  @Operation(summary = "Get Category Recommendations", description = "Generates personalized category recommendations based on the authenticated user's view history.")
  @ApiResponse(responseCode = "200", description = "Category recommendations generated successfully", content = @Content(schema = @Schema(implementation = CategoryRecommendationDto.class)))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<CategoryRecommendationDto> getCategoryRecommendations(
      @Parameter(hidden = true) Principal principal) {
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
   * @return {@link ResponseEntity} containing a map with the total view count
   */
  @GetMapping("/views/count")
  @Operation(summary = "Get User View Count", description = "Retrieves the total number of items viewed by the authenticated user.")
  @ApiResponse(responseCode = "200", description = "User view count retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class), examples = @io.swagger.v3.oas.annotations.media.ExampleObject(name = "View Count Response", value = "{\"totalViews\": 15}")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Map<String, Integer>> getUserViewCount(
      @Parameter(hidden = true) Principal principal) {
    int totalViews = recommendationService.getUserViewCount(userService.getCurrentUser(principal));
    Map<String, Integer> response = new HashMap<>();
    response.put("totalViews", totalViews);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}