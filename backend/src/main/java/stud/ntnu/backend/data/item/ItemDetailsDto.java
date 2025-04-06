package stud.ntnu.backend.data.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * <h2>ItemDetailsDto</h2>
 * <p>Data Transfer Object that contains comprehensive information about marketplace items.</p>
 * <p>This DTO is designed to provide all relevant item details needed for display on item detail pages.</p>
 * <p>The object is mapped from the {@link stud.ntnu.backend.model.Item} entity.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDetailsDto {

    /**
     * <h3>Unique identifier for the item</h3>
     * <p>Corresponds to the primary key in the items table.</p>
     */
    private Long id;

    /**
     * <h3>Title of the item</h3>
     * <p>Mapped from the briefDescription field of the Item entity.</p>
     */
    private String title;

    /**
     * <h3>Full description of the item</h3>
     * <p>Mapped from the fullDescription field of the Item entity.</p>
     */
    private String description;

    /**
     * <h3>Category of the item</h3>
     * <p>Name of the category the item belongs to.</p>
     */
    private String category;

    /**
     * <h3>Price of the item</h3>
     * <p>Uses BigDecimal for precise monetary calculations.</p>
     */
    private BigDecimal price;

    /**
     * <h3>Contact information for the seller</h3>
     * <p>Username of the seller.</p>
     */
    private String contact;

    /**
     * <h3>URLs to the images of the item</h3>
     * <p>List of image URLs associated with the item.</p>
     */
    private List<String> imageUrls;

    /**
    * <h3>Latitude of the item</h3>
    * <p>Uses Double for precise position</p>
    */
    private Double latitude;

    /**
    * <h3>Longitude of the item</h3>
    * <p>Uses Double for precise position</p>
    */
    private Double longitude;
}