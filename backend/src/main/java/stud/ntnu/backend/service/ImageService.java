package stud.ntnu.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.backend.model.Image;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.repository.ImageRepository;
import stud.ntnu.backend.repository.ItemRepository;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
public class ImageService {

  private final ImageRepository imageRepository;
  private final ItemRepository itemRepository;
  private final Cloudinary cloudinary;

  @Autowired
  public ImageService(ImageRepository imageRepository,
      ItemRepository itemRepository,
      Cloudinary cloudinary) {
    this.imageRepository = imageRepository;
    this.itemRepository = itemRepository;
    this.cloudinary = cloudinary;
  }

  @Transactional
  public void uploadImagesForItem(MultipartFile[] files, Long itemId) throws IOException {
    Optional<Item> itemOpt = itemRepository.findById(itemId);
    if (itemOpt.isEmpty()) {
      throw new IllegalArgumentException("Item with ID " + itemId + " does not exist.");
    }

    Item item = itemOpt.get();

    for (MultipartFile file : files) {
      Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
      String url = (String) uploadResult.get("secure_url");

      Image image = new Image();
      image.setUrl(url);
      image.setItem(item);

      imageRepository.save(image);
    }
  }
}