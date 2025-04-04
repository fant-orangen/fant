package stud.ntnu.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.data.*;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.Message;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.MessageRepository;
import stud.ntnu.backend.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <h2>MessageService</h2>
 * <p>Service for handling message-related operations.</p>
 */
@Service
@RequiredArgsConstructor
public class MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ItemService itemService;

  /**
   * <h3>Get conversations for a user</h3>
   * <p>Retrieves all conversations for a user identified by their email.</p>
   *
   * @param email The email of the user
   * @return A list of conversation previews
   */
  public List<ConversationPreviewDto> getUserConversations(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

    // Get all messages where the user is either sender or receiver
    List<Message> allMessages = messageRepository.findAllByUserInvolved(user.getId());

    // Group messages by conversation (other user + item)
    Map<String, List<Message>> conversations = new HashMap<>();

    for (Message message : allMessages) {
      Long otherUserId;
      if (message.getSender().getId().equals(user.getId())) {
        otherUserId = message.getReceiver().getId();
      } else {
        otherUserId = message.getSender().getId();
      }

      String conversationKey = otherUserId + "-" + message.getItem().getId();

      if (!conversations.containsKey(conversationKey)) {
        conversations.put(conversationKey, new ArrayList<>());
      }
      conversations.get(conversationKey).add(message);
    }

    // Convert each conversation to a preview DTO
    return conversations.entrySet().stream()
        .map(entry -> {
          String[] keys = entry.getKey().split("-");
          Long otherUserId = Long.parseLong(keys[0]);
          Long itemId = Long.parseLong(keys[1]);
          List<Message> messages = entry.getValue();

          return createConversationPreview(user.getId(), otherUserId, itemId, messages);
        })
        .collect(Collectors.toList());
  }

  /**
   * <h3>Create a conversation preview</h3>
   * <p>Creates a DTO for conversation preview from messages.</p>
   *
   * @param userId      The ID of the current user
   * @param otherUserId The ID of the other user in the conversation
   * @param itemId      The ID of the item related to the conversation
   * @param messages    List of messages in the conversation
   * @return A conversation preview DTO
   */
  private ConversationPreviewDto createConversationPreview(Long userId, Long otherUserId,
      Long itemId, List<Message> messages) {
    // Sort messages by sent time (descending)
    messages.sort((m1, m2) -> m2.getSentAt().compareTo(m1.getSentAt()));

    // Get the other user
    User otherUser = userRepository.findById(otherUserId)
        .orElseThrow(() -> new RuntimeException("User not found with ID: " + otherUserId));

    // Get the item
    Item item = itemService.findById(itemId);

    // Get the last message
    Message lastMessage = !messages.isEmpty() ? messages.get(0) : null;

    // Count unread messages
    int unreadCount = (int) messages.stream()
        .filter(m -> m.getReceiver().getId().equals(userId) && !m.isRead())
        .count();

    // Create the DTO
    return ConversationPreviewDto.builder()
        .id(otherUserId) // Using other user's ID as conversation ID
        .otherUser(MessageUserDto.builder()
            .id(otherUser.getId())
            .displayName(otherUser.getDisplayName())
            .email(otherUser.getEmail())
            .build())
        .item(ItemPreviewDto.builder()
            .id(item.getId())
            .title(item.getBriefDescription())
            .price(item.getPrice())
            .imageUrl(itemService.getFirstImageUrl(item))
            .build())
        .lastMessage(lastMessage != null ? MessageDto.builder()
            .id(lastMessage.getId())
            .content(lastMessage.getContent())
            .senderId(lastMessage.getSender().getId())
            .receiverId(lastMessage.getReceiver().getId())
            .sentAt(lastMessage.getSentAt())
            .read(lastMessage.isRead())
            .build() : null)
        .unreadMessagesCount(unreadCount)
        .relatedItem(ConversationPreviewDto.RelatedItemDto.builder()
            .id(item.getId())
            .title(item.getBriefDescription())
            .build())
        .build();
  }
}