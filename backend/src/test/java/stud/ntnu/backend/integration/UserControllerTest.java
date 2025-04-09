package stud.ntnu.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import stud.ntnu.backend.data.user.UserCreateDto;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.model.enums.Role;
import stud.ntnu.backend.repository.UserRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private UserRepository userRepository;

  private User testUser;

  @BeforeEach
  void setup() {
    testUser = userRepository.save(User.builder()
        .email("testuser@example.com")
        .displayName("DisplayName")
        .firstName("First")
        .lastName("Last")
        .passwordHash("secure")
        .phone("+4798765432")
        .role(Role.USER)
        .build());
  }

  // ─────────────────────────────────────────────────────────────
  // ✅ POSITIVE TESTS
  // ─────────────────────────────────────────────────────────────

  @Test
  @WithMockUser(username = "testuser@example.com")
  void getUserById_shouldReturnUser() throws Exception {
    mockMvc.perform(get("/api/users/{id}", testUser.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value("DisplayName"));
  }

  @Test
  @WithMockUser(username = "testuser@example.com")
  void getCurrentUser_shouldReturnUserProfile() throws Exception {
    mockMvc.perform(get("/api/users/profile"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("testuser@example.com"));
  }

  @Test
  @WithMockUser(username = "testuser@example.com")
  void getCurrentUserId_shouldReturnUserId() throws Exception {
    mockMvc.perform(get("/api/users/id"))
        .andExpect(status().isOk())
        .andExpect(content().string(testUser.getId().toString()));
  }

  @Test
  @WithMockUser(username = "testuser@example.com")
  void updateUser_shouldUpdateAndReturnUser() throws Exception {
    UserCreateDto dto =
        new UserCreateDto("mail@mail.com", "password", "NewDisplay", "test", "testy",
            "+4712345678");

    mockMvc.perform(put("/api/users/profile")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value("NewDisplay"));
  }

  // ─────────────────────────────────────────────────────────────
  // 🚫 NEGATIVE TESTS
  // ─────────────────────────────────────────────────────────────

  @Test
  @WithMockUser(username = "testuser@example.com")
  void getUserById_shouldReturn404ForInvalidId() throws Exception {
    mockMvc.perform(get("/api/users/{id}", 999999))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "testuser@example.com")
  void updateUser_shouldFailValidation_whenMissingFields() throws Exception {
    UserCreateDto dto = new UserCreateDto("", "", "", "", "", "notaphone");

    mockMvc.perform(put("/api/users/profile")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest());
  }

  // ─────────────────────────────────────────────────────────────
  // 🔐 UNAUTHORIZED ACCESS
  // ─────────────────────────────────────────────────────────────

  @Test
  @WithAnonymousUser
  void allEndpoints_shouldReturn401_whenUnauthenticated() throws Exception {
    mockMvc.perform(get("/api/users/" + testUser.getId()))
        .andExpect(status().isForbidden());

    mockMvc.perform(get("/api/users/profile"))
        .andExpect(status().isForbidden());

    mockMvc.perform(get("/api/users/id"))
        .andExpect(status().isForbidden());

    UserCreateDto dto =
        new UserCreateDto("mail@mail.com", "password", "c", "d", "e", "+4711111111");
    mockMvc.perform(put("/api/users/profile")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isForbidden());
  }
}
