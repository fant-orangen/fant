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

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

  private ItemRepository itemRepository;
  private ItemViewRepository itemViewRepository;
  private FavoriteRepository favoriteRepository;
  private ModelMapper modelMapper;
  private CategoryRepository categoryRepository;
  private ItemService itemService;

  private User seller;
  private Category category;
  private Item item;

  @BeforeEach
  void setUp() {
    itemRepository = mock(ItemRepository.class);
    itemViewRepository = mock(ItemViewRepository.class);
    favoriteRepository = mock(FavoriteRepository.class);
    modelMapper = mock(ModelMapper.class);
    categoryRepository = mock(CategoryRepository.class);
    itemService = new ItemService(itemRepository, itemViewRepository, favoriteRepository, modelMapper, categoryRepository);

    seller = User.builder().id(1L).displayName("seller").build();
    category = Category.builder().id(1L).name("Electronics").build();
    item = Item.builder().id(1L).briefDescription("Laptop").fullDescription("A good one").price(new BigDecimal("1000")).seller(seller).category(category).latitude(1.0).longitude(1.0).images(new ArrayList<>()).build();
  }

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

  @Test
  void updateItem_success() {
    ItemCreateDto dto = new ItemCreateDto();
    dto.setCategoryId(1L);
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    doNothing().when(modelMapper).map(dto, item);
    when(itemRepository.save(item)).thenReturn(item);

    ItemDetailsDto result = itemService.updateItem(seller, dto, 1L);
    assertEquals(1L, result.getId());
  }

  @Test
  void updateItem_notOwner() {
    User another = User.builder().id(2L).build();
    ItemCreateDto dto = new ItemCreateDto();
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

    assertThrows(BadCredentialsException.class, () -> itemService.updateItem(another, dto, 1L));
  }

  @Test
  void deleteItem_success() {
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    itemService.deleteItem(seller, 1L);
    verify(itemRepository).delete(item);
  }

  @Test
  void deleteItem_notOwner() {
    User another = User.builder().id(2L).build();
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    assertThrows(BadCredentialsException.class, () -> itemService.deleteItem(another, 1L));
  }

  @Test
  void adminDeleteItem_success() {
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    itemService.adminDeleteItem(1L);
    verify(itemRepository).delete(item);
  }

  @Test
  void getItemDetailsById_success() {
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    ItemDetailsDto result = itemService.getItemDetailsById(1L);
    assertEquals(item.getId(), result.getId());
  }

  @Test
  void getAllItemPreviews_success() {
    Pageable pageable = PageRequest.of(0, 10);
    when(itemRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(item)));
    Page<ItemPreviewDto> result = itemService.getAllItemPreviews(pageable);
    assertEquals(1, result.getTotalElements());
  }

  @Test
  void getItemsByCategoryId_success() {
    Pageable pageable = PageRequest.of(0, 10);
    when(itemRepository.findByCategoryId(1L, pageable)).thenReturn(new PageImpl<>(List.of(item)));
    Page<ItemPreviewDto> result = itemService.getItemsByCategoryId(1L, pageable);
    assertEquals(1, result.getTotalElements());
  }

  @Test
  void getItemsBySellerId_success() {
    Pageable pageable = PageRequest.of(0, 10);
    when(itemRepository.findBySellerId(1L, pageable)).thenReturn(new PageImpl<>(List.of(item)));
    Page<ItemPreviewDto> result = itemService.getItemsBySellerId(1L, pageable);
    assertEquals(1, result.getTotalElements());
  }

  @Test
  void recordView_success() {
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    itemService.recordView(1L, seller);
    verify(itemViewRepository).save(any());
  }

  @Test
  void getItemsByDistribution_success() {
    Map<String, Double> dist = new HashMap<>();
    dist.put("1", 1.0);
    when(itemRepository.findByCategoryId(1L)).thenReturn(List.of(item));
    Pageable pageable = PageRequest.of(0, 1);
    Page<ItemPreviewDto> result = itemService.getItemsByDistribution(dist, pageable);
    assertEquals(1, result.getContent().size());
  }

  @Test
  void getFavoritesByUserId_success() {
    Pageable pageable = PageRequest.of(0, 10);
    Favorite fav = Favorite.builder().item(item).build();
    when(favoriteRepository.findAllByUserId(1L, pageable)).thenReturn(new PageImpl<>(List.of(fav)));
    Page<ItemPreviewDto> result = itemService.getFavoritesByUserId(1L, pageable);
    assertEquals(1, result.getTotalElements());
  }

  @Test
  void getItemDetailsById_notFound() {
    when(itemRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> itemService.getItemDetailsById(1L));
  }

  @Test
  void createItem_categoryNotFound() {
    ItemCreateDto dto = new ItemCreateDto();
    dto.setCategoryId(99L);
    when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> itemService.createItem(seller, dto));
  }

  @Test
  void updateItem_itemNotFound() {
    ItemCreateDto dto = new ItemCreateDto();
    when(itemRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(seller, dto, 99L));
  }

  @Test
  void updateItem_categoryNotFound() {
    ItemCreateDto dto = new ItemCreateDto();
    dto.setCategoryId(1L);
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(seller, dto, 1L));
  }

  @Test
  void deleteItem_itemNotFound() {
    when(itemRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> itemService.deleteItem(seller, 99L));
  }

  @Test
  void adminDeleteItem_itemNotFound() {
    when(itemRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> itemService.adminDeleteItem(99L));
  }

  @Test
  void recordView_itemNotFound() {
    when(itemRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> itemService.recordView(99L, seller));
  }

  @Test
  void getItemsByDistribution_empty() {
    when(itemRepository.findByCategoryId(anyLong())).thenReturn(List.of());
    Pageable pageable = PageRequest.of(0, 10);
    Page<ItemPreviewDto> result = itemService.getItemsByDistribution(Map.of("1", 1.0), pageable);
    assertTrue(result.isEmpty());
  }
}
