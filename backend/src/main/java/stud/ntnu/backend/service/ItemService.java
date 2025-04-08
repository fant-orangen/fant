package stud.ntnu.backend.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.data.item.ItemCreateDto;
import stud.ntnu.backend.data.item.ItemDetailsDto;
import stud.ntnu.backend.data.item.ItemPreviewDto;
import stud.ntnu.backend.model.Category;
import stud.ntnu.backend.model.Favorite;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.ItemImage;
import stud.ntnu.backend.model.ItemView;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.CategoryRepository;
import stud.ntnu.backend.repository.FavoriteRepository;
import stud.ntnu.backend.repository.ItemRepository;
import stud.ntnu.backend.repository.ItemViewRepository;

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

  private final FavoriteRepository favoriteRepository;

  private final ModelMapper modelMapper;
  private final CategoryRepository categoryRepository;

  @Transactional
  public ItemDetailsDto createItem(User seller, ItemCreateDto itemCreateDto) {
    Category category = categoryRepository.findById(itemCreateDto.getCategoryId()).orElseThrow(
        () -> new EntityNotFoundException(
            "Category not found with id " + itemCreateDto.getCategoryId()));
    Item item = modelMapper.map(itemCreateDto, Item.class);
    item.setSeller(seller);
    item.setCategory(category);
    return mapToItemDetailsDto(itemRepository.save(item));
  }

  @Transactional
  public ItemDetailsDto updateItem(User seller, ItemCreateDto itemCreateDto, Long id) {
    Item item = itemRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Item not found with id " + id));
    if (!item.getSeller().equals(seller)) {
      throw new BadCredentialsException("This is not your item!");
    }
    Category category = categoryRepository.findById(itemCreateDto.getCategoryId()).orElseThrow(
        () -> new EntityNotFoundException(
            "Category not found with id " + itemCreateDto.getCategoryId()));
    modelMapper.map(itemCreateDto, item);
    item.setSeller(seller);
    item.setCategory(category);
    return mapToItemDetailsDto(itemRepository.save(item));
  }

  @Transactional
  public void deleteItem(User seller, Long id) {
    Item item = itemRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));
    if (!item.getSeller().equals(seller)) {
      throw new BadCredentialsException("You are not allowed to delete this item");
    }
    itemRepository.delete(item);
  }

  @Transactional
  public void adminDeleteItem(Long id) {
    Item item = itemRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));
    itemRepository.delete(item);
  }

  /**
   * <h3>Get All Item Previews</h3>
   * <p>Retrieves all items in simplified preview format.</p>
   *
   * @return list of {@link ItemPreviewDto}
   */
  public Page<ItemPreviewDto> getAllItemPreviews(Pageable pageable) {
    return itemRepository.findAll(pageable).map(this::mapToItemPreviewDto);
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
  public Page<ItemPreviewDto> getItemsByCategoryId(Long categoryId, Pageable pageable) {
    return itemRepository.findByCategoryId(categoryId, pageable).map(this::mapToItemPreviewDto);
  }

  /**
   * <h3>Get Items By Seller</h3>
   * <p>Retrieves items listed by a specific seller.</p>
   *
   * @param sellerId the seller's ID
   * @return list of {@link ItemPreviewDto}
   */
  public Page<ItemPreviewDto> getItemsBySellerId(Long sellerId, Pageable pageable) {
    return itemRepository.findBySellerId(sellerId, pageable)
        .map(this::mapToItemPreviewDto);
  }

  /**
   * <h3>Record Item View</h3>
   * <p>Tracks when a user views an item.</p>
   *
   * @param itemId the viewed item ID
   * @param user   the viewing user
   */
  @Transactional
  public void recordView(Long itemId, User user) {
    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + itemId));
    itemViewRepository.save(ItemView.builder().item(item).user(user).build());
  }

  public Page<ItemPreviewDto> getItemsByDistribution(Map<String, Double> distribution,
                                                     Pageable pageable) {
    int pageSize = pageable.getPageSize();
    int offset = (int) pageable.getOffset();

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
      return Page.empty(pageable);
    }

    List<ItemPreviewDto> allItems =
        selectRandomItems(categoryItemsMap, distribution, pageSize, random);

    // Apply pagination
    int end = Math.min(offset + pageSize, allItems.size());
    if (offset > allItems.size()) {
      return Page.empty(pageable);
    }

    List<ItemPreviewDto> paginatedItems = allItems.subList(offset, end);

    return new PageImpl<>(paginatedItems, pageable, allItems.size());
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
        .sellerId(item.getSeller() != null ? item.getSeller().getId() : null) // <-- Added mapping
        .imageUrls(imageUrls)
        .latitude(item.getLatitude())
        .longitude(item.getLongitude())
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

  public Page<ItemPreviewDto> getFavoritesByUserId(Long userId, Pageable pageable) {
    Page<Favorite> favoritesPage = favoriteRepository.findAllByUserId(userId, pageable);

    return favoritesPage.map(favorite -> mapToItemPreviewDto(favorite.getItem()));
  }
}