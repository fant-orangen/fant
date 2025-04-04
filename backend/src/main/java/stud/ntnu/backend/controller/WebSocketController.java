package stud.ntnu.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import stud.ntnu.backend.data.MessageCreateDto;
import stud.ntnu.backend.data.MessageResponseDto;
import stud.ntnu.backend.data.WebSocketMessageDto;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.service.MessageService;
import stud.ntnu.backend.service.UserService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

  private final SimpMessagingTemplate messagingTemplate;
  private final MessageService messageService;
  private final UserService userService;

  @MessageMapping("/chat.send") // Client sends to /app/chat.send
  public void sendMessage(@Payload WebSocketMessageDto messageDto,
      SimpMessageHeaderAccessor headerAccessor) {
    // 1. Extract authenticated user from the WebSocket session
    Principal principal = headerAccessor.getUser();
    if (principal == null) {
      throw new RuntimeException("User not authenticated");
    }

    // 2. Get the sender details from the authentication context
    User sender = userService.getUserByEmail(principal.getName());

    // 3. Create a proper MessageCreateDto with sender ID included
    MessageCreateDto createDto = MessageCreateDto.builder()
        .senderId(sender.getId())
        .receiverId(Long.valueOf(messageDto.getReceiver().getId()))
        .itemId(Long.valueOf(messageDto.getItem().getId()))
        .content(messageDto.getMessageContent())
        .build();

    // 4. Save message and get response
    MessageResponseDto savedMessage = messageService.saveMessage(createDto);

    // 5. Send to receiver
    messagingTemplate.convertAndSend(
        "/topic/messages/" + createDto.getReceiverId(),
        savedMessage
    );

    // 6. Also send back to sender
    messagingTemplate.convertAndSend(
        "/topic/messages/" + sender.getId(),
        savedMessage
    );
  }
}