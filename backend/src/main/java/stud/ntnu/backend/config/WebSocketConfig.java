package stud.ntnu.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import stud.ntnu.backend.interceptor.WebSocketAuthInterceptor;

/**
 * <h2>WebSocketConfig</h2>
 * <p>Configuration class for WebSocket messaging.</p>
 * <p>This class sets up WebSocket endpoints, message broker options, and
 * configures the authentication interceptor for secure messaging.</p>
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  /**
   * <h3>WebSocket Authentication Interceptor</h3>
   * <p>Interceptor for authenticating WebSocket connections using JWT tokens.</p>
   */
  private final WebSocketAuthInterceptor webSocketAuthInterceptor;

  /**
   * <h3>Constructor for WebSocketConfig</h3>
   *
   * @param webSocketAuthInterceptor the authentication interceptor for WebSocket connections
   */
  public WebSocketConfig(WebSocketAuthInterceptor webSocketAuthInterceptor) {
    this.webSocketAuthInterceptor = webSocketAuthInterceptor;
  }

  /**
   * <h3>Configure Message Broker</h3>
   * <p>Sets up the message broker with appropriate destination prefixes.</p>
   *
   * @param config the message broker registry to configure
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }

  /**
   * <h3>Register STOMP Endpoints</h3>
   * <p>Registers WebSocket endpoints that clients connect to.</p>
   *
   * @param registry the STOMP endpoint registry
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("*")
        .withSockJS();
  }

  /**
   * <h3>Configure Client Inbound Channel</h3>
   * <p>Adds the authentication interceptor to the client inbound channel.</p>
   *
   * @param registration the channel registration
   */
  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(webSocketAuthInterceptor);
  }
}