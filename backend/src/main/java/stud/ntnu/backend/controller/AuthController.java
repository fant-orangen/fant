package stud.ntnu.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.auth.AuthRequestDto;
import stud.ntnu.backend.data.auth.AuthResponseDto;
import stud.ntnu.backend.data.user.UserCreateDto;
import stud.ntnu.backend.service.UserService;
import stud.ntnu.backend.util.JwtUtil;

/**
 * <h2>AuthController</h2>
 * <p>Controller for handling user authentication and registration.</p>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Operations related to user authentication")
public class AuthController {

  /**
   * <h3>Authentication Manager</h3>
   * <p>Handles user authentication processes.</p>
   *
   * @see AuthenticationManager
   */
  private final AuthenticationManager authenticationManager;

  /**
   * <h3>JWT Utility</h3>
   * <p>Handles JWT token generation and validation.</p>
   *
   * @see JwtUtil
   */
  private final JwtUtil jwtUtil;

  /**
   * <h3>User Service</h3>
   * <p>Service for user management operations.</p>
   *
   * @see UserService
   */
  private final UserService userService;

  /**
   * <h3>User Login</h3>
   * <p>Authenticates user and generates JWT token.</p>
   *
   * @param request the authentication request
   * @return {@link AuthResponseDto} containing JWT token
   */
  @Operation(summary = "Authenticate user", description = "Authenticates user credentials and returns JWT token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authentication successful", content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))})
  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody
                                               @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Authentication credentials", required = true, content = @Content(schema = @Schema(implementation = AuthRequestDto.class)))
                                               AuthRequestDto request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    return ResponseEntity.ok(new AuthResponseDto(jwtUtil.generateToken(request.getEmail())));
  }

  /**
   * <h3>User Registration</h3>
   * <p>Creates new user account and returns JWT token.</p>
   *
   * @param request the user registration data
   * @return {@link AuthResponseDto} containing JWT token with HTTP status 201 (Created)
   */
  @Operation(summary = "Register user", description = "Creates new user account and returns JWT token")
  @ApiResponse(responseCode = "201", description = "Registration successful", content = @Content(schema = @Schema(implementation = AuthResponseDto.class)))
  @ApiResponse(responseCode = "400", description = "Invalid registration data", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @PostMapping("/register")
  public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody
                                                  @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User registration details", required = true, content = @Content(schema = @Schema(implementation = UserCreateDto.class)))
                                                  UserCreateDto request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
        new AuthResponseDto(jwtUtil.generateToken(userService.createUser(request).getEmail())));
  }
}