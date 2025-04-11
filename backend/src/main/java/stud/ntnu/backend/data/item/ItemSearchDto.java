package stud.ntnu.backend.data.item;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import stud.ntnu.backend.model.enums.ItemStatus;

import java.math.BigDecimal;

/**
 * <h2>ItemSearchDto</h2>
 * <p>Data transfer object for searching items with various filter criteria.</p>
 */
@Data
public class ItemSearchDto { //

  /**
   * <h3>Search Term</h3>
   * <p>The keyword or phrase to search for in item titles and descriptions.</p>
   */
  @Size(max = 255, message = "Search term must be less than 255 characters")
  private String searchTerm;

  /**
   * <h3>Minimum Price</h3>
   * <p>The lower bound for the price range filter.</p>
   */
  @DecimalMin(value = "0.0", message = "Minimum price cannot be negative")
  private BigDecimal minPrice;

  /**
   * <h3>Maximum Price</h3>
   * <p>The upper bound for the price range filter.</p>
   */
  @DecimalMin(value = "0.0", message = "Maximum price cannot be negative")
  private BigDecimal maxPrice;

  /**
   * <h3>Status</h3>
   * <p>The status filter for items (e.g., AVAILABLE, SOLD, etc.).</p>
   */
  private ItemStatus status;

  /**
   * <h3>Category Name</h3>
   * <p>The name of the category to filter items by.</p>
   */
  @Size(max = 100, message = "Category name must be less than 100 characters")
  private String categoryName;

  /**
   * <h3>User Latitude</h3>
   * <p>The latitude coordinate of the user's location for proximity search.</p>
   */
  private Double userLatitude;

  /**
   * <h3>User Longitude</h3>
   * <p>The longitude coordinate of the user's location for proximity search.</p>
   */
  private Double userLongitude;

  /**
   * <h3>Maximum Distance</h3>
   * <p>The maximum distance (in kilometers) from the user's location to search for items.</p>
   */
  @DecimalMin(value = "0.0", message = "Maximum distance cannot be negative")
  private Double maxDistance; // in kilometers

  // --- NEW ---
  /**
   * Optional seller ID to filter items by a specific seller.
   */
  private Long sellerId;
  // --- END NEW ---
}