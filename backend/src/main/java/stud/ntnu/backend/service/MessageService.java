package stud.ntnu.backend.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.data.ConversationPreviewDto;
import stud.ntnu.backend.data.ItemPreviewDto;
import stud.ntnu.backend.data.MessageCreateDto;
import stud.ntnu.backend.data.MessageDto;
import stud.ntnu.backend.data.MessageResponseDto;
import stud.ntnu.backend.data.MessageUserDto;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.ItemImage;
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

  /**
   * <h3>Save a new message</h3>
   * <p>Persists a message to the database and returns the created message.</p>
   *
   * @param messageDto DTO containing message data
   * @return The persisted message as a MessageResponseDto
   */
    public MessageResponseDto saveMessage(MessageCreateDto messageDto) {
      User sender = userRepository.findById(messageDto.getSenderId())
          .orElseThrow(() -> new RuntimeException("Sender not found"));

      User receiver = userRepository.findById(messageDto.getReceiverId())
          .orElseThrow(() -> new RuntimeException("Receiver not found"));

      Item item = itemRepository.findById(messageDto.getItemId())
          .orElseThrow(() -> new RuntimeException("Item not found"));

      Message message = Message.builder().sender(sender).receiver(receiver).item(item)
          .content(messageDto.getContent()).read(false).build();

      Message savedMessage = messageRepository.save(message);

      return mapToMessageResponseDto(savedMessage);
    }

  /**
   * <h3>Get all conversations for a user</h3>
   * <p>This method retrieves all conversations for a given user, grouping messages by conversation
   * and returning a list of conversation previews.</p>
   *
   * @param email the email of the user whose conversations are to be retrieved
   * @return a list of {@link ConversationPreviewDto} objects representing the user's conversations
   */
  public List<ConversationPreviewDto> getUserConversations(String email) {
    User currentUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

    List<Message> allMessages = messageRepository.findAllByUserInvolved(currentUser.getId());

    Map<String, List<Message>> grouped = groupMessagesByConversation(currentUser, allMessages);

    return grouped.values().stream().filter(conversation -> !conversation.isEmpty())
        .map(messages -> buildConversationPreview(currentUser, messages)).sorted(
            Comparator.comparing((ConversationPreviewDto c) -> c.getLastMessage().getSentAt())
                .reversed()).toList();
  }

  /**
   * <h3>Group messages by conversation</h3>
   * <p>This method groups messages by conversation based on the sender and receiver.</p>
   *
   * @param currentUser the user whose conversations are to be grouped
   * @param messages    the list of messages to be grouped
   * @return a map where the key is a string representing the conversation and the value is a list
   * of messages
   */
  private Map<String, List<Message>> groupMessagesByConversation(User currentUser,
      List<Message> messages) {
    Map<String, List<Message>> grouped = new HashMap<>();

    for (Message m : messages) {
      User other =
          m.getSender().getId().equals(currentUser.getId()) ? m.getReceiver() : m.getSender();
      Long itemId = m.getItem().getId();
      String key = other.getId() + "-" + itemId;

      grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(m);
    }

    return grouped;
  }

  /**
   * <h3>Build a conversation preview</h3>
   * <p>This method constructs a conversation preview DTO from the provided messages.</p>
   *
   * @param currentUser the user whose conversations are being previewed
   * @param messages    the list of messages in the conversation
   * @return a {@link ConversationPreviewDto} object representing the conversation preview
   */
  private ConversationPreviewDto buildConversationPreview(User currentUser,
      List<Message> messages) {
    messages.sort(Comparator.comparing(Message::getSentAt).reversed());
    Message lastMessage = messages.getFirst();
    Item item = lastMessage.getItem();

    User otherUser =
        lastMessage.getSender().getId().equals(currentUser.getId()) ? lastMessage.getReceiver()
            : lastMessage.getSender();

    int unreadCount = (int) messages.stream()
        .filter(m -> m.getReceiver().getId().equals(currentUser.getId()) && !m.isRead()).count();

    return ConversationPreviewDto.builder().id(lastMessage.getId()).otherUser(toUserDto(otherUser))
        .item(toItemDto(item)).lastMessage(toMessageDto(lastMessage))
        .unreadMessagesCount(unreadCount).relatedItem(toRelatedItemDto(item)).build();
  }

  /**
   * <h3>Convert a User to MessageUserDto</h3>
   * <p>This method converts a User entity to a MessageUserDto.</p>
   *
   * @param user the user entity to be converted
   * @return a {@link MessageUserDto} object representing the user
   */
  private MessageUserDto toUserDto(User user) {
    return MessageUserDto.builder().id(user.getId()).displayName(user.getDisplayName())
        .email(user.getEmail()).build();
  }

  /**
   * <h3>Convert a Message to MessageDto</h3>
   * <p>This method converts a Message entity to a MessageDto.</p>
   *
   * @param m the message entity to be converted
   * @return a {@link MessageDto} object representing the message
   */
  private MessageDto toMessageDto(Message m) {
    return MessageDto.builder().id(m.getId()).content(m.getContent()).sentAt(m.getSentAt())
        .read(m.isRead()).build();
  }

  /**
   * <h3>Convert an Item to ItemPreviewDto</h3>
   * <p>This method converts an Item entity to an ItemPreviewDto.</p>
   *
   * @param item the item entity to be converted
   * @return a {@link ItemPreviewDto} object representing the item
   */
  private ItemPreviewDto toItemDto(Item item) {
    String imageUrl =
        item.getImages() != null && !item.getImages().isEmpty() ? item.getImages().stream()
            .min(Comparator.comparing(ItemImage::getPosition)).map(ItemImage::getImageUrl)
            .orElse(null) : null;

    return ItemPreviewDto.builder().id(item.getId()).title(item.getBriefDescription())
        .price(item.getPrice()).imageUrl(imageUrl).latitude(item.getLatitude())
        .longitude(item.getLongitude()).build();
  }

  /**
   * <h3>Convert an Item to RelatedItemDto</h3>
   * <p>This method converts an Item entity to a RelatedItemDto.</p>
   *
   * @param item the item entity to be converted
   * @return a {@link ConversationPreviewDto.RelatedItemDto} object representing the related item
   */
  private ConversationPreviewDto.RelatedItemDto toRelatedItemDto(Item item) {
    return ConversationPreviewDto.RelatedItemDto.builder().id(item.getId())
        .title(item.getBriefDescription()).build();
  }

  /**
   * <h3>Get messages for a specific item</h3>
   * <p>This method retrieves all messages related to a specific item for a given user.</p>
   *
   * @param email  the email of the user whose messages are to be retrieved
   * @param itemId the ID of the item whose messages are to be retrieved
   * @return a list of {@link MessageResponseDto} objects representing the messages for the item
   */
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
      messages = messageRepository.findConversation(currentUser.getId(), otherUser.getId(), itemId);
    } else {
      // Just get all messages related to the item for this user
      messages = messageRepository.findAllByUserInvolved(currentUser.getId()).stream()
          .filter(message -> message.getItem().getId().equals(itemId)).collect(Collectors.toList());
    }

    return messages.stream().map(this::mapToMessageResponseDto).collect(Collectors.toList());
  }

  private MessageResponseDto mapToMessageResponseDto(Message message) {
    return MessageResponseDto.builder().id(message.getId())
        .sender(mapToMessageUserDto(message.getSender()))
        .receiver(mapToMessageUserDto(message.getReceiver())).item(
            new MessageResponseDto.ItemReferenceDto(message.getItem().getId(),
                message.getItem().getBriefDescription())).messageContent(message.getContent())
        .sentDate(message.getSentAt()).build();
  }

  private MessageUserDto mapToMessageUserDto(User user) {
    return MessageUserDto.builder().id(user.getId()).email(user.getEmail())
        .displayName(user.getDisplayName()).build();
  }
}