package stud.ntnu.backend.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequestDto {
  @NotBlank(message = "Category name is required")
  @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
  private String name;

  @Size(max = 255, message = "Image URL cannot exceed 255 characters")
  private String imageUrl;

  @PositiveOrZero(message = "Parent ID must be a positive number or zero")
  private Long parentId;
}
