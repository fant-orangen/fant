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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private User existingUser;

  @BeforeEach
  public void setup() {
    // Save a user for login tests
    existingUser = User.builder()
        .email("loginuser@example.com")
        .displayName("Login User")
        .firstName("Login")
        .lastName("User")
        .passwordHash(passwordEncoder.encode("securePass123"))
        .phone("+4711223344")
        .role(Role.USER)
        .build();
    userRepository.save(existingUser);
  }

  @Test
  public void testRegister_ShouldReturnJwtToken() throws Exception {
    UserCreateDto registerDto = new UserCreateDto();
    registerDto.setEmail("newuser@example.com");
    registerDto.setPassword("newPassword");
    registerDto.setDisplayName("New User");
    registerDto.setFirstName("New");
    registerDto.setLastName("User");
    registerDto.setPhone("+4798765432");

    mockMvc.perform(post("/auth/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isNotEmpty());

    // Verify the user is saved
    assertTrue(userRepository.existsByEmail("newuser@example.com"));
  }

  @Test
  public void testLogin_ShouldReturnJwtToken_WhenValidCredentials() throws Exception {
    AuthRequestDto loginDto = new AuthRequestDto();
    loginDto.setEmail("loginuser@example.com");
    loginDto.setPassword("securePass123");

    mockMvc.perform(post("/auth/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isNotEmpty());
  }

  @Test
  public void testLogin_ShouldReturnUnauthorized_WhenInvalidCredentials() throws Exception {
    AuthRequestDto loginDto = new AuthRequestDto();
    loginDto.setEmail("loginuser@example.com");
    loginDto.setPassword("wrongPassword");

    mockMvc.perform(post("/auth/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDto)))
        .andExpect(status().isUnauthorized());
  }
}
