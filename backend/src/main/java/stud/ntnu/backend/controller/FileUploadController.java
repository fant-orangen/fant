package stud.ntnu.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.backend.service.FileUploadService;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class FileUploadController {

  private final FileUploadService fileUploadService;

  @PostMapping
  public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
      return ResponseEntity.ok(fileUploadService.storeFile(file));
    }
}
