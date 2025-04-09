// frontend 6/backend/src/main/java/stud/ntnu/backend/service/ItemService.java

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
import org.springframework.data.jpa.domain.Specification; // Import Specification
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
import stud.ntnu.backend.repository.specification.ItemSpecification; // Import ItemSpecification

/**
 * <h2>ItemService</h2>
 * <p>Service for marketplace item operations and recommendations.</p>
 */
@Service
@RequiredArgsConstructor
public class ItemService {

  // ... (Existing repositories and ModelMapper) ...
  private final ItemRepository itemRepository;
  private final ItemViewRepository itemViewRepository;
  private final FavoriteRepository favoriteRepository;
  private final ModelMapper modelMapper;
  private final CategoryRepository categoryRepository;


  // --- Existing methods (createItem, updateItem, deleteItem, etc.) ---
  @Transactional
  public ItemDetailsDto createItem(User seller, ItemCreateDto itemCreateDto) {
    Category category = categoryRepository.findById(itemCreateDto.getCategoryId()).orElseThrow(
        () -> new EntityNotFoundException(
            "Category not found with id " + itemCreateDto.getCategoryId()));
    Item item = modelMapper.map(itemCreateDto, Item.class);
    item.setId(null); // This line is crucial! Otherwise the item overwrites an existing one.
    item.setSeller(seller);
    item.setCategory(category);
    return mapToItemDetailsDto(itemRepository.save(item));
  }

  @Transactional
  public ItemDetailsDto updateItem(User seller, ItemCreateDto itemCreateDto, Long id) {
    Item item = itemRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Item not found with id " + id));
    if (!item.getSeller().getId().equals(seller.getId())) { // Use ID comparison
      throw new BadCredentialsException("This is not your item!");
    }
    Category category = categoryRepository.findById(itemCreateDto.getCategoryId()).orElseThrow(
        () -> new EntityNotFoundException(
            "Category not found with id " + itemCreateDto.getCategoryId()));
    modelMapper.map(itemCreateDto, item);
    // Ensure seller and category are set correctly after mapping
    item.setSeller(seller);
    item.setCategory(category);
    return mapToItemDetailsDto(itemRepository.save(item));
  }

