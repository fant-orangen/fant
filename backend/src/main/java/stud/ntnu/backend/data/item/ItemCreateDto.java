package stud.ntnu.backend.data.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import stud.ntnu.backend.model.ItemImage;

/**
 * <h2>ItemCreateDto</h2>
 * <p>Data transfer object for creating a new item.</p>
 */
@Data
public class ItemCreateDto {

  /**
   * <h3>Category ID</h3>
   * <p>The ID of the category to which the item belongs.</p>
   */
  @NotNull
  @Positive
  private Long categoryId;

  /**
   * <h3>Brief Description</h3>
   * <p>A short description of the item (max 255 characters).</p>
   */
  @NotBlank
  @Size(max = 255)
  private String briefDescription;

  /**
   * <h3>Full Description</h3>
   * <p>A detailed description of the item.</p>
   */
  private String fullDescription;

  /**
   * <h3>Price</h3>
   * <p>The price of the item.</p>
   */
  @NotNull
  @Positive
  private BigDecimal price;

  /**
   * <h3>Latitude</h3>
   * <p>The latitude coordinate of the item's location.</p>
   */
  private Double latitude;

  /**
   * <h3>Longitude</h3>
   * <p>The longitude coordinate of the item's location.</p>
   */
  private Double longitude;

  /**
   * <h3>Images</h3>
   * <p>A list of images associated with the item.</p>
   */
  private List<ItemImage> images;
}