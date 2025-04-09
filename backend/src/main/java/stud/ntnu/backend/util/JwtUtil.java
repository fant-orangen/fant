package stud.ntnu.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import stud.ntnu.backend.repository.UserRepository;

// TODO: remove everything related to claims that's unnecessary

/**
 * <h2>JwtUtil</h2>
 * <p>Utility class for generating and validating JSON Web Tokens (JWTs).</p>
 */
@Component
public class JwtUtil {

  /**
   * <h3>Secret key for signing JWTs</h3>
   * <p>This key should be stored securely and not hardcoded in production.</p>
   */
  private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
  // TODO: secure secret key (env?)

  /**
   * <h3>JWT expiration time</h3>
   * <p>Token validity time in milliseconds (5 hours).</p>
   */
  private final long jwtExpirationMs = 1000 * 60 * 60 * 5;

  private final UserRepository userRepository;

  // Add constructor for dependency injection
  public JwtUtil(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * <h3>Generate a JWT token</h3>
   * <p>Creates a JWT token for the given email.</p>
   *
   * @param email the email for which the token is generated
   * @return the generated JWT token
   */
  public String generateToken(String email) {
    Map<String, Object> claims = new HashMap<>();
    // Find user and get their role
    userRepository.findByEmail(email).ifPresent(user -> {
      claims.put("role", user.getRole().toString());
      claims.put("userId", user.getId());
    });
    return createToken(claims, email);
  }

  /**
   * <h3>Create a JWT token</h3>
   * <p>Helper method to create a JWT token with claims and subject.</p>
   *
   * @param claims  the claims to be included in the token
   * @param subject the subject (email) of the token
   * @return the created JWT token
   */
  private String createToken(Map<String, Object> claims, String subject) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

    return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(now)
        .setExpiration(expiryDate).signWith(secretKey).compact();
  }

  /**
   * <h3>Extract email from token</h3>
   * <p>Extracts the email (subject) from the given JWT token.</p>
   *
   * @param token the JWT token
   * @return the email extracted from the token
   */
  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * <h3>Extract expiration date from token</h3>
   * <p>Extracts the expiration date from the given JWT token.</p>
   *
   * @param token the JWT token
   * @return the expiration date extracted from the token
   */
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * <h3>Extract a specific claim from token</h3>
   * <p>Extracts a specific claim from the given JWT token using a claims resolver function.</p>
   *
   * @param token          the JWT token
   * @param claimsResolver the function to resolve the claim
   * @param <T>            the type of the claim
   * @return the extracted claim
   */
  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * <h3>Extract all claims from token</h3>
   * <p>Extracts all claims from the given JWT token.</p>
   *
   * @param token the JWT token
   * @return the claims extracted from the token
   */
  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
  }

  /**
   * <h3>Check if token is expired</h3>
   * <p>Checks if the given JWT token is expired.</p>
   *
   * @param token the JWT token
   * @return true if the token is expired, false otherwise
   */
  public Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * <h3>Validate token</h3>
   * <p>Validates the given JWT token by checking its email and expiration date.</p>
   *
   * @param token the JWT token
   * @param email the email to validate against
   * @return true if the token is valid, false otherwise
   */
  public Boolean validateToken(String token, String email) {
    final String extractedEmail = extractEmail(token);
    return (extractedEmail.equals(email) && !isTokenExpired(token));
  }
}