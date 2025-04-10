package stud.ntnu.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.data.message.ConversationPreviewDto;
import stud.ntnu.backend.data.message.MessageReadRequestDto;
import stud.ntnu.backend.data.message.MessageResponseDto;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.service.MessageService;
import stud.ntnu.backend.service.UserService;

/**
 * <h2>MessageController</h2>
 * <p>Controller for messaging operations between users.</p>
 */
@RestController
@RequestMapping("/api/messaging")
@RequiredArgsConstructor
@Tag(name = "Messaging", description = "Operations for managing messages and conversations between users.")
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
   * @return {@link ResponseEntity} containing a list of {@link ConversationPreviewDto}
   */
  @GetMapping("/conversations")
  @Operation(summary = "Get User Conversations", description = "Retrieves a list of all conversations for the authenticated user.")
  @ApiResponse(responseCode = "200", description = "List of user's conversations", content = @Content(schema = @Schema(implementation = List.class, subTypes = {
      ConversationPreviewDto.class})))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<List<ConversationPreviewDto>> getConversations(
      @Parameter(hidden = true) Principal principal) {
    return ResponseEntity.ok(
        messageService.getUserConversations(userService.getCurrentUser(principal)));
  }

  /**
   * <h3>Get Item Messages</h3>
   * <p>Retrieves messages for a specific item.</p>
   *
   * @param itemId    the ID of the item
   * @param principal the authenticated user
   * @param pageable  pagination information
   * @return {@link ResponseEntity} containing a paginated list of {@link MessageResponseDto}
   */
  @GetMapping("/messages")
  @Operation(summary = "Get Messages for Item", description = "Retrieves messages related to a specific item for the authenticated user.")
  @ApiResponse(responseCode = "200", description = "Paginated list of messages for the item", content = @Content(schema = @Schema(implementation = Page.class, subTypes = {
      MessageResponseDto.class})))
  @ApiResponse(responseCode = "400", description = "Invalid item ID", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Page<MessageResponseDto>> getItemMessages(
      @Parameter(description = "ID of the item to retrieve messages for", required = true) @Positive
      @RequestParam Long itemId, @Parameter(hidden = true) Principal principal,
      @Parameter(description = "Pagination information (page number, size, sort)")
      Pageable pageable) {
    return ResponseEntity.ok(
        messageService.getItemMessages(userService.getCurrentUser(principal), itemId, pageable));
  }

  /**
   * <h3>Mark Messages as Read</h3>
   * <p>Marks a list of messages as read by the current user.</p>
   *
   * @param request   the request containing the IDs of the messages to mark as read
   * @param principal the authenticated user
   * @return {@link ResponseEntity} with status OK if the messages were successfully marked as read
   */
  @PostMapping("/readall")
  @Operation(summary = "Mark Messages as Read", description = "Marks a list of messages as read by the authenticated user.")
  @ApiResponse(responseCode = "200", description = "Messages marked as read successfully")
  @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Void> markMessagesAsRead(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request containing the IDs of the messages to mark as read", required = true, content = @Content(schema = @Schema(implementation = MessageReadRequestDto.class)))
      @RequestBody MessageReadRequestDto request, @Parameter(hidden = true) Principal principal) {
    messageService.markMessagesAsRead(request.getMessageIds(),
        userService.getCurrentUser(principal));
    return ResponseEntity.ok().build();
  }

  /**
   * <h3>Initiate Conversation for Item</h3>
   * <p>Initiates a new conversation related to a specific item.</p>
   *
   * @param itemId    ID of the item to initiate conversation for
   * @param principal the authenticated user
   * @return {@link ResponseEntity} containing a map with the conversation ID
   */
  @PostMapping("/conversations/initiate")
  @Operation(summary = "Initiate Conversation for Item", description = "Initiates a new conversation related to a specific item.")
  @ApiResponse(responseCode = "200", description = "Conversation initiated/found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class), examples = @io.swagger.v3.oas.annotations.media.ExampleObject(name = "Conversation ID Response", value = "{\"conversationId\": 123}")))
  @ApiResponse(responseCode = "400", description = "Invalid item ID", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "404", description = "Item not found", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  public ResponseEntity<Map<String, Long>> initiateConversation(
      @Parameter(description = "ID of the item to initiate conversation for", required = true)
      @RequestParam @Positive Long itemId, @Parameter(hidden = true) Principal principal) {

    User currentUser = userService.getCurrentUser(principal);
    Long conversationIdentifier = messageService.findOrCreateConversation(currentUser, itemId);

    Map<String, Long> response = new HashMap<>();
    response.put("conversationId", conversationIdentifier);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}