package stud.ntnu.backend.controller;

import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.MessageRequestDto;
import stud.ntnu.backend.data.MessageResponseDto;
import stud.ntnu.backend.model.Message;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.service.MessageService;
import stud.ntnu.backend.service.UserService;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {
  private final MessageService messageService;
  private final UserService userService;

  @PostMapping
  public ResponseEntity<MessageResponseDto> sendMessage(
      @RequestBody MessageRequestDto messageRequestDto, Principal principal) {
    return ResponseEntity.ok(
        messageService.sendMessage(messageRequestDto, userService.getCurrentUser(principal)));
  }

  @GetMapping("/{userId}")
  public ResponseEntity<List<MessageResponseDto>> getConversation(@PathVariable long userId,
                                                       Principal principal) {
    return ResponseEntity.ok(messageService.getConversation(userService.getUserById(userId),
        userService.getCurrentUser(principal)));
  }
}
