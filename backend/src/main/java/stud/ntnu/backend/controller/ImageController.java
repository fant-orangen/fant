package stud.ntnu.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.backend.service.ImageService;

@RestController
@RequestMapping("/api/images")
public class ImageController {

  private final ImageService imageService;

  @Autowired
  public ImageController(ImageService imageService) {
    this.imageService = imageService;
  }

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