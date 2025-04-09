package stud.ntnu.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
 * <li>{@code POST /api/images/add} - Upload one or more images for a specific item</li>
 * <li>{@code DELETE /api/images/delete} - Delete a specific image associated with an item</li>
 * </ul>
 *
 * <h3>Dependencies:</h3>
 * <ul>
 * <li>{@link ImageService} - Service that handles the business logic for image operations</li>
 * <li>{@link stud.ntnu.backend.config.CloudinaryConfig} - Configuration for Cloudinary integration</li>
 * </ul>
 *
 * @see stud.ntnu.backend.service.ImageService
 * @see stud.ntnu.backend.config.CloudinaryConfig
 */
@RestController
@RequestMapping("/api/images")
@Tag(name = "Images", description = "Operations for uploading and deleting images for items.")
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
   * @return {@link ResponseEntity} containing a success message if images are uploaded, or an error message otherwise
   * @throws IllegalArgumentException if the specified item doesn't exist
   */
  @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Upload Images for Item", description = "Uploads one or more images for a specific item.")
  @ApiResponse(responseCode = "200", description = "Images uploaded successfully", content = @Content(mediaType = "text/plain"))
  @ApiResponse(responseCode = "400", description = "Invalid request: Item ID is missing or invalid.")
  @ApiResponse(responseCode = "404", description = "Item not found.")
  @ApiResponse(responseCode = "500", description = "Failed to upload images due to server error.")
  public ResponseEntity<String> uploadImages(
      @Parameter(description = "Array of image files to upload", required = true)
      @RequestParam("files") MultipartFile[] files,
      @Parameter(description = "ID of the item to associate the images with", required = true)
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

  /**
   * <h3>Delete Image</h3>
   * <p>Endpoint for deleting a specific image associated with a marketplace item.</p>
   * <p>Removes the image reference from the database based on the item ID and image URL.</p>
   *
   * @param itemId   ID of the item the image is associated with
   * @param imageUrl URL of the image to delete
   * @return {@link ResponseEntity} containing a success message if the image is deleted, or an error message otherwise
   */
  @DeleteMapping("/delete")
  @Operation(summary = "Delete Image for Item", description = "Deletes a specific image associated with an item using its URL.")
  @ApiResponse(responseCode = "200", description = "Image deleted successfully", content = @Content(mediaType = "text/plain"))
  @ApiResponse(responseCode = "400", description = "Invalid request: Item ID or Image URL is missing or invalid.")
  @ApiResponse(responseCode = "404", description = "Item or image not found.")
  @ApiResponse(responseCode = "500", description = "Failed to delete image due to server error.")
  public ResponseEntity<String> deleteImage(
      @Parameter(description = "ID of the item the image belongs to", required = true)
      @RequestParam("itemId") Long itemId,
      @Parameter(description = "URL of the image to delete", required = true)
      @RequestParam("imageUrl") String imageUrl) {
    try {
      imageService.deleteImageForItem(itemId, imageUrl);
      return ResponseEntity.ok("Image deleted successfully");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Failed to delete image: " + e.getMessage());
    }
  }
}