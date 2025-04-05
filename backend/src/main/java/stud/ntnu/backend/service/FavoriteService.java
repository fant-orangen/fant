package stud.ntnu.backend.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.data.FavoriteResponseDto;
import stud.ntnu.backend.data.ItemPreviewDto;
import stud.ntnu.backend.model.Favorite;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.FavoriteRepository;
import stud.ntnu.backend.repository.ItemRepository;
import stud.ntnu.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class FavoriteService {
  private final FavoriteRepository favoriteRepository;
  private final UserRepository userRepository;
  private final ItemRepository itemRepository;
  private final ModelMapper modelMapper;

  public void add(Long userId, Long itemId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));

    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new EntityNotFoundException("Item not found with id " + itemId));

    favoriteRepository.save(Favorite.builder().user(user).item(item).build());
  }

  public void delete(Long userId, Long itemId) {
    favoriteRepository.delete(favoriteRepository.findByUserIdAndItemId(userId, itemId).orElseThrow(
        () -> new EntityNotFoundException(
            "Favorite not found with user id " + userId + "and item id " + itemId)));
  }

  public List<FavoriteResponseDto> getFavoritesByUserId(Long userId) {
    List<Favorite> favorites = favoriteRepository.findAllByUserId(userId);
    return favorites.stream()
        .map(favorite -> modelMapper.map(favorite.getItem(), FavoriteResponseDto.class))
        .collect(Collectors.toList());
  }

  public Long countByItemId(Long itemId) {
    return favoriteRepository.countByItemId(itemId);
  }
}
