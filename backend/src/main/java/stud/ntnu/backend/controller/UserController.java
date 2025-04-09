package stud.ntnu.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.user.UserCreateDto;
import stud.ntnu.backend.data.user.UserResponseDto;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.service.UserService;

/**
 * <h2>UserController</h2>
 * <p>Controller for user profile and account operations.</p>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Operations related to user management")
public class UserController {

  /**
   * <h3>User Service</h3>
   * <p>Service handling user account operations.</p>
   *
   * @see UserService
   */
  private final UserService userService;

  /**
   * <h3>Get User By ID</h3>
   * <p>Retrieves user details by user ID.</p>
   *
   * @param id the user ID
   * @return {@link ResponseEntity} containing {@link UserResponseDto} with user details
   */
  @GetMapping("/{id}")
  @Operation(summary = "Get User by ID", description = "Retrieves user details based on the provided user ID.")
  @ApiResponse(responseCode = "200", description = "User details retrieved successfully", content = @Content(schema = @Schema(implementation = UserResponseDto.class)))
  @ApiResponse(responseCode = "400", description = "Invalid user ID", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class)))
  @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<UserResponseDto> getUser(
      @Parameter(description = "ID of the user to retrieve", required = true) @Positive
      @PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  /**
   * <h3>Update User Profile</h3>
   * <p>Updates the current user's profile information.</p>
   *
   * @param userCreateDto the updated user data
   * @param principal     the authenticated user
   * @return {@link ResponseEntity} containing the updated {@link User} entity
   */
  @PutMapping("/profile")
  @Operation(summary = "Update User Profile", description = "Updates the profile information of the currently authenticated user.")
  @ApiResponse(responseCode = "200", description = "User profile updated successfully", content = @Content(schema = @Schema(implementation = User.class)))
  @ApiResponse(responseCode = "400", description = "Invalid user details", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<User> updateUser(@Valid @RequestBody
                                         @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated user details", required = true, content = @Content(schema = @Schema(implementation = UserCreateDto.class)))
                                         UserCreateDto userCreateDto,
                                         @Parameter(hidden = true) Principal principal) {
    return ResponseEntity.ok(
        userService.updateUser(userCreateDto, userService.getCurrentUserId(principal)));
  }

  /**
   * <h3>Get Current User</h3>
   * <p>Retrieves details of the currently authenticated user.</p>
   *
   * @param principal the authenticated user
   * @return {@link ResponseEntity} containing the current {@link User} entity
   */
  @GetMapping("/profile")
  @Operation(summary = "Get Current User Profile", description = "Retrieves the profile information of the currently authenticated user.")
  @ApiResponse(responseCode = "200", description = "Current user profile retrieved successfully", content = @Content(schema = @Schema(implementation = User.class)))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<User> getCurrentUser(@Parameter(hidden = true) Principal principal) {
    return ResponseEntity.ok(userService.getCurrentUser(principal));
  }

  /**
   * <h3>Get Current User ID</h3>
   * <p>Retrieves the ID of the currently authenticated user.</p>
   *
   * @param principal the authenticated user
   * @return {@link ResponseEntity} containing the ID of the current user
   */
  @GetMapping("/id")
  @Operation(summary = "Get Current User ID", description = "Retrieves the ID of the currently authenticated user.")
  @ApiResponse(responseCode = "200", description = "Current user ID retrieved successfully", content = @Content(schema = @Schema(implementation = Long.class)))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Long> getCurrentUserId(@Parameter(hidden = true) Principal principal) {
    return ResponseEntity.ok(userService.getCurrentUserId(principal));
  }
}