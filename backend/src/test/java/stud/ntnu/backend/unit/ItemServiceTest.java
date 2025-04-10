package stud.ntnu.backend.unit;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import stud.ntnu.backend.data.item.ItemCreateDto;
import stud.ntnu.backend.data.item.ItemDetailsDto;
import stud.ntnu.backend.data.item.ItemPreviewDto;
import stud.ntnu.backend.model.*;
import stud.ntnu.backend.repository.*;

import java.math.BigDecimal;
import java.util.*;
import stud.ntnu.backend.service.ItemService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * <h2>Unit Tests for Item Service</h2>
 * <p>This class contains unit tests for the {@link ItemService} class, focusing on the business logic for creating, retrieving, updating, and deleting items, as well as handling favorites and views.</p>
 * <p>The tests utilize Mockito to mock the dependencies of the {@code ItemService}, such as {@link ItemRepository}, {@link ItemViewRepository}, {@link FavoriteRepository}, {@link ModelMapper}, and {@link CategoryRepository}, allowing for isolated testing of the service's methods.</p>
 */
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

  /**
   * <h3>Mock ItemRepository</h3>
   * <p>A Mockito mock for the {@link ItemRepository} dependency, used to simulate database interactions related to items.</p>
   */
  private ItemRepository itemRepository;

  /**
   * <h3>Mock ItemViewRepository</h3>
   * <p>A Mockito mock for the {@link ItemViewRepository} dependency, used to simulate database interactions related to item views.</p>
   */
  private ItemViewRepository itemViewRepository;

  /**
   * <h3>Mock FavoriteRepository</h3>
   * <p>A Mockito mock for the {@link FavoriteRepository} dependency, used to simulate database interactions related to user favorites.</p>
   */
  private FavoriteRepository favoriteRepository;

  /**
   * <h3>Mock ModelMapper</h3>
   * <p>A Mockito mock for the {@link ModelMapper} dependency, used for mapping between DTOs and entities.</p>
   */
  private ModelMapper modelMapper;

  /**
   * <h3>Mock CategoryRepository</h3>
   * <p>A Mockito mock for the {@link CategoryRepository} dependency, used to simulate database interactions related to categories.</p>
   */
  private CategoryRepository categoryRepository;

  /**
   * <h3>InjectMocks ItemService</h3>
   * <p>The instance of {@link ItemService} being tested, with its dependencies injected via Mockito.</p>
   */
  private ItemService itemService;

  /**
   * <h3>Test Seller User</h3>
   * <p>A {@link User} entity representing the seller in the test scenarios.</p>
   */
  private User seller;

  /**
   * <h3>Test Category</h3>
   * <p>A {@link Category} entity used for associating items in the test scenarios.</p>
   */
  private Category category;

  /**
   * <h3>Test Item</h3>
   * <p>An {@link Item} entity used for testing item-related operations.</p>
   */
  private Item item;

  /**
   * <h3>Setup Method</h3>
   * <p>This method is executed before each test case. It initializes mock repositories, a mock model mapper, and a test seller user, category, and item.</p>
   */
  @BeforeEach
  void setUp() {
    itemRepository = mock(ItemRepository.class);
    itemViewRepository = mock(ItemViewRepository.class);
    favoriteRepository = mock(FavoriteRepository.class);
    modelMapper = mock(ModelMapper.class);
    categoryRepository = mock(CategoryRepository.class);
    itemService =
        new ItemService(itemRepository, itemViewRepository, favoriteRepository, modelMapper,
            categoryRepository);

    seller = User.builder().id(1L).displayName("seller").build();
    category = Category.builder().id(1L).name("Electronics").build();
    item = Item.builder().id(1L).briefDescription("Laptop").fullDescription("A good one")
        .price(new BigDecimal("1000")).seller(seller).category(category).latitude(1.0)
        .longitude(1.0).images(new ArrayList<>()).build();
  }

  /**
   * <h3>Test Create Item - Success</h3>
   * <p>Tests the successful creation of a new item. It verifies that the category exists, the DTO is mapped to an item, and the item is saved to the repository.</p>
   */
  @Test
  void createItem_success() {
    ItemCreateDto dto = new ItemCreateDto();
    dto.setCategoryId(1L);
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(modelMapper.map(dto, Item.class)).thenReturn(new Item());
    when(itemRepository.save(any())).thenReturn(item);

    ItemDetailsDto result = itemService.createItem(seller, dto);
    assertNotNull(result);
    verify(itemRepository).save(any());
  }

  /**
   * <h3>Test Update Item - Success</h3>
   * <p>Tests the successful update of an existing item. It verifies that the item and category exist, the DTO's properties are mapped to the existing item, and the updated item is saved.</p>
   */
  @Test
  void updateItem_success() {
    ItemCreateDto dto = new ItemCreateDto();
    dto.setCategoryId(1L);
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(itemRepository.save(item)).thenReturn(item);

    ItemDetailsDto result = itemService.updateItem(seller, dto, 1L);
    assertNotNull(result);
    assertEquals(1L, result.getId());
  }

  /**
   * <h3>Test Update Item - Not Owner</h3>
   * <p>Tests the scenario where a user who is not the seller attempts to update an item. It verifies that a {@link BadCredentialsException} is thrown.</p>
   */
  @Test
  void updateItem_notOwner() {
    User another = User.builder().id(2L).build();
    ItemCreateDto dto = new ItemCreateDto();
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

    assertThrows(BadCredentialsException.class, () -> itemService.updateItem(another, dto, 1L));
  }

  /**
   * <h3>Test Delete Item - Success</h3>
   * <p>Tests the successful deletion of an item by its owner. It verifies that the item is found and then deleted from the repository.</p>
   */
  @Test
  void deleteItem_success() {
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    itemService.deleteItem(seller, 1L);
    verify(itemRepository).delete(item);
  }

  /**
   * <h3>Test Delete Item - Not Owner</h3>
   * <p>Tests the scenario where a user who is not the seller attempts to delete an item. It verifies that a {@link BadCredentialsException} is thrown.</p>
   */
  @Test
  void deleteItem_notOwner() {
    User another = User.builder().id(2L).build();
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    assertThrows(BadCredentialsException.class, () -> itemService.deleteItem(another, 1L));
  }

  /**
   * <h3>Test Admin Delete Item - Success</h3>
   * <p>Tests the successful deletion of an item by an administrator. It verifies that the item is found and then deleted from the repository.</p>
   */
  @Test
  void adminDeleteItem_success() {
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    itemService.adminDeleteItem(1L);
    verify(itemRepository).delete(item);
  }

  /**
   * <h3>Test Get Item Details By ID - Success</h3>
   * <p>Tests the successful retrieval of item details by its ID. It verifies that the item is found and mapped to an {@link ItemDetailsDto}.</p>
   */
  @Test
  void getItemDetailsById_success() {
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    ItemDetailsDto result = itemService.getItemDetailsById(1L);
    assertEquals(item.getId(), result.getId());
  }

  /**
   * <h3>Test Get All Item Previews - Success</h3>
   * <p>Tests the successful retrieval of all item previews with pagination. It verifies that the repository's {@code findAll} method is called and the results are mapped to {@link ItemPreviewDto} objects.</p>
   */
  @Test
  void getAllItemPreviews_success() {
    Pageable pageable = PageRequest.of(0, 10);
    when(itemRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(item)));
    Page<ItemPreviewDto> result = itemService.getAllItemPreviews(pageable);
    assertEquals(1, result.getTotalElements());
  }

  /**
   * <h3>Test Get Items By Category ID - Success</h3>
   * <p>Tests the successful retrieval of item previews by category ID with pagination. It verifies that the repository's {@code findByCategoryId} method is called and the results are mapped to {@link ItemPreviewDto} objects.</p>
   */
  @Test
  void getItemsByCategoryId_success() {
    Pageable pageable = PageRequest.of(0, 10);
    when(itemRepository.findByCategoryId(1L, pageable)).thenReturn(new PageImpl<>(List.of(item)));
    Page<ItemPreviewDto> result = itemService.getItemsByCategoryId(1L, pageable);
    assertEquals(1, result.getTotalElements());
  }

  /**
   * <h3>Test Get Items By Seller ID - Success</h3>
   * <p>Tests the successful retrieval of item previews by seller ID with pagination. It verifies that the repository's {@code findBySellerId} method is called and the results are mapped to {@link ItemPreviewDto} objects.</p>
   */
  @Test
  void getItemsBySellerId_success() {
    Pageable pageable = PageRequest.of(0, 10);
    when(itemRepository.findBySellerId(1L, pageable)).thenReturn(new PageImpl<>(List.of(item)));
    Page<ItemPreviewDto> result = itemService.getItemsBySellerId(1L, pageable);
    assertEquals(1, result.getTotalElements());
  }

  /**
   * <h3>Test Record View - Success</h3>
   * <p>Tests the successful recording of an item view by a user. It verifies that the item exists and a new {@link ItemView} is saved to the repository.</p>
   */
  @Test
  void recordView_success() {
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    itemService.recordView(1L, seller);
    verify(itemViewRepository).save(any());
  }

  /**
   * <h3>Test Get Items By Distribution - Success</h3>
   * <p>Tests the retrieval of items based on a distribution map (e.g., for recommendations). It verifies that items are fetched by category and mapped to {@link ItemPreviewDto} objects.</p>
   */
  @Test
  void getItemsByDistribution_success() {
    Map<String, Double> dist = new HashMap<>();
    dist.put("1", 1.0);
    when(itemRepository.findByCategoryId(1L)).thenReturn(List.of(item));
    Pageable pageable = PageRequest.of(0, 1);
    Page<ItemPreviewDto> result = itemService.getItemsByDistribution(dist, pageable,
        1);
    assertEquals(1, result.getContent().size());
  }

  /**
   * <h3>Test Get Favorites By User ID - Success</h3>
   * <p>Tests the successful retrieval of a user's favorite items with pagination. It verifies that the repository's {@code findAllByUserId} method is called and the results are mapped to {@link ItemPreviewDto} objects.</p>
   */
  @Test
  void getFavoritesByUserId_success() {
    Pageable pageable = PageRequest.of(0, 10);
    Favorite fav = Favorite.builder().item(item).build();
    when(favoriteRepository.findAllByUserId(1L, pageable)).thenReturn(new PageImpl<>(List.of(fav)));
    Page<ItemPreviewDto> result = itemService.getFavoritesByUserId(1L, pageable);
    assertEquals(1, result.getTotalElements());
  }

  /**
   * <h3>Test Get Item Details By ID - Not Found</h3>
   * <p>Tests the scenario where an attempt is made to retrieve item details for a non-existent item. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void getItemDetailsById_notFound() {
    when(itemRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> itemService.getItemDetailsById(1L));
  }

  /**
   * <h3>Test Create Item - Category Not Found</h3>
   * <p>Tests the scenario where an attempt is made to create an item with a non-existent category ID. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void createItem_categoryNotFound() {
    ItemCreateDto dto = new ItemCreateDto();
    dto.setCategoryId(99L);
    when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> itemService.createItem(seller, dto));
  }

  /**
   * <h3>Test Update Item - Item Not Found</h3>
   * <p>Tests the scenario where an attempt is made to update a non-existent item. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void updateItem_itemNotFound() {
    ItemCreateDto dto = new ItemCreateDto();
    when(itemRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(seller, dto, 99L));
  }

  /**
   * <h3>Test Update Item - Category Not Found</h3>
   * <p>Tests the scenario where an attempt is made to update an item and the provided category ID does not exist. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void updateItem_categoryNotFound() {
    ItemCreateDto dto = new ItemCreateDto();
    dto.setCategoryId(1L);
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(seller, dto, 1L));
  }

  /**
   * <h3>Test Delete Item - Item Not Found</h3>
   * <p>Tests the scenario where an attempt is made to delete a non-existent item. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void deleteItem_itemNotFound() {
    when(itemRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> itemService.deleteItem(seller, 99L));
  }

  /**
   * <h3>Test Admin Delete Item - Item Not Found</h3>
   * <p>Tests the scenario where an attempt is made to delete a non-existent item by an administrator. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void adminDeleteItem_itemNotFound() {
    when(itemRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> itemService.adminDeleteItem(99L));
  }

  /**
   * <h3>Test Record View - Item Not Found</h3>
   * <p>Tests the scenario where an attempt is made to record a view for a non-existent item. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void recordView_itemNotFound() {
    when(itemRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> itemService.recordView(99L, seller));
  }

  /**
   * <h3>Test Get Items By Distribution - Empty Result</h3>
   * <p>Tests the scenario where the category IDs in the distribution map do not yield any items. It verifies that an empty page of {@link ItemPreviewDto} is returned.</p>
   */
  @Test
  void getItemsByDistribution_empty() {
    when(itemRepository.findByCategoryId(anyLong())).thenReturn(List.of());
    Pageable pageable = PageRequest.of(0, 10);
    Page<ItemPreviewDto> result = itemService.getItemsByDistribution(Map.of("1", 1.0), pageable, 1);
    assertTrue(result.isEmpty());
  }
}