package stud.ntnu.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.data.ConversationPreviewDto;
import stud.ntnu.backend.data.MessageDto;
import stud.ntnu.backend.data.MessageResponseDto;
import stud.ntnu.backend.data.MessageUserDto;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.Message;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.ItemRepository;
import stud.ntnu.backend.repository.MessageRepository;
import stud.ntnu.backend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ItemRepository itemRepository;

  public List<ConversationPreviewDto> getUserConversations(String email) {
    // Implementation for conversations
    // This can be implemented later
    return List.of();
  }

  public List<MessageResponseDto> getItemMessages(String email, Long itemId) {
    User currentUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found"));

    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new RuntimeException("Item not found"));

    // Find either the owner or potential buyer based on the current user
    User otherUser;
    if (item.getSeller().getId().equals(currentUser.getId())) {
      // Current user is the seller, look for messages from any buyer
      // This is simplified - in a real scenario, you'd need to determine which buyer
      otherUser = null; // You'll need logic to identify the specific buyer
    } else {
      // Current user is a potential buyer, the other user is the seller
      otherUser = item.getSeller();
    }

    List<Message> messages;
    if (otherUser != null) {
      messages = messageRepository.findConversation(
          currentUser.getId(), otherUser.getId(), itemId);
    } else {
      // Just get all messages related to the item for this user
      messages = messageRepository.findAllByUserInvolved(currentUser.getId())
          .stream()
          .filter(message -> message.getItem().getId().equals(itemId))
          .collect(Collectors.toList());
    }

    return messages.stream()
        .map(this::mapToMessageResponseDto)
        .collect(Collectors.toList());
  }

  private MessageResponseDto mapToMessageResponseDto(Message message) {
    return MessageResponseDto.builder()
        .id(message.getId())
        .sender(mapToMessageUserDto(message.getSender()))
        .receiver(mapToMessageUserDto(message.getReceiver()))
        .item(new MessageResponseDto.ItemReferenceDto(
            message.getItem().getId(),
            message.getItem().getBriefDescription()))
        .messageContent(message.getContent())
        .sentDate(message.getSentAt())
        .build();
  }

  private MessageUserDto mapToMessageUserDto(User user) {
    return MessageUserDto.builder()
        .id(user.getId())
        .email(user.getEmail())
        .displayName(user.getDisplayName())
        .build();
  }
}