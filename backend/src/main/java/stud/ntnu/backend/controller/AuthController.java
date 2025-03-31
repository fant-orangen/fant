package stud.ntnu.backend.controller;

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
import stud.ntnu.backend.util.JwtUtil;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/token")
    public ResponseEntity<AuthResponseDto> getToken(@RequestBody AuthRequestDto request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            String token = jwtUtil.generateToken(request.getUsername());
            return ResponseEntity.ok(new AuthResponseDto(200, token));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                .body(new AuthResponseDto(401, null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new AuthResponseDto(500, null));
        }
    }
}