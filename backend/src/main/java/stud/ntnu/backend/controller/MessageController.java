package stud.ntnu.backend.controller;

import jakarta.validation.constraints.Positive;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.message.MessageReadRequestDto;
import stud.ntnu.backend.data.message.ConversationPreviewDto;
import stud.ntnu.backend.data.message.MessageResponseDto;
import stud.ntnu.backend.model.User;
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
  public ResponseEntity<Page<MessageResponseDto>> getItemMessages(
      @Positive @RequestParam Long itemId,
      Principal principal,
      Pageable pageable) {
    return ResponseEntity.ok(
        messageService.getItemMessages(userService.getCurrentUser(principal), itemId, pageable));
  }

  @PostMapping("/readall")
  public ResponseEntity<Void> markMessagesAsRead(
      @RequestBody MessageReadRequestDto request,
      Principal principal) {
    messageService.markMessagesAsRead(request.getMessageIds(),
        userService.getCurrentUser(principal));
    return ResponseEntity.ok().build();
  }
  @PostMapping("/conversations/initiate")
  public ResponseEntity<Map<String, Long>> initiateConversation(
      @RequestParam @Positive Long itemId,
      Principal principal) {

    User currentUser = userService.getCurrentUser(principal);
    // Delegate logic to MessageService
    Long conversationIdentifier = messageService.findOrCreateConversation(currentUser, itemId); // Calls the service

    Map<String, Long> response = new HashMap<>();
    response.put("conversationId", conversationIdentifier);
    return ResponseEntity.ok(response);
  }
}