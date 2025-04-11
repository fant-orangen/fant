package stud.ntnu.backend.data.item;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import stud.ntnu.backend.model.enums.ItemStatus;

import java.math.BigDecimal;

@Data
public class ItemSearchDto { //

  @Size(max = 255, message = "Search term must be less than 255 characters")
  private String searchTerm;

  @DecimalMin(value = "0.0", message = "Minimum price cannot be negative")
  private BigDecimal minPrice;

  @DecimalMin(value = "0.0", message = "Maximum price cannot be negative")
  private BigDecimal maxPrice;

  private ItemStatus status;

  @Size(max = 100, message = "Category name must be less than 100 characters")
  private String categoryName;

  // Location parameters
  private Double userLatitude;
  private Double userLongitude;

  @DecimalMin(value = "0.0", message = "Maximum distance cannot be negative")
  private Double maxDistance; // in kilometers

  // --- NEW ---
  /**
   * Optional seller ID to filter items by a specific seller.
   */
  private Long sellerId;
  // --- END NEW ---
}