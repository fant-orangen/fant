package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.AuthRequestDto;
import stud.ntnu.backend.data.AuthResponseDto;
import stud.ntnu.backend.data.UserRegistrationDto;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.service.UserService;
import stud.ntnu.backend.util.JwtUtil;

/**
 * <h2>AuthController</h2>
 * <p>Controller for handling authentication requests.</p>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Operations related to user authentication")
public class AuthController {

  /**
   * <h3>Authentication Manager</h3>
   * <p>Manages authentication processes.</p>
   */
  private final AuthenticationManager authenticationManager;

  /**
   * <h3>JWT Utility</h3>
   * <p>Utility class for generating and validating JWT tokens.</p>
   */
  private final JwtUtil jwtUtil;

  private final UserService userService;

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  /**
   * <h3>Generate JWT Token</h3>

   * <p>Authenticates the user and generates a JWT token if authentication is successful.</p>
   *
   * @param request the authentication request containing username and password
   * @return a response entity containing the status and the generated JWT token
   */
  @Operation(summary = "Authenticate user",
      description = "Authenticates a user with username and password and returns a JWT token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authentication successful",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = AuthResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "Invalid credentials",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = AuthResponseDto.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = AuthResponseDto.class)))})
  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@Valid @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "Authentication request with username and password",
      required = true,
      content = @Content(schema = @Schema(implementation = AuthRequestDto.class))
  ) AuthRequestDto request) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              request.getEmail(),
              request.getPassword()
          )
      );

      return ResponseEntity.ok(new AuthResponseDto(jwtUtil.generateToken(request.getEmail())));

    } catch (BadCredentialsException e) {
      return ResponseEntity.status(401)
          .body(new AuthResponseDto(null));
    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body(new AuthResponseDto(null));
    }
  }
  @PostMapping("/register")
  public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody UserRegistrationDto request) {
    return ResponseEntity.ok(
        new AuthResponseDto(jwtUtil.generateToken(userService.createUser(request).getEmail())));
  }
}