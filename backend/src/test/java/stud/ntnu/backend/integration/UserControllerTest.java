package stud.ntnu.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import stud.ntnu.backend.data.user.UserCreateDto;
import stud.ntnu.backend.data.user.UserUpdateDto;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.model.enums.Role;
import stud.ntnu.backend.repository.UserRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <h2>Integration Tests for User Controller</h2>
 * <p>This class contains integration tests for the user-related endpoints of the application.</p>
 * <p>These tests verify the functionality for retrieving user details by ID, retrieving the current user's profile, retrieving the current user's ID, and updating the current user's profile.</p>
 * <p>The tests also cover negative scenarios such as attempting to retrieve a user with an invalid ID and attempting to update the user profile with invalid data.</p>
 * <p>Finally, the tests ensure that unauthenticated users are denied access to all user-related endpoints.</p>
 * <p>The tests utilize Spring Security's {@code @WithMockUser} annotation to simulate authenticated users and {@code @WithAnonymousUser} to simulate unauthenticated users.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerTest {

  /**
   * <h3>MockMvc</h3>
   * <p>A Spring MVC Test framework client used to perform HTTP requests and assertions.</p>
   */
  @Autowired
  private MockMvc mockMvc;

  /**
   * <h3>ObjectMapper</h3>
   * <p>Jackson's ObjectMapper for converting Java objects to JSON and vice versa.</p>
   */
  @Autowired
  private ObjectMapper objectMapper;

  /**
   * <h3>UserRepository</h3>
   * <p>Spring Data JPA repository for {@link User} entities.</p>
   */
  @Autowired
  private UserRepository userRepository;

  /**
   * <h3>Test User</h3>
   * <p>A regular user entity saved in the database before each test to ensure data availability for user-specific operations.</p>
   */
  private User testUser;

  /**
   * <h3>PasswordEncoder</h3>
   * <p>Spring Security's PasswordEncoder for encoding user passwords.</p>
   */
  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * <h3>Setup Method</h3>
   * <p>This method is executed before each test case. It sets up a test user in the database with a hashed password.</p>
   */
  @BeforeEach
  void setup() {
    // Create and save a test user with an encoded password
    testUser = userRepository.save(
        User.builder().email("testuser@example.com").displayName("DisplayName").firstName("First")
            .lastName("Last").passwordHash(passwordEncoder.encode("Password123"))
            .phone("+4798765432").role(Role.USER).build());
  }

  /**
   * <h3>Test Get User By ID Endpoint (Authenticated User) - Should Return User</h3>
   * <p>Tests the {@code GET /api/users/{id}} endpoint to ensure an authenticated user can retrieve their own user details by their ID.</p>
   * <p>This test uses {@code @WithMockUser} to simulate the authenticated user making the request.</p>
   * <p>It verifies that the HTTP status code is 200 (OK) and that the returned JSON response contains the expected display name of the user.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "testuser@example.com")
  void getUserById_shouldReturnUser() throws Exception {
    // Perform the get user by ID request
    mockMvc.perform(get("/api/users/{id}", testUser.getId())).andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value("DisplayName"));
  }

  /**
   * <h3>Test Get Current User Profile Endpoint (Authenticated User) - Should Return User Profile</h3>
   * <p>Tests the {@code GET /api/users/profile} endpoint to ensure an authenticated user can retrieve their own profile information.</p>
   * <p>This test uses {@code @WithMockUser} to simulate the authenticated user making the request.</p>
   * <p>It verifies that the HTTP status code is 200 (OK) and that the returned JSON response contains the expected email address of the user.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "testuser@example.com")
  void getCurrentUser_shouldReturnUserProfile() throws Exception {
    // Perform the get current user profile request
    mockMvc.perform(get("/api/users/profile")).andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("testuser@example.com"));
  }

  /**
   * <h3>Test Get Current User ID Endpoint (Authenticated User) - Should Return User ID</h3>
   * <p>Tests the {@code GET /api/users/id} endpoint to ensure an authenticated user can retrieve their own user ID.</p>
   * <p>This test uses {@code @WithMockUser} to simulate the authenticated user making the request.</p>
   * <p>It verifies that the HTTP status code is 200 (OK) and that the returned content is the string representation of the user's ID.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "testuser@example.com")
  void getCurrentUserId_shouldReturnUserId() throws Exception {
    // Perform the get current user ID request
    mockMvc.perform(get("/api/users/id")).andExpect(status().isOk())
        .andExpect(content().string(testUser.getId().toString()));
  }

  /**
   * <h3>Test Update User Profile Endpoint (Authenticated User) - Should Update and Return User</h3>
   * <p>Tests the {@code PUT /api/users/profile} endpoint to ensure an authenticated user can successfully update their profile information.</p>
   * <p>This test uses {@code @WithMockUser} to simulate the authenticated user making the update request.</p>
   * <p>It creates a {@link UserUpdateDto} with new profile details, performs the update request, and verifies that the HTTP status code is 200 (OK) and that the returned JSON response contains the updated display name.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "testuser@example.com")
  void updateUser_shouldUpdateAndReturnUser() throws Exception {
    // Create a UserUpdateDto with the updated information
    UserUpdateDto dto =
        new UserUpdateDto("Password123", "mail@mail.com", "Password1234", "NewDisplay", "test",
            "testy", "+4712345678");

    // Perform the update user profile request
    mockMvc.perform(put("/api/users/profile").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value("NewDisplay"));
  }

  /**
   * <h3>Test Get User By ID Endpoint (Authenticated User) - Should Return 404 for Invalid ID</h3>
   * <p>Tests the {@code GET /api/users/{id}} endpoint with an invalid user ID to ensure the server returns a 404 (Not Found) status code.</p>
   * <p>This test uses {@code @WithMockUser} to simulate an authenticated user making the request for a non-existent user.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "testuser@example.com")
  void getUserById_shouldReturn404ForInvalidId() throws Exception {
    // Perform the get user by ID request with an invalid ID
    mockMvc.perform(get("/api/users/{id}", 999999)).andExpect(status().isNotFound());
  }

  /**
   * <h3>Test Update User Profile Endpoint (Authenticated User) - Should Fail Validation for Missing Fields</h3>
   * <p>Tests the {@code PUT /api/users/profile} endpoint with a {@link UserCreateDto} containing missing or invalid fields to ensure the server returns a 400 (Bad Request) status code due to validation failure.</p>
   * <p>This test uses {@code @WithMockUser} to simulate an authenticated user attempting to update their profile with invalid data.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithMockUser(username = "testuser@example.com")
  void updateUser_shouldFailValidation_whenMissingFields() throws Exception {
    // Create a UserCreateDto with missing or invalid fields
    UserCreateDto dto = new UserCreateDto("", "", "", "", "", "notaphone");

    // Perform the update user profile request with invalid data
    mockMvc.perform(put("/api/users/profile").with(csrf()).contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
  }

  /**
   * <h3>Test All User Endpoints - Should Return 403 Forbidden for Unauthenticated Users</h3>
   * <p>Tests various user-related endpoints to ensure that unauthenticated users (anonymous users) are denied access with a 403 (Forbidden) status code.</p>
   * <p>This test uses {@code @WithAnonymousUser} to simulate an unauthenticated user attempting to access these endpoints.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  @WithAnonymousUser
  void allEndpoints_shouldReturn403_whenUnauthenticated() throws Exception {
    // Attempt to get user by ID as an anonymous user
    mockMvc.perform(get("/api/users/" + testUser.getId())).andExpect(status().isForbidden());

    // Attempt to get current user profile as an anonymous user
    mockMvc.perform(get("/api/users/profile")).andExpect(status().isForbidden());

    // Attempt to get current user ID as an anonymous user
    mockMvc.perform(get("/api/users/id")).andExpect(status().isForbidden());

    // Attempt to update user profile as an anonymous user
    UserCreateDto dto =
        new UserCreateDto("mail@mail.com", "password", "c", "d", "e", "+4711111111");
    mockMvc.perform(put("/api/users/profile").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto))).andExpect(status().isForbidden());
  }
}