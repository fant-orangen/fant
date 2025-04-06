package stud.ntnu.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import stud.ntnu.backend.exception.FileFormatException;

@Service
public class FileUploadService {

  @Value("${upload.dir:storage/uploads}")
  private String uploadDir;

  public String storeFile(MultipartFile file) {
    if (file.isEmpty()) {
      throw new FileFormatException("File is empty");
    }

    String contentType = file.getContentType();
    if (contentType == null ||
        (!contentType.equals("image/jpeg") && !contentType.equals("image/png") &&
            !contentType.equals("image/img"))) {
      throw new FileFormatException("File format not supported");
    }

    try {
      Files.createDirectories(Paths.get(uploadDir));

      String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
      Path filePath = Paths.get(uploadDir, fileName);
      Files.write(filePath, file.getBytes());

      return fileName; // Public URL, configurable
    } catch (IOException e) {
      throw new RuntimeException("File upload failed");
    }
  }
}
