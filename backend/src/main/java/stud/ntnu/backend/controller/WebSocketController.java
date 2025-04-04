package stud.ntnu.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import stud.ntnu.backend.data.MessageDto;
import stud.ntnu.backend.service.MessageService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

  private final SimpMessagingTemplate messagingTemplate;
  private final MessageService messageService;

  @MessageMapping("/chat.send") // Client sends to /app/chat.send
  public void sendMessage(@Payload MessageDto message) {
    // 1. Persist message to DB
    MessageDto saved = messageService.saveWebSocketMessage(message);

    // 2. Broadcast to receiver over /topic/messages/{receiverId}
    messagingTemplate.convertAndSend("/topic/messages/" + message.getReceiverId(), saved);
    
    // 3. Also send back to sender to confirm delivery
    messagingTemplate.convertAndSend("/topic/messages/" + message.getSenderId(), saved);
  }

}