  @Transactional
  public void deleteItem(User seller, Long id) {
    Item item = itemRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));
    if (!item.getSeller().getId().equals(seller.getId())) { // Use ID comparison
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

  public Page<ItemPreviewDto> getAllItemPreviews(Pageable pageable) {
    return itemRepository.findAll(pageable).map(this::mapToItemPreviewDto);
  }

  public ItemDetailsDto getItemDetailsById(Long id) {
    Item item = itemRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));
    return mapToItemDetailsDto(item);
  }

  public Page<ItemPreviewDto> getItemsByCategoryId(Long categoryId, Pageable pageable) {
    return itemRepository.findByCategoryId(categoryId, pageable).map(this::mapToItemPreviewDto);
  }

  public Page<ItemPreviewDto> getItemsBySellerId(Long sellerId, Pageable pageable) {
    return itemRepository.findBySellerId(sellerId, pageable)
        .map(this::mapToItemPreviewDto);
  }

  @Transactional
  public void recordView(Long itemId, User user) {
    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + itemId));
    itemViewRepository.save(ItemView.builder().item(item).user(user).build());
  }

  // --- Haversine Distance Calculation ---
  private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    final int R = 6371; // Radius of the earth in km
    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c; // convert to kilometers
  }

  /**
   * Searches items based on criteria, including in-memory distance filtering.
   */
  public Page<ItemPreviewDto> searchItems(ItemSearchDto searchDto, Pageable pageable) {
    // Create specification based on NON-LOCATION criteria from the DTO
    Specification<Item> spec = ItemSpecification.searchByCriteria(searchDto); // [cite: uploaded:frontend 6/backend/src/main/java/stud/ntnu/backend/repository/specification/ItemSpecification.java]

    // Fetch the paged results based on the specification (without distance DB function)
    Page<Item> itemsPage = itemRepository.findAll(spec, pageable);

    List<Item> itemsToFilter = itemsPage.getContent();
    List<Item> filteredItems;

    // Perform distance filtering in memory if location parameters are provided
    if (searchDto.getUserLatitude() != null && searchDto.getUserLongitude() != null && searchDto.getMaxDistance() != null && searchDto.getMaxDistance() > 0) {
      final double userLat = searchDto.getUserLatitude();
      final double userLon = searchDto.getUserLongitude();
      final double maxDist = searchDto.getMaxDistance();

      filteredItems = itemsToFilter.stream()
          .filter(item -> item.getLatitude() != null && item.getLongitude() != null) // Ensure item has location
          .filter(item -> calculateDistance(userLat, userLon, item.getLatitude(), item.getLongitude()) <= maxDist)
          .collect(Collectors.toList());
    } else {
      // No location filtering needed
      filteredItems = itemsToFilter;
    }

    // Map the potentially filtered list of items on the current page to DTOs
    List<ItemPreviewDto> dtoList = filteredItems.stream()
        .map(this::mapToItemPreviewDto)
        .collect(Collectors.toList());

    // Return a new Page object. Note: The totalElements count is from the pre-filtered query.
    // This is a trade-off for H2 compatibility. A more complex solution would be needed
    // for a perfectly accurate total count after in-memory filtering.
    return new PageImpl<>(dtoList, pageable, itemsPage.getTotalElements());
  }


  // --- Other existing methods (getItemsByDistribution, mapToItemPreviewDto, etc.) ---
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

    List<ItemPreviewDto> allItems = selectRandomItems(categoryItemsMap, distribution, limit,
        random);

    // Apply pagination
    int end = Math.min(offset + limit, allItems.size());
    if (offset > allItems.size()) {
      return Page.empty(pageable);
    }

    List<ItemPreviewDto> paginatedItems = allItems.subList(offset, end);

    return new PageImpl<>(paginatedItems, pageable, allItems.size());
  }

  private ItemPreviewDto mapToItemPreviewDto(Item item) {
    return ItemPreviewDto.builder()
        .id(item.getId())
        .title(item.getBriefDescription())
        .price(item.getPrice())
        .imageUrl(getFirstImageUrl(item))
        .latitude(item.getLatitude())
        .longitude(item.getLongitude())
        // Add categoryId if needed by frontend preview
        // .categoryId(item.getCategory() != null ? item.getCategory().getId().toString() : null)
        .build();
  }

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
        .sellerId(item.getSeller() != null ? item.getSeller().getId() : null)
        .imageUrls(imageUrls)
        .latitude(item.getLatitude())
        .longitude(item.getLongitude())
        .build();
  }

  private String getFirstImageUrl(Item item) {
    if (item.getImages() == null || item.getImages().isEmpty()) {
      return null;
    }
    return item.getImages().stream()
        .min(Comparator.comparing(ItemImage::getPosition))
        .map(ItemImage::getImageUrl)
        .orElse(null);
  }

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
      Collections.shuffle(categoryItems, random); // Shuffle before picking
      int count = 0;
      for (Item item : categoryItems) {
        if (count >= itemCount || result.size() >= itemLimit) break;
        result.add(mapToItemPreviewDto(item));
        count++;
      }
    });

    // Shuffle final result if distribution was probabilistic
    Collections.shuffle(result, random);

    return result.size() > itemLimit ? result.subList(0, itemLimit) : result;
  }

  public Page<ItemPreviewDto> getFavoritesByUserId(Long userId, Pageable pageable) {
    Page<Favorite> favoritesPage = favoriteRepository.findAllByUserId(userId, pageable);

    return favoritesPage.map(favorite -> mapToItemPreviewDto(favorite.getItem()));
  }
}