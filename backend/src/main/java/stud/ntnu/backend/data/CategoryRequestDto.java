package stud.ntnu.backend.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * <h2>CategoryRequestDto</h2>
 * <p>Data transfer object for category creation and updates.</p>
 */
@Data
public class CategoryRequestDto {

  /**
   * <h3>Name</h3>
   * <p>The name of the category (2-50 characters).</p>
   */
  @NotBlank(message = "Category name is required")
  @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
  private String name;

  /**
   * <h3>Image URL</h3>
   * <p>Optional URL for category image (max 255 characters).</p>
   */
  @Size(max = 255, message = "Image URL cannot exceed 255 characters")
  private String imageUrl;

  /**
   * <h3>Parent ID</h3>
   * <p>Optional parent category identifier (positive number or zero).</p>
   */
  @PositiveOrZero(message = "Parent ID must be a positive number or zero")
  private Long parentId;
}