package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import stud.ntnu.backend.data.message.WebSocketMessageDto;
import stud.ntnu.backend.service.MessageService;
import stud.ntnu.backend.service.UserService;

/**
 * <h2>WebSocketController</h2>
 * <p>Controller for handling real-time WebSocket messaging operations.</p>
 */
@Controller
@RequiredArgsConstructor
public class WebSocketController {

  /**
   * <h3>Message Service</h3>
   * <p>Service handling message processing and delivery.</p>
   *
   * @see MessageService
   */
  private final MessageService messageService;

  /**
   * <h3>User Service</h3>
   * <p>Service handling user authentication and retrieval.</p>
   *
   * @see UserService
   */
  private final UserService userService;

  /**
   * <h3>Send WebSocket Message</h3>
   * <p>Processes and delivers real-time messages via WebSocket.</p>
   *
   * @param messageDto the message content and metadata
   * @param headerAccessor the WebSocket session header accessor
   */
  @MessageMapping("/chat.send")
  public void sendMessage(
      @Payload @Valid WebSocketMessageDto messageDto,
      SimpMessageHeaderAccessor headerAccessor) {
    messageService.sendWebSocketMessage(
        userService.getCurrentUser(Objects.requireNonNull(headerAccessor.getUser())),
        messageDto);
  }
}