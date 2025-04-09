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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.data.item.ItemCreateDto;
import stud.ntnu.backend.data.item.ItemDetailsDto;
import stud.ntnu.backend.data.item.ItemPreviewDto;
import stud.ntnu.backend.data.item.ItemSearchDto;
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
import stud.ntnu.backend.repository.specification.ItemSpecification;

/**
 * <h2>ItemService</h2>
 * <p>Service for marketplace item operations and recommendations.</p>
 * <p>This service provides functionalities for creating, retrieving, updating, and deleting items
 * in the marketplace. It also handles item searches, recommendations, and user interactions such as
 * viewing and favoriting items.</p>
 */
@Service
@RequiredArgsConstructor
public class ItemService {

  /**
   * <h3>Item Repository</h3>
   * <p>Data access component for {@link Item} entities.</p>
   * <p>Provides methods for persisting and retrieving marketplace items from the database.</p>
   */
  private final ItemRepository itemRepository;

  /**
   * <h3>Item View Repository</h3>
   * <p>Data access component for {@link ItemView} entities.</p>
   * <p>Tracks user views of marketplace items for analytics and recommendation purposes.</p>
   */
  private final ItemViewRepository itemViewRepository;

  /**
   * <h3>Favorite Repository</h3>
   * <p>Data access component for {@link Favorite} entities.</p>
   * <p>Manages user favorite items for personalized marketplace experiences.</p>
   */
  private final FavoriteRepository favoriteRepository;

  /**
   * <h3>Model Mapper</h3>
   * <p>Utility for mapping between entity and DTO objects.</p>
   * <p>Simplifies conversion between domain models and data transfer objects.</p>
   */
  private final ModelMapper modelMapper;

  /**
   * <h3>Category Repository</h3>
   * <p>Data access component for {@link Category} entities.</p>
   * <p>Provides methods for retrieving item categories from the database.</p>
   */
  private final CategoryRepository categoryRepository;

  /**
   * <h3>Create Item</h3>
   * <p>Creates a new marketplace item for a seller.</p>
   *
   * @param seller        the {@link User} who is selling the item
   * @param itemCreateDto the data transfer object containing the item details
   * @return a {@link ItemDetailsDto} containing the complete details of the created item
   * @throws EntityNotFoundException if the specified category does not exist
   */
  @Transactional
  public ItemDetailsDto createItem(User seller, ItemCreateDto itemCreateDto) {
    Category category = categoryRepository.findById(itemCreateDto.getCategoryId()).orElseThrow(
        () -> new EntityNotFoundException(
            "Category not found with id " + itemCreateDto.getCategoryId()));
    Item item = modelMapper.map(itemCreateDto, Item.class);
    item.setId(null);
    item.setSeller(seller);
    item.setCategory(category);
    return mapToItemDetailsDto(itemRepository.save(item));
  }

  /**
   * <h3>Update Item</h3>
   * <p>Updates an existing marketplace item.</p>
   *
   * @param seller        the {@link User} attempting to update the item
   * @param itemCreateDto the data transfer object containing the updated item details
   * @param id            the unique identifier of the item to update
   * @return a {@link ItemDetailsDto} containing the complete details of the updated item
   * @throws EntityNotFoundException if the specified item or category does not exist
   * @throws BadCredentialsException if the user is not the original seller of the item
   */
  @Transactional
  public ItemDetailsDto updateItem(User seller, ItemCreateDto itemCreateDto, Long id) {
    Item item = itemRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Item not found with id " + id));

    if (!item.getSeller().getId().equals(seller.getId())) {
      throw new BadCredentialsException("This is not your item!");
    }

    Category category = categoryRepository.findById(itemCreateDto.getCategoryId()).orElseThrow(
        () -> new EntityNotFoundException(
            "Category not found with id " + itemCreateDto.getCategoryId()));

    // Preserve the original images and ID
    List<ItemImage> originalImages = item.getImages();

    // Manually update properties instead of using modelMapper
    item.setBriefDescription(itemCreateDto.getBriefDescription());
    item.setFullDescription(itemCreateDto.getFullDescription());
    item.setPrice(itemCreateDto.getPrice());
    item.setLatitude(itemCreateDto.getLatitude());
    item.setLongitude(itemCreateDto.getLongitude());

    // Set seller and category
    item.setSeller(seller);
    item.setCategory(category);

    // Handle images if provided in the DTO
    if (itemCreateDto.getImages() != null && !itemCreateDto.getImages().isEmpty()) {
      // Clear existing images and add new ones
      originalImages.clear();
      for (ItemImage newImage : itemCreateDto.getImages()) {
        newImage.setItem(item);
        originalImages.add(newImage);
      }
    }

    return mapToItemDetailsDto(itemRepository.save(item));
  }

