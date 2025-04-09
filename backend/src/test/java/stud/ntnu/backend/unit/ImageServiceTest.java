package stud.ntnu.backend.unit;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.backend.model.Image;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.repository.ImageRepository;
import stud.ntnu.backend.repository.ItemRepository;
import stud.ntnu.backend.service.ImageService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

  @Mock
  private ImageRepository imageRepository;

  @Mock
  private ItemRepository itemRepository;

  @Mock
  private Cloudinary cloudinary;

  @Mock
  private Uploader uploader;

  @InjectMocks
  private ImageService imageService;

  private Item testItem;
  private MultipartFile[] testFiles;
  private Map<String, Object> uploadResult;

  @BeforeEach
  void setUp() {
    // Set up test item
    testItem = new Item();
    testItem.setId(1L);

    // Set up test files
    MockMultipartFile file1 = new MockMultipartFile(
        "file1", "test1.jpg", "image/jpeg", "test image content 1".getBytes());
    MockMultipartFile file2 = new MockMultipartFile(
        "file2", "test2.jpg", "image/jpeg", "test image content 2".getBytes());
    testFiles = new MultipartFile[]{file1, file2};

    // Set up cloudinary upload result
    uploadResult = new HashMap<>();
    uploadResult.put("secure_url", "https://cloudinary.com/test-image.jpg");

    // We'll move the cloudinary.uploader() stubbing to specific test methods
  }

  @Test
  void uploadImagesForItem_Success() throws IOException {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
    when(cloudinary.uploader()).thenReturn(uploader);
    when(uploader.upload(any(byte[].class), any())).thenReturn(uploadResult);

    // Act
    imageService.uploadImagesForItem(testFiles, 1L);

    // Assert
    verify(itemRepository).findById(1L);
    verify(uploader, times(testFiles.length)).upload(any(byte[].class), any());
    verify(imageRepository, times(testFiles.length)).save(any(Image.class));
  }

  @Test
  void uploadImagesForItem_ItemNotFound() throws IOException {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      imageService.uploadImagesForItem(testFiles, 999L);
    });

    verify(itemRepository).findById(999L);
    verify(uploader, never()).upload(any(byte[].class), any());
    verify(imageRepository, never()).save(any(Image.class));
  }

  @Test
  void uploadImagesForItem_CloudinaryError() throws IOException {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
    when(cloudinary.uploader()).thenReturn(uploader);
    when(uploader.upload(any(byte[].class), any())).thenThrow(new IOException("Upload failed"));

    // Act & Assert
    assertThrows(IOException.class, () -> {
      imageService.uploadImagesForItem(testFiles, 1L);
    });

    verify(itemRepository).findById(1L);
    verify(uploader).upload(any(byte[].class), any());
    verify(imageRepository, never()).save(any(Image.class));
  }

  @Test
  void uploadImagesForItem_EmptyFilesArray() throws IOException {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
    MultipartFile[] emptyFiles = new MultipartFile[0];

    // Act
    imageService.uploadImagesForItem(emptyFiles, 1L);

    // Assert
    verify(itemRepository).findById(1L);
    verify(uploader, never()).upload(any(byte[].class), any());
    verify(imageRepository, never()).save(any(Image.class));
  }

  @Test
  void uploadImagesForItem_VerifyImageData() throws IOException {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
    when(cloudinary.uploader()).thenReturn(uploader);
    when(uploader.upload(any(byte[].class), any())).thenReturn(uploadResult);

    // Set up capture for the Image object being saved
    doAnswer(invocation -> {
      Image savedImage = invocation.getArgument(0);

      // Verify the image has the correct URL and is associated with the correct item
      assert savedImage.getUrl().equals("https://cloudinary.com/test-image.jpg");
      assert savedImage.getItem() == testItem;

      return savedImage;
    }).when(imageRepository).save(any(Image.class));

    // Act
    imageService.uploadImagesForItem(testFiles, 1L);

    // Assert
    verify(imageRepository, times(testFiles.length)).save(any(Image.class));
  }
}