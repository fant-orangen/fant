package stud.ntnu.backend.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.data.ItemDetailsDto;
import stud.ntnu.backend.data.ItemPreviewDto;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.ItemImage;
import stud.ntnu.backend.model.ItemView;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.ItemRepository;
import stud.ntnu.backend.repository.ItemViewRepository;
import stud.ntnu.backend.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h2>ItemService</h2>
 * <p>Service for marketplace item operations and recommendations.</p>
 */
@Service
@RequiredArgsConstructor
public class ItemService {

  /**
   * <h3>Item Repository</h3>
   * <p>Data access component for items.</p>
   */
  private final ItemRepository itemRepository;

  /**
   * <h3>Item View Repository</h3>
   * <p>Data access component for item view tracking.</p>
   */
  private final ItemViewRepository itemViewRepository;

  /**
   * <h3>Get All Item Previews</h3>
   * <p>Retrieves all items in simplified preview format.</p>
   *
   * @return list of {@link ItemPreviewDto}
   */
  public List<ItemPreviewDto> getAllItemPreviews() {
    return itemRepository.findAll().stream()
        .map(this::mapToItemPreviewDto)
        .collect(Collectors.toList());
  }

  /**
   * <h3>Get Item Details</h3>
   * <p>Retrieves complete details for a specific item.</p>
   *
   * @param id the item ID
   * @return {@link ItemDetailsDto}
   * @throws EntityNotFoundException if item not found
   */
  public ItemDetailsDto getItemDetailsById(Long id) {
    Item item = itemRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));
    return mapToItemDetailsDto(item);
  }

  /**
   * <h3>Get Items By Category</h3>
   * <p>Retrieves items belonging to a specific category.</p>
   *
   * @param categoryId the category ID
   * @return list of {@link ItemPreviewDto}
   */
  public List<ItemPreviewDto> getItemsByCategory(Long categoryId) {
    return itemRepository.findByCategoryId(categoryId).stream()
        .map(this::mapToItemPreviewDto)
        .collect(Collectors.toList());
  }

  /**
   * <h3>Record Item View</h3>
   * <p>Tracks when a user views an item.</p>
   *
   * @param itemId the viewed item ID
   * @param user   the viewing user
   */
  public void recordView(Long itemId, User user) {
    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + itemId));
    itemViewRepository.save(ItemView.builder().item(item).user(user).build());
  }

  /**
   * <h3>Get Recommended Items</h3>
   * <p>Generates item recommendations based on category distribution.</p>
   *
   * @param distribution map of category probabilities
   * @param limit        maximum items to return
   * @return list of recommended {@link ItemPreviewDto}
   */
  public List<ItemPreviewDto> getItemsByDistribution(
      Map<String, Double> distribution, Integer limit) {
    int itemLimit = (limit != null && limit > 0) ? Math.min(limit, 1000) : 1000;
    Map<Long, List<Item>> categoryItemsMap = new HashMap<>();
    Random random = new Random();

    distribution.keySet().forEach(categoryIdStr -> {
      Long categoryId = Long.parseLong(categoryIdStr);
      List<Item> items = itemRepository.findByCategoryId(categoryId);
      if (!items.isEmpty()) {
        categoryItemsMap.put(categoryId, items);
      }
    });

    if (categoryItemsMap.isEmpty()) {
      return Collections.emptyList();
    }

    return selectRandomItems(categoryItemsMap, distribution, itemLimit, random);
  }

  /**
   * <h3>Map To Item Preview</h3>
   * <p>Converts Item entity to preview DTO.</p>
   *
   * @param item the item to convert
   * @return {@link ItemPreviewDto}
   */
  private ItemPreviewDto mapToItemPreviewDto(Item item) {
    return ItemPreviewDto.builder()
        .id(item.getId())
        .title(item.getBriefDescription())
        .price(item.getPrice())
        .imageUrl(getFirstImageUrl(item))
        .latitude(item.getLatitude())
        .longitude(item.getLongitude())
        .build();
  }

  /**
   * <h3>Map To Item Details</h3>
   * <p>Converts Item entity to details DTO.</p>
   *
   * @param item the item to convert
   * @return {@link ItemDetailsDto}
   */
  private ItemDetailsDto mapToItemDetailsDto(Item item) {
    List<String> imageUrls = item.getImages() != null ?
        item.getImages().stream()
            .sorted(Comparator.comparing(ItemImage::getPosition))
            .map(ItemImage::getImageUrl)
            .collect(Collectors.toList()) :
        List.of();

    return ItemDetailsDto.builder()
        .id(item.getId())
        .title(item.getBriefDescription())
        .description(item.getFullDescription())
        .category(item.getCategory() != null ? item.getCategory().getName() : "")
        .price(item.getPrice())
        .contact(item.getSeller() != null ? item.getSeller().getDisplayName() : "")
        .imageUrls(imageUrls)
        .build();
  }

  /**
   * <h3>Get First Image URL</h3>
   * <p>Retrieves URL of primary item image.</p>
   *
   * @param item the item to check
   * @return image URL or null
   */
  private String getFirstImageUrl(Item item) {
    if (item.getImages() == null || item.getImages().isEmpty()) {
      return null;
    }
    return item.getImages().stream()
        .min(Comparator.comparing(ItemImage::getPosition))
        .map(ItemImage::getImageUrl)
        .orElse(null);
  }

  /**
   * <h3>Select Random Items</h3>
   * <p>Helper method for recommendation selection logic.</p>
   */
  private List<ItemPreviewDto> selectRandomItems(
      Map<Long, List<Item>> categoryItemsMap,
      Map<String, Double> distribution,
      int itemLimit,
      Random random) {
    List<ItemPreviewDto> result = new ArrayList<>();
    Map<Long, Integer> categoryItemCounts = new HashMap<>();

    distribution.forEach((categoryIdStr, probability) -> {
      Long categoryId = Long.parseLong(categoryIdStr);
      if (categoryItemsMap.containsKey(categoryId)) {
        categoryItemCounts.put(categoryId, (int) Math.ceil(itemLimit * probability));
      }
    });

    categoryItemCounts.forEach((categoryId, itemCount) -> {
      List<Item> categoryItems = new ArrayList<>(categoryItemsMap.get(categoryId));
      for (int i = 0; i < itemCount && result.size() < itemLimit && !categoryItems.isEmpty(); i++) {
        int randomIndex = random.nextInt(categoryItems.size());
        result.add(mapToItemPreviewDto(categoryItems.remove(randomIndex)));
      }
    });

    Collections.shuffle(result);
    return result.size() > itemLimit ? result.subList(0, itemLimit) : result;
  }
}