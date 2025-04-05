package stud.ntnu.backend.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import stud.ntnu.backend.util.JwtUtil;

import java.util.Collections;
import java.util.List;

/**
 * <h2>WebSocketAuthInterceptor</h2>
 * <p>Interceptor for authenticating WebSocket connections via JWT tokens.</p>
 * <p>This interceptor validates JWT tokens from incoming WebSocket connection requests
 * and establishes authentication for authorized users.</p>
 */
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

  /**
   * <h3>JWT Utility</h3>
   * <p>Utility for JWT token validation and data extraction.</p>
   */
  private final JwtUtil jwtUtil;

  /**
   * <h3>Pre-send Processing</h3>
   * <p>Intercepts WebSocket messages before they are sent to validate authentication.</p>
   * <p>For CONNECT commands, extracts and validates JWT token to authenticate the user.</p>
   *
   * @param message the message being processed
   * @param channel the channel the message is being sent through
   * @return the processed message
   */
  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
        StompHeaderAccessor.class);

    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
      try {
        String token = extractToken(accessor);

        if (token != null && !token.isEmpty()) {
          String email = jwtUtil.extractEmail(token);
          if (email != null && !jwtUtil.isTokenExpired(token)) {
            List<SimpleGrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

            Authentication auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
            accessor.setUser(auth);
          }
        }
        // If no valid token, just continue without setting authentication
      } catch (Exception e) {
        // Log but don't fail the connection
        // This prevents system initialization errors
        System.err.println("Error in WebSocket authentication: " + e.getMessage());
      }
    }
    return message;
  }

  /**
   * <h3>Extract JWT Token</h3>
   * <p>Attempts to extract the JWT token from various locations in the request.</p>
   * <p>First tries headers, then fallbacks to URL parameters.</p>
   *
   * @param accessor the STOMP header accessor for the connection
   * @return the extracted JWT token or null if not found
   */
  private String extractToken(StompHeaderAccessor accessor) {
    // Try to get token from headers (sent by frontend)
    List<String> authorization = accessor.getNativeHeader("Authorization");
    if (authorization != null && !authorization.isEmpty()) {
      String bearerToken = authorization.get(0);
      if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
        return bearerToken.substring(7);
      }
    }

    // Try to get token from URL parameters (fallback)
    String query = accessor.getFirstNativeHeader("simpConnectMessage");
    if (query != null && query.contains("token=")) {
      int tokenIndex = query.indexOf("token=");
      if (tokenIndex >= 0) {
        String tokenPart = query.substring(tokenIndex + 6);
        int endIndex = tokenPart.indexOf("&");
        return endIndex > 0 ? tokenPart.substring(0, endIndex) : tokenPart;
      }
    }

    return null;
  }
}