  /**
   * <h3>Delete Item</h3>
   * <p>Removes a marketplace item if the requesting user is the seller.</p>
   *
   * @param seller the {@link User} attempting to delete the item
   * @param id     the unique identifier of the item to delete
   * @throws EntityNotFoundException if the specified item does not exist
   * @throws BadCredentialsException if the user is not the original seller of the item
   */
  @Transactional
  public void deleteItem(User seller, Long id) {
    Item item = itemRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));
    if (!item.getSeller().getId().equals(seller.getId())) { // Use ID comparison
      throw new BadCredentialsException("You are not allowed to delete this item");
    }
    itemRepository.delete(item);
  }

  /**
   * <h3>Admin Delete Item</h3>
   * <p>Removes a marketplace item with administrative privileges.</p>
   *
   * @param id the unique identifier of the item to delete
   * @throws EntityNotFoundException if the specified item does not exist
   */
  @Transactional
  public void adminDeleteItem(Long id) {
    Item item = itemRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));
    itemRepository.delete(item);
  }

  /**
   * <h3>Get All Item Previews</h3>
   * <p>Retrieves a paginated list of all marketplace items.</p>
   *
   * @param pageable the pagination information
   * @return a {@link Page} of {@link ItemPreviewDto} objects containing basic item information
   */
  public Page<ItemPreviewDto> getAllItemPreviews(Pageable pageable) {
    return itemRepository.findAll(pageable).map(this::mapToItemPreviewDto);
  }

  /**
   * <h3>Get Item Details By ID</h3>
   * <p>Retrieves complete details for a specific marketplace item.</p>
   *
   * @param id the unique identifier of the item
   * @return an {@link ItemDetailsDto} containing the complete details of the item
   * @throws EntityNotFoundException if the specified item does not exist
   */
  public ItemDetailsDto getItemDetailsById(Long id) {
    Item item = itemRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));
    return mapToItemDetailsDto(item);
  }

  /**
   * <h3>Get Items By Category</h3>
   * <p>Retrieves a paginated list of marketplace items in a specific category.</p>
   *
   * @param categoryId the unique identifier of the category to filter by
   * @param pageable   the pagination information
   * @return a {@link Page} of {@link ItemPreviewDto} objects in the specified category
   */
  public Page<ItemPreviewDto> getItemsByCategoryId(Long categoryId, Pageable pageable) {
    return itemRepository.findByCategoryId(categoryId, pageable).map(this::mapToItemPreviewDto);
  }

  /**
   * <h3>Get Items By Seller</h3>
   * <p>Retrieves a paginated list of marketplace items from a specific seller.</p>
   *
   * @param sellerId the unique identifier of the seller to filter by
   * @param pageable the pagination information
   * @return a {@link Page} of {@link ItemPreviewDto} objects from the specified seller
   */
  public Page<ItemPreviewDto> getItemsBySellerId(Long sellerId, Pageable pageable) {
    return itemRepository.findBySellerId(sellerId, pageable).map(this::mapToItemPreviewDto);
  }

  /**
   * <h3>Record Item View</h3>
   * <p>Records that a user has viewed a specific marketplace item.</p>
   *
   * @param itemId the unique identifier of the item that was viewed
   * @param user   the {@link User} who viewed the item
   * @throws EntityNotFoundException if the specified item does not exist
   */
  @Transactional
  public void recordView(Long itemId, User user) {
    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + itemId));
    itemViewRepository.save(ItemView.builder().item(item).user(user).build());
  }

  /**
   * <h3>Calculate Distance</h3>
   * <p>Calculates the distance between two geographic coordinates using the Haversine formula.</p>
   *
   * @param lat1 the latitude of the first point
   * @param lon1 the longitude of the first point
   * @param lat2 the latitude of the second point
   * @param lon2 the longitude of the second point
   * @return the distance between the points in kilometers
   */
  private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    final int R = 6371; // Radius of the earth in km
    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
        Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c; // convert to kilometers
  }

  /**
   * <h3>Search Items</h3>
   * <p>Searches for marketplace items based on various criteria, including location-based filtering.</p>
   * <p>This method combines database filtering with in-memory distance calculations for location-based searches.</p>
   *
   * @param searchDto the search criteria including text, category, price range, and location parameters
   * @param pageable  the pagination information
   * @return a {@link Page} of {@link ItemPreviewDto} objects matching the search criteria
   */
  public Page<ItemPreviewDto> searchItems(ItemSearchDto searchDto, Pageable pageable) {
    // Create specification based on NON-LOCATION criteria from the DTO
    Specification<Item> spec = ItemSpecification.searchByCriteria(searchDto);

    // Fetch the paged results based on the specification (without distance DB function)
    Page<Item> itemsPage = itemRepository.findAll(spec, pageable);

    List<Item> itemsToFilter = itemsPage.getContent();
    List<Item> filteredItems;

    // Perform distance filtering in memory if location parameters are provided
    if (searchDto.getUserLatitude() != null && searchDto.getUserLongitude() != null &&
        searchDto.getMaxDistance() != null && searchDto.getMaxDistance() > 0) {
      final double userLat = searchDto.getUserLatitude();
      final double userLon = searchDto.getUserLongitude();
      final double maxDist = searchDto.getMaxDistance();

      filteredItems = itemsToFilter.stream().filter(item -> item.getLatitude() != null &&
              item.getLongitude() != null) // Ensure item has location
          .filter(item ->
              calculateDistance(userLat, userLon, item.getLatitude(), item.getLongitude()) <=
                  maxDist).collect(Collectors.toList());
    } else {
      // No location filtering needed
      filteredItems = itemsToFilter;
    }

    // Map the potentially filtered list of items on the current page to DTOs
    List<ItemPreviewDto> dtoList =
        filteredItems.stream().map(this::mapToItemPreviewDto).collect(Collectors.toList());

    // Return a new Page object. Note: The totalElements count is from the pre-filtered query.
    // This is a trade-off for H2 compatibility. A more complex solution would be needed
    // for a perfectly accurate total count after in-memory filtering.
    return new PageImpl<>(dtoList, pageable, itemsPage.getTotalElements());
  }

  /**
   * <h3>Get Items By Distribution</h3>
   * <p>Retrieves marketplace items according to a specified categorical distribution.</p>
   * <p>This method is useful for personalized recommendations where items from different
   * categories are needed in specific proportions.</p>
   *
   * @param distribution a map of category IDs to their proportional weights
   * @param pageable     the pagination information
   * @param limit        the maximum number of items to return
   * @return a {@link Page} of {@link ItemPreviewDto} objects distributed according to the weights
   */
  public Page<ItemPreviewDto> getItemsByDistribution(Map<String, Double> distribution,
                                                     Pageable pageable, Integer limit) {
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
        selectRandomItems(categoryItemsMap, distribution, limit, random);

    // Apply pagination
    int end = Math.min(offset + limit, allItems.size());
    if (offset > allItems.size()) {
      return Page.empty(pageable);
    }

    List<ItemPreviewDto> paginatedItems = allItems.subList(offset, end);

    return new PageImpl<>(paginatedItems, pageable, allItems.size());
  }

  /**
   * <h3>Map To Item Preview DTO</h3>
   * <p>Converts an {@link Item} entity to an {@link ItemPreviewDto} for list display.</p>
   *
   * @param item the item entity to convert
   * @return an {@link ItemPreviewDto} containing the basic preview information
   */
  private ItemPreviewDto mapToItemPreviewDto(Item item) {
    return ItemPreviewDto.builder().id(item.getId()).title(item.getBriefDescription())
        .price(item.getPrice()).imageUrl(getFirstImageUrl(item)).latitude(item.getLatitude())
        .longitude(item.getLongitude())
        // Add categoryId if needed by frontend preview
        // .categoryId(item.getCategory() != null ? item.getCategory().getId().toString() : null)
        .build();
  }

  /**
   * <h3>Map To Item Details DTO</h3>
   * <p>Converts an {@link Item} entity to an {@link ItemDetailsDto} for detailed display.</p>
   *
   * @param item the item entity to convert
   * @return an {@link ItemDetailsDto} containing the complete item details
   */
  private ItemDetailsDto mapToItemDetailsDto(Item item) {
    List<String> imageUrls = item.getImages() != null ?
        item.getImages().stream().sorted(Comparator.comparing(ItemImage::getPosition))
            .map(ItemImage::getImageUrl).collect(Collectors.toList()) : List.of();

    return ItemDetailsDto.builder().id(item.getId()).title(item.getBriefDescription())
        .description(item.getFullDescription())
        .category(item.getCategory() != null ? item.getCategory().getName() : "")
        .price(item.getPrice())
        .contact(item.getSeller() != null ? item.getSeller().getDisplayName() : "")
        .sellerId(item.getSeller() != null ? item.getSeller().getId() : null).imageUrls(imageUrls)
        .latitude(item.getLatitude()).longitude(item.getLongitude()).build();
  }

  /**
   * <h3>Get First Image URL</h3>
   * <p>Retrieves the URL of the first image associated with an item.</p>
   *
   * @param item the item entity
   * @return the URL of the first image, or null if no images exist
   */
  private String getFirstImageUrl(Item item) {
    if (item.getImages() == null || item.getImages().isEmpty()) {
      return null;
    }
    return item.getImages().stream().min(Comparator.comparing(ItemImage::getPosition))
        .map(ItemImage::getImageUrl).orElse(null);
  }

  /**
   * <h3>Select Random Items</h3>
   * <p>Selects items from different categories according to a specified distribution.</p>
   *
   * @param categoryItemsMap a map of category IDs to their associated items
   * @param distribution     a map of category IDs to their proportional weights
   * @param itemLimit        the maximum number of items to select
   * @param random           a random number generator for consistent shuffling
   * @return a list of {@link ItemPreviewDto} objects distributed according to the weights
   */
  private List<ItemPreviewDto> selectRandomItems(Map<Long, List<Item>> categoryItemsMap,
                                                 Map<String, Double> distribution, int itemLimit,
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
      Collections.shuffle(categoryItems, random); // Shuffle before picking
      int count = 0;
      for (Item item : categoryItems) {
        if (count >= itemCount || result.size() >= itemLimit) {
          break;
        }
        result.add(mapToItemPreviewDto(item));
        count++;
      }
    });

    // Shuffle final result if distribution was probabilistic
    Collections.shuffle(result, random);

    return result.size() > itemLimit ? result.subList(0, itemLimit) : result;
  }

  /**
   * <h3>Get Favorites By User ID</h3>
   * <p>Retrieves a paginated list of items favorited by a specific user.</p>
   *
   * @param userId   the unique identifier of the user
   * @param pageable the pagination information
   * @return a {@link Page} of {@link ItemPreviewDto} objects representing the user's favorites
   */
  public Page<ItemPreviewDto> getFavoritesByUserId(Long userId, Pageable pageable) {
    Page<Favorite> favoritesPage = favoriteRepository.findAllByUserId(userId, pageable);

    return favoritesPage.map(favorite -> mapToItemPreviewDto(favorite.getItem()));
  }
}