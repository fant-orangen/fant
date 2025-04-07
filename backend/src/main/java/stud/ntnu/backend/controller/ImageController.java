package stud.ntnu.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.backend.service.ImageService;

/**
 * <h2>ImageController</h2>
 * <p>REST controller for image upload operations in the marketplace application.</p>
 * <p>This controller handles endpoints related to image processing, specifically allowing
 * users to upload images for marketplace items using Cloudinary as the storage service.</p>
 *
 * <h3>Endpoints:</h3>
 * <ul>
 *   <li>{@code POST /api/images/add} - Upload one or more images for a specific item</li>
 * </ul>
 *
 * <h3>Dependencies:</h3>
 * <ul>
 *   <li>{@link ImageService} - Service that handles the business logic for image operations</li>
 *   <li>{@link stud.ntnu.backend.config.CloudinaryConfig} - Configuration for Cloudinary integration</li>
 * </ul>
 *
 * @see stud.ntnu.backend.service.ImageService
 * @see stud.ntnu.backend.config.CloudinaryConfig
 */
@RestController
@RequestMapping("/api/images")
public class ImageController {

  /**
   * <h3>Image Service</h3>
   * <p>Service handling image upload operations and Cloudinary integration.</p>
   */
  private final ImageService imageService;

  /**
   * <h3>Constructor</h3>
   * <p>Initializes the controller with required dependencies.</p>
   *
   * @param imageService the service for image operations
   */
  @Autowired
  public ImageController(ImageService imageService) {
    this.imageService = imageService;
  }

  /**
   * <h3>Upload Images</h3>
   * <p>Endpoint for uploading one or more image files associated with a marketplace item.</p>
   * <p>Images are stored in Cloudinary cloud storage and their URLs are saved in the database.</p>
   *
   * @param files  array of image files to upload
   * @param itemId ID of the item to associate the images with
   * @return {@link ResponseEntity} containing success message or error details
   * @throws IllegalArgumentException if the specified item doesn't exist
   */
  @PostMapping("/add")
  public ResponseEntity<String> uploadImages(
      @RequestParam("files") MultipartFile[] files,
      @RequestParam("itemId") Long itemId) {
    try {
      imageService.uploadImagesForItem(files, itemId);
      return ResponseEntity.ok("Images uploaded successfully");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body("Failed to upload images: " + e.getMessage());
    }
  }
}