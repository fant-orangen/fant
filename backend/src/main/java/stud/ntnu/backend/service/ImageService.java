package stud.ntnu.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.backend.exception.FileFormatException;
import stud.ntnu.backend.model.Image;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.repository.ImageRepository;
import stud.ntnu.backend.repository.ItemRepository;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * <h2>ImageService</h2>
 * <p>Service responsible for managing image operations in the marketplace application.</p>
 *
 * <p>This service handles the following operations:</p>
 * <ul>
 *   <li>Uploading images to Cloudinary cloud storage</li>
 *   <li>Associating uploaded images with marketplace items</li>
 *   <li>Persisting image metadata in the database</li>
 * </ul>
 *
 * <p>The service integrates with Cloudinary for cloud storage of images
 * and maintains references to these images in the application's database.</p>
 */
@Service
public class ImageService {

  /**
   * <h3>Image Repository</h3>
   * <p>Repository for image data operations.</p>
   */
  private final ImageRepository imageRepository;

  /**
   * <h3>Item Repository</h3>
   * <p>Repository for accessing item data.</p>
   */
  private final ItemRepository itemRepository;

  /**
   * <h3>Cloudinary Client</h3>
   * <p>Client for interacting with the Cloudinary API.</p>
   */
  private final Cloudinary cloudinary;

  /**
   * <h3>Constructor</h3>
   * <p>Initializes the ImageService with required dependencies.</p>
   *
   * @param imageRepository Repository for image data operations
   * @param itemRepository  Repository for item data operations
   * @param cloudinary      Client for Cloudinary API interactions
   */
  @Autowired
  public ImageService(ImageRepository imageRepository,
                      ItemRepository itemRepository,
                      Cloudinary cloudinary) {
    this.imageRepository = imageRepository;
    this.itemRepository = itemRepository;
    this.cloudinary = cloudinary;
  }

  /**
   * <h3>Upload Images For Item</h3>
   * <p>Uploads multiple images to Cloudinary and associates them with a specified item.</p>
   *
   * <p>This method performs the following operations:</p>
   * <ol>
   *   <li>Verifies the existence of the specified item</li>
   *   <li>Uploads each image file to Cloudinary</li>
   *   <li>Creates database records linking the uploaded images to the item</li>
   * </ol>
   *
   * <p>The method uses a transaction to ensure database consistency, rolling back
   * if any part of the process fails.</p>
   *
   * @param files  Array of image files to be uploaded
   * @param itemId ID of the item to associate with the images
   * @throws IOException              If there is an error during file upload or processing
   * @throws IllegalArgumentException If the specified item does not exist
   */
  @Transactional
  public void uploadImagesForItem(MultipartFile[] files, Long itemId) throws IOException {
    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));

    for (MultipartFile file : files) {
      String contentType = file.getContentType();
      if (contentType == null ||
          (!contentType.equals("image/jpeg") && !contentType.equals("image/png") &&
              !contentType.equals("image/img"))) {
        throw new FileFormatException("File format not supported");
      }
    }

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