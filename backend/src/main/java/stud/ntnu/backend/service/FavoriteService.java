package stud.ntnu.backend.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.data.FavoriteResponseDto;
import stud.ntnu.backend.model.Favorite;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.FavoriteRepository;
import stud.ntnu.backend.repository.ItemRepository;

/**
 * <h2>FavoriteService</h2>
 * <p>Service for managing user favorite items.</p>
 */
@Service
@RequiredArgsConstructor
public class FavoriteService {

  /**
   * <h3>Favorite Repository</h3>
   * <p>Data access component for favorites.</p>
   */
  private final FavoriteRepository favoriteRepository;

  /**
   * <h3>Item Repository</h3>
   * <p>Data access component for items.</p>
   */
  private final ItemRepository itemRepository;

  /**
   * <h3>Add Favorite</h3>
   * <p>Adds an item to user's favorites.</p>
   *
   * @param user   the user adding the favorite
   * @param itemId the ID of the item to favorite
   * @throws EntityNotFoundException if item not found
   */
  @Transactional
  public void add(User user, Long itemId) {
    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new EntityNotFoundException("Item not found with id " + itemId));
    favoriteRepository.save(Favorite.builder().user(user).item(item).build());
  }

  /**
   * <h3>Remove Favorite</h3>
   * <p>Removes an item from user's favorites.</p>
   *
   * @param userId the ID of the user
   * @param itemId the ID of the item to remove
   * @throws EntityNotFoundException if favorite relationship not found
   */
  @Transactional
  public void delete(Long userId, Long itemId) {
    favoriteRepository.delete(favoriteRepository.findByUserIdAndItemId(userId, itemId).orElseThrow(
        () -> new EntityNotFoundException(
            "Favorite not found with user id " + userId + "and item id " + itemId)));
  }

  /**
   * <h3>Get User Favorites</h3>
   * <p>Retrieves all favorite items for a user.</p>
   *
   * @param userId the ID of the user
   * @return list of {@link FavoriteResponseDto} for the user
   */
  public List<FavoriteResponseDto> getFavoritesByUserId(Long userId) {
    List<Favorite> favorites = favoriteRepository.findAllByUserId(userId);
    return favorites.stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  /**
   * <h3>Count Item Favorites</h3>
   * <p>Counts how many users have favorited an item.</p>
   *
   * @param itemId the ID of the item
   * @return total number of favorites for the item
   */
  public Long countByItemId(Long itemId) {
    return favoriteRepository.countByItemId(itemId);
  }

  /**
   * <h3>Convert to DTO</h3>
   * <p>Maps a Favorite entity to its response DTO.</p>
   *
   * @param favorite the favorite entity to convert
   * @return the mapped {@link FavoriteResponseDto}
   */
  public FavoriteResponseDto toDto(Favorite favorite) {
    return new FavoriteResponseDto(favorite.getItem().getId(), favorite.getCreatedAt());
  }
}
