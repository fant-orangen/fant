package stud.ntnu.backend.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.data.FavoriteResponseDto;
import stud.ntnu.backend.data.MessageResponseDto;
import stud.ntnu.backend.model.Favorite;
import stud.ntnu.backend.model.Message;
import stud.ntnu.backend.repository.FavoriteRepository;

@Service
@RequiredArgsConstructor
public class FavoriteService {
  private final FavoriteRepository favoriteRepository;

  public FavoriteResponseDto save(Favorite favorite) {
    return toResponseDto(favoriteRepository.save(favorite));
  }

  public void delete(Favorite favorite) {
    favoriteRepository.delete(favorite);
  }

  public Favorite findById(long id) {
    return favoriteRepository.findById(id).orElse(null);
  }

  public List<FavoriteResponseDto> findByUserId(long userId) {
    return listToResponseDto(favoriteRepository.findAllByUserId(userId));
  }

  public List<FavoriteResponseDto> findByItemId(long itemId) {
    return listToResponseDto(favoriteRepository.findAllByItemId(itemId));
  }

  private FavoriteResponseDto toResponseDto(Favorite favorite) {
    return new FavoriteResponseDto(favorite.getId(), favorite.getUser().getId(),
        favorite.getItem().getId(), favorite.getCreatedAt());
  }

  private List<FavoriteResponseDto> listToResponseDto(List<Favorite> favorites) {
    return favorites.stream().map(this::toResponseDto).collect(Collectors.toList());
  }
}
