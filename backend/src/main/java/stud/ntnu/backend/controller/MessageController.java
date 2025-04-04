package stud.ntnu.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.ConversationPreviewDto;
import stud.ntnu.backend.service.MessageService;

import java.util.List;

/**
 * <h2>MessageController</h2>
 * <p>Controller for handling messaging related operations.</p>
 */
@RestController
@RequestMapping("/api/messaging")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  /**
   * <h3>Get all conversations for the authenticated user</h3>
   * <p>Returns a list of conversation previews containing information about other users,
   * related items, and last messages.</p>
   *
   * @return A list of conversation preview DTOs
   */
  @GetMapping("/conversations")
  public ResponseEntity<List<ConversationPreviewDto>> getConversations() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    List<ConversationPreviewDto> conversations = messageService.getUserConversations(email);
    return ResponseEntity.ok(conversations);
  }
}