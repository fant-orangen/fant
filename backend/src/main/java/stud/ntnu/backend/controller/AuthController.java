package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

  /**
   * <h3>Authenticate the user</h3>
   * <p>Authenticates the user and generates a JWT token if authentication is successful.</p>
   *
   * @param request the authentication request containing username and password
   * @return a response entity containing the status and the generated JWT token
   */
  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
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