package stud.ntnu.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import stud.ntnu.backend.interceptor.WebSocketAuthInterceptor;

/**
 * <h2>WebSocketConfig</h2>
 * <p>Configuration class for WebSocket and STOMP messaging.</p>
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  /**
   * <h3>WebSocket Authentication Interceptor</h3>
   * <p>Interceptor for authenticating WebSocket connections.</p>
   *
   * @see WebSocketAuthInterceptor
   */
  private final WebSocketAuthInterceptor webSocketAuthInterceptor;

  /**
   * <h3>Constructor</h3>
   * <p>Creates a new WebSocketConfig instance.</p>
   *
   * @param webSocketAuthInterceptor the authentication interceptor
   */
  public WebSocketConfig(WebSocketAuthInterceptor webSocketAuthInterceptor) {
    this.webSocketAuthInterceptor = webSocketAuthInterceptor;
  }

  /**
   * <h3>Configure Message Broker</h3>
   * <p>Configures message broker destinations and prefixes.</p>
   *
   * @param config the message broker registry
   * @see MessageBrokerRegistry
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }

  /**
   * <h3>Register STOMP Endpoints</h3>
   * <p>Registers WebSocket endpoints for client connections.</p>
   *
   * @param registry the STOMP endpoint registry
   * @see StompEndpointRegistry
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
  }

  /**
   * <h3>Configure Client Inbound Channel</h3>
   * <p>Adds authentication interceptor to inbound channel.</p>
   *
   * @param registration the channel registration
   * @see ChannelRegistration
   */
  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(webSocketAuthInterceptor);
  }
}