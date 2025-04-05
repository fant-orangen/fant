package stud.ntnu.backend.controller;

import jakarta.validation.constraints.Positive;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.ConversationPreviewDto;
import stud.ntnu.backend.data.MessageResponseDto;
import stud.ntnu.backend.service.MessageService;

import java.util.List;
import stud.ntnu.backend.service.UserService;

/**
 * <h2>MessageController</h2>
 * <p>Controller for messaging operations between users.</p>
 */
@RestController
@RequestMapping("/api/messaging")
@RequiredArgsConstructor
public class MessageController {

  /**
   * <h3>Message Service</h3>
   * <p>Service handling message-related operations.</p>
   *
   * @see MessageService
   */
  private final MessageService messageService;

  /**
   * <h3>User Service</h3>
   * <p>Service handling user-related operations.</p>
   *
   * @see UserService
   */
  private final UserService userService;

  /**
   * <h3>Get Conversations</h3>
   * <p>Retrieves all conversations for the current user.</p>
   *
   * @param principal the authenticated user
   * @return list of {@link ConversationPreviewDto}
   */
  @GetMapping("/conversations")
  public ResponseEntity<List<ConversationPreviewDto>> getConversations(Principal principal) {
    return ResponseEntity.ok(
        messageService.getUserConversations(userService.getCurrentUser(principal)));
  }

  /**
   * <h3>Get Item Messages</h3>
   * <p>Retrieves messages for a specific item.</p>
   *
   * @param itemId    the ID of the item
   * @param principal the authenticated user
   * @return list of {@link MessageResponseDto}
   */
  @GetMapping("/messages")
  public ResponseEntity<List<MessageResponseDto>> getItemMessages(
      @Positive @RequestParam Long itemId,
      Principal principal) {
    return ResponseEntity.ok(
        messageService.getItemMessages(userService.getCurrentUser(principal), itemId));
  }
}