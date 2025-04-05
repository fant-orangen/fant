package stud.ntnu.backend.filter;

import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import stud.ntnu.backend.util.JwtUtil;

import java.io.IOException;

/**
 * <h2>JwtAuthFilter</h2>
 * <p>Filter for processing JWT authentication in incoming HTTP requests.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  /**
   * <h3>UserDetailsService</h3>
   * <p>Service for loading user-specific data. The class UserAuthenticationService is used.</p>
   */
  private final UserDetailsService userDetailsService;

  /**
   * <h3>JWT Utility</h3>
   * <p>Utility class for generating and validating JWT tokens.</p>
   */
  private final JwtUtil jwtUtil;

  /**
   * <h3>Filter incoming requests</h3>
   * <p>Filters each incoming request to check for a valid JWT token in the Authorization
   * header.</p>
   *
   * @param request     the HTTP request
   * @param response    the HTTP response
   * @param filterChain the filter chain
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException      if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    try {

      final String authHeader = request.getHeader("Authorization");

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
      }

      String token = authHeader.substring(7);
      String username = jwtUtil.extractEmail(token);

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

        if (jwtUtil.validateToken(token, userDetails.getUsername())) {
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(userDetails, null,
                  userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }

      filterChain.doFilter(request, response);

    } catch (SignatureException e) {
      log.warn(e.getMessage());
      response.setStatus(401);
      response.getWriter().write("Invalid JWT signature");
    }
  }
}