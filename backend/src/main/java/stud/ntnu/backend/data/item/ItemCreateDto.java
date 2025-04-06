package stud.ntnu.backend.data.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import stud.ntnu.backend.model.ItemImage;

@Data
public class ItemCreateDto {

  @NotBlank
  private Long categoryId;

  @NotBlank
  @Size(max = 255)
  private String briefDescription;

  private String fullDescription;

  @NotBlank
  private BigDecimal price;

  private Double latitude;
  private Double longitude;

  private List<ItemImage> images;
}
