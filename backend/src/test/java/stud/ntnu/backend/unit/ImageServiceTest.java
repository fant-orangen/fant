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

/**
 * <h2>Unit Tests for Image Service</h2>
 * <p>This class contains unit tests for the {@link ImageService} class, focusing on the business logic for uploading images and associating them with items.</p>
 * <p>The tests utilize Mockito to mock the dependencies of the {@code ImageService}, such as {@link ImageRepository}, {@link ItemRepository}, and Cloudinary's API clients, allowing for isolated testing of the service's methods.</p>
 */
@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

  /**
   * <h3>Mock ImageRepository</h3>
   * <p>A Mockito mock for the {@link ImageRepository} dependency, used to simulate database interactions related to images.</p>
   */
  @Mock
  private ImageRepository imageRepository;

  /**
   * <h3>Mock ItemRepository</h3>
   * <p>A Mockito mock for the {@link ItemRepository} dependency, used to simulate database interactions related to items.</p>
   */
  @Mock
  private ItemRepository itemRepository;

  /**
   * <h3>Mock Cloudinary</h3>
   * <p>A Mockito mock for the {@link Cloudinary} dependency, used to simulate interactions with the Cloudinary service.</p>
   */
  @Mock
  private Cloudinary cloudinary;

  /**
   * <h3>Mock Uploader</h3>
   * <p>A Mockito mock for Cloudinary's {@link Uploader}, used to simulate image upload operations.</p>
   */
  @Mock
  private Uploader uploader;

  /**
   * <h3>InjectMocks ImageService</h3>
   * <p>The instance of {@link ImageService} being tested, with its dependencies injected via Mockito.</p>
   */
  @InjectMocks
  private ImageService imageService;

  /**
   * <h3>Test Item</h3>
   * <p>An {@link Item} entity used for testing image uploads and associations.</p>
   */
  private Item testItem;

  /**
   * <h3>Test MultipartFiles Array</h3>
   * <p>An array of {@link MultipartFile} used to simulate uploaded image files.</p>
   */
  private MultipartFile[] testFiles;

  /**
   * <h3>Cloudinary Upload Result</h3>
   * <p>A {@link Map} simulating the response from Cloudinary after a successful image upload.</p>
   */
  private Map<String, Object> uploadResult;

  /**
   * <h3>Setup Method</h3>
   * <p>This method is executed before each test case. It initializes a test item, an array of mock multipart files, and a mock Cloudinary upload result.</p>
   */
  @BeforeEach
  void setUp() {
    // Set up test item
    testItem = new Item();
    testItem.setId(1L);

    // Set up test files
    MockMultipartFile file1 = new MockMultipartFile("file1", "test1.jpg", "image/jpeg",
        "test image content 1".getBytes());
    MockMultipartFile file2 = new MockMultipartFile("file2", "test2.jpg", "image/jpeg",
        "test image content 2".getBytes());
    testFiles = new MultipartFile[] {file1, file2};

    // Set up cloudinary upload result
    uploadResult = new HashMap<>();
    uploadResult.put("secure_url", "https://cloudinary.com/test-image.jpg");

    // We'll move the cloudinary.uploader() stubbing to specific test methods
  }

  /**
   * <h3>Test Upload Images For Item - Success</h3>
   * <p>Tests the successful upload of images for an existing item. It verifies that the item is found, Cloudinary's uploader is used, images are uploaded, and corresponding {@link Image} entities are saved to the repository.</p>
   *
   * @throws IOException if an I/O error occurs during the test.
   */
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

  /**
   * <h3>Test Upload Images For Item - Item Not Found</h3>
   * <p>Tests the scenario where an attempt is made to upload images for a non-existent item. It verifies that an {@link IllegalArgumentException} is thrown.</p>
   *
   * @throws IOException if an I/O error occurs during the test.
   */
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

  /**
   * <h3>Test Upload Images For Item - Cloudinary Error</h3>
   * <p>Tests the scenario where the image upload to Cloudinary fails. It verifies that an {@link IOException} is thrown and no images are saved to the repository.</p>
   *
   * @throws IOException if an I/O error occurs during the test.
   */
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

  /**
   * <h3>Test Upload Images For Item - Empty Files Array</h3>
   * <p>Tests the scenario where an empty array of files is provided for upload. It verifies that no upload or save operations are performed.</p>
   *
   * @throws IOException if an I/O error occurs during the test.
   */
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

  /**
   * <h3>Test Upload Images For Item - Verify Image Data</h3>
   * <p>Tests the successful upload of images and verifies that the saved {@link Image} entities have the correct URL from Cloudinary and are associated with the correct {@link Item}.</p>
   *
   * @throws IOException if an I/O error occurs during the test.
   */
  @Test
  void uploadImagesForItem_VerifyImageData() throws IOException {
    // Arrange
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
    when(cloudinary.uploader()).thenReturn(uploader);
    when(uploader.upload(any(byte[].class), any())).thenReturn(uploadResult);

    // Set up capture for the Image object being saved and perform assertions on it
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