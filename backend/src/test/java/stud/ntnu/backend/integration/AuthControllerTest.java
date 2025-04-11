package stud.ntnu.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.data.auth.AuthRequestDto;
import stud.ntnu.backend.data.user.UserCreateDto;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.model.enums.Role;
import stud.ntnu.backend.repository.UserRepository;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <h2>Integration Tests for Authentication Controller</h2>
 * <p>This class contains integration tests for the authentication endpoints of the application.</p>
 * <p>These tests verify the user registration and login functionalities, ensuring that users can successfully create accounts and obtain JWT tokens upon successful authentication.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerTest {

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
   * <h3>PasswordEncoder</h3>
   * <p>Spring Security's PasswordEncoder for encoding user passwords.</p>
   */
  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * <h3>Existing Test User</h3>
   * <p>A user entity pre-saved in the database for testing login functionality.</p>
   */
  private User existingUser;

  /**
   * <h3>Setup Method</h3>
   * <p>This method is executed before each test case. It sets up the necessary test data, including saving a user to the database for login tests.</p>
   */
  @BeforeEach
  public void setup() {
    // Save a user for login tests
    existingUser =
        User.builder().email("loginuser@example.com").displayName("Login User").firstName("Login")
            .lastName("User").passwordHash(passwordEncoder.encode("securePass123"))
            .phone("+4711223344").role(Role.USER).build();
    userRepository.save(existingUser);
  }

  /**
   * <h3>Test User Registration Endpoint - Expects JWT Token on Success</h3>
   * <p>Tests the {@code POST /auth/register} endpoint to ensure a new user can successfully register and receive a JWT token.</p>
   * <p>The test verifies that the HTTP status code is 201 (Created) and that the response contains a non-empty 'token' field.</p>
   * <p>It also checks if the new user is persisted in the database.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  public void testRegister_ShouldReturnJwtToken() throws Exception {
    // Create registration DTO
    UserCreateDto registerDto = new UserCreateDto();
    registerDto.setEmail("newuser@example.com");
    registerDto.setPassword("Password123");
    registerDto.setDisplayName("New User");
    registerDto.setFirstName("New");
    registerDto.setLastName("User");
    registerDto.setPhone("+4798765432");

    // Perform registration request
    mockMvc.perform(post("/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerDto))).andExpect(status().isCreated())
        .andExpect(jsonPath("$.token").isNotEmpty());

    // Verify the user is saved
    assertTrue(userRepository.existsByEmail("newuser@example.com"));
  }

  /**
   * <h3>Test User Login Endpoint - Expects JWT Token for Valid Credentials</h3>
   * <p>Tests the {@code POST /auth/login} endpoint with valid user credentials to ensure a JWT token is returned upon successful authentication.</p>
   * <p>The test verifies that the HTTP status code is 200 (OK) and that the response contains a non-empty 'token' field.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  public void testLogin_ShouldReturnJwtToken_WhenValidCredentials() throws Exception {
    // Create login DTO with valid credentials
    AuthRequestDto loginDto = new AuthRequestDto();
    loginDto.setEmail("loginuser@example.com");
    loginDto.setPassword("securePass123");

    // Perform login request
    mockMvc.perform(post("/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDto))).andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isNotEmpty());
  }

  /**
   * <h3>Test User Login Endpoint - Expects Unauthorized for Invalid Credentials</h3>
   * <p>Tests the {@code POST /auth/login} endpoint with invalid user credentials to ensure an unauthorized (401) status code is returned.</p>
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  public void testLogin_ShouldReturnUnauthorized_WhenInvalidCredentials() throws Exception {
    // Create login DTO with invalid password
    AuthRequestDto loginDto = new AuthRequestDto();
    loginDto.setEmail("loginuser@example.com");
    loginDto.setPassword("wrongPassword");

    // Perform login request
    mockMvc.perform(post("/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginDto))).andExpect(status().isUnauthorized());
  }
}