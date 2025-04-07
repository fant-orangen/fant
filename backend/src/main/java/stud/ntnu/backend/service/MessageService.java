package stud.ntnu.backend.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.data.message.ConversationPreviewDto;
import stud.ntnu.backend.data.item.ItemPreviewDto;
import stud.ntnu.backend.data.message.MessageCreateDto;
import stud.ntnu.backend.data.message.MessageDto;
import stud.ntnu.backend.data.message.MessageResponseDto;
import stud.ntnu.backend.data.message.MessageUserDto;
import stud.ntnu.backend.data.message.WebSocketMessageDto;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.ItemImage;
import stud.ntnu.backend.model.Message;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.ItemRepository;
import stud.ntnu.backend.repository.MessageRepository;
import stud.ntnu.backend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <h2>MessageService</h2>
 * <p>Service for managing messaging between users.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

  /**
   * <h3>Message Repository</h3>
   * <p>Data access component for messages.</p>
   */
  private final MessageRepository messageRepository;

  /**
   * <h3>User Repository</h3>
   * <p>Data access component for users.</p>
   */
  private final UserRepository userRepository;

  /**
   * <h3>Item Repository</h3>
   * <p>Data access component for items.</p>
   */
  private final ItemRepository itemRepository;

  /**
   * <h3>Messaging Template</h3>
   * <p>Component for WebSocket message delivery.</p>
   */
  private final SimpMessagingTemplate messagingTemplate;

  /**
   * <h3>Save Message</h3>
   * <p>Creates and persists a new message.</p>
   *
   * @param messageDto the message data
   * @return saved {@link MessageResponseDto}
   * @throws EntityNotFoundException if sender, receiver or item not found
   */
  @Transactional
  public MessageResponseDto saveMessage(MessageCreateDto messageDto) {
    User sender = userRepository.findById(messageDto.getSenderId())
        .orElseThrow(() -> new EntityNotFoundException("Sender not found"));
    User receiver = userRepository.findById(messageDto.getReceiverId())
        .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));
    Item item = itemRepository.findById(messageDto.getItemId())
        .orElseThrow(() -> new EntityNotFoundException("Item not found"));

    Message message = Message.builder()
        .sender(sender)
        .receiver(receiver)
        .item(item)
        .content(messageDto.getContent())
        .read(false)
        .build();

    Message savedMessage = messageRepository.save(message);
    return mapToMessageResponseDto(savedMessage);
  }

  /**
   * <h3>Send WebSocket Message</h3>
   * <p>Processes and delivers real-time messages via WebSocket.</p>
   *
   * @param sender     the sending user
   * @param messageDto the message content
   */
  @Transactional
  public void sendWebSocketMessage(User sender, WebSocketMessageDto messageDto) {
    MessageCreateDto createDto = MessageCreateDto.builder()
        .senderId(sender.getId())
        .receiverId(Long.valueOf(messageDto.getReceiver().getId()))
        .itemId(Long.valueOf(messageDto.getItem().getId()))
        .content(messageDto.getMessageContent())
        .build();

    MessageResponseDto savedMessage = saveMessage(createDto);
    messagingTemplate.convertAndSend("/topic/messages/" + createDto.getReceiverId(), savedMessage);
  }

  /**
   * <h3>Get User Conversations</h3>
   * <p>Retrieves all conversations for a user.</p>
   *
   * @param user the requesting user
   * @return list of {@link ConversationPreviewDto}
   */
  public List<ConversationPreviewDto> getUserConversations(User user) {
    List<Message> allMessages = messageRepository.findAllByUserInvolved(user.getId());
    Map<String, List<Message>> grouped = groupMessagesByConversation(user, allMessages);

    return grouped.values().stream()
        .filter(conversation -> !conversation.isEmpty())
        .map(messages -> buildConversationPreview(user, messages))
        .sorted(Comparator.comparing((ConversationPreviewDto c) -> c.getLastMessage().getSentAt())
            .reversed())
        .toList();
  }

  /**
   * <h3>Get Item Messages</h3>
   * <p>Retrieves conversation history about a specific item.</p>
   *
   * @param user   the requesting user
   * @param itemId the item ID
   * @return list of {@link MessageResponseDto}
   */
  public Page<MessageResponseDto> getItemMessages(User user, Long itemId, Pageable pageable) {
    if (!itemRepository.existsById(itemId)) {
      throw new EntityNotFoundException("Item not found");
    }

    return messageRepository.findByUserInvolvedAndItemId(user.getId(), itemId, pageable)
        .map(this::mapToMessageResponseDto);
  }

  /**
   * <h3>Group Messages By Conversation</h3>
   * <p>Organizes messages into conversation groups.</p>
   *
   * @param user     the requesting user
   * @param messages list of messages to group
   * @return map of grouped messages by conversation key
   */
  private Map<String, List<Message>> groupMessagesByConversation(User user,
                                                                 List<Message> messages) {
    Map<String, List<Message>> grouped = new HashMap<>();
    for (Message m : messages) {
      User other = m.getSender().getId().equals(user.getId()) ? m.getReceiver() : m.getSender();
      String key = other.getId() + "-" + m.getItem().getId();
      grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(m);
    }
    return grouped;
  }

  /**
   * <h3>Build Conversation Preview</h3>
   * <p>Creates a conversation summary from messages.</p>
   * <p>Uses the ID of the *earliest* message as the stable conversation identifier.</p>
   *
   * @param user     the requesting user
   * @param messages list of messages in conversation (assumes non-empty)
   * @return {@link ConversationPreviewDto} for the conversation
   */
  private ConversationPreviewDto buildConversationPreview(User user, List<Message> messages) {
    // Sort oldest first to find the stable identifier
    messages.sort(Comparator.comparing(Message::getSentAt));
    Message firstMessage = messages.getFirst(); // Earliest message
    Long conversationIdentifier = firstMessage.getId(); // Use earliest message ID as the main ID

    // Sort newest first to find the last message for preview content
    messages.sort(Comparator.comparing(Message::getSentAt).reversed());
    Message lastMessage = messages.getFirst(); // Newest message for preview

    // Determine the other user based on the last message (or first, doesn't matter)
    User otherUser = lastMessage.getSender().getId().equals(user.getId()) ?
        lastMessage.getReceiver() : lastMessage.getSender();

    // Calculate unread count based on all messages in the thread
    int unreadCount = (int) messages.stream()
        .filter(m -> m.getReceiver().getId().equals(user.getId()) && !m.isRead())
        .count();

    return ConversationPreviewDto.builder()
        .id(conversationIdentifier) // <-- USE EARLIEST MESSAGE ID HERE
        .otherUser(toUserDto(otherUser))
        .item(toItemDto(lastMessage.getItem())) // Item context from last message is fine
        .lastMessage(toMessageDto(lastMessage)) // Preview content from last message
        .unreadMessagesCount(unreadCount)
        .relatedItem(toRelatedItemDto(lastMessage.getItem()))
        .build();
  }

  /**
   * <h3>Map To Message Response DTO</h3>
   * <p>Converts Message entity to response DTO.</p>
   *
   * @param message the message to convert
   * @return {@link MessageResponseDto}
   */
  private MessageResponseDto mapToMessageResponseDto(Message message) {
    return MessageResponseDto.builder()
        .id(message.getId())
        .sender(mapToMessageUserDto(message.getSender()))
        .receiver(mapToMessageUserDto(message.getReceiver()))
        .item(new MessageResponseDto.ItemReferenceDto(
            message.getItem().getId(),
            message.getItem().getBriefDescription()))
        .messageContent(message.getContent())
        .isRead(message.isRead())
        .sentDate(message.getSentAt())
        .build();
  }

  /**
   * <h3>Map To Message User DTO</h3>
   * <p>Converts User entity to message user DTO.</p>
   *
   * @param user the user to convert
   * @return {@link MessageUserDto}
   */
  private MessageUserDto mapToMessageUserDto(User user) {
    return MessageUserDto.builder()
        .id(user.getId())
        .email(user.getEmail())
        .displayName(user.getDisplayName())
        .build();
  }

  /**
   * <h3>Convert To User DTO</h3>
   * <p>Creates a simplified user DTO for messages.</p>
   *
   * @param user the user to convert
   * @return {@link MessageUserDto}
   */
  private MessageUserDto toUserDto(User user) {
    return MessageUserDto.builder()
        .id(user.getId())
        .displayName(user.getDisplayName())
        .email(user.getEmail())
        .build();
  }

  /**
   * <h3>Convert To Message DTO</h3>
   * <p>Creates a simplified message DTO.</p>
   *
   * @param m the message to convert
   * @return {@link MessageDto}
   */
  private MessageDto toMessageDto(Message m) {
    return MessageDto.builder()
        .id(m.getId())
        .content(m.getContent())
        .sentAt(m.getSentAt())
        .read(m.isRead())
        .build();
  }

  /**
   * <h3>Convert To Item DTO</h3>
   * <p>Creates an item preview DTO.</p>
   *
   * @param item the item to convert
   * @return {@link ItemPreviewDto}
   */
  private ItemPreviewDto toItemDto(Item item) {
    String imageUrl = item.getImages() != null && !item.getImages().isEmpty() ?
        item.getImages().stream()
            .min(Comparator.comparing(ItemImage::getPosition))
            .map(ItemImage::getImageUrl)
            .orElse(null) : null;

    return ItemPreviewDto.builder()
        .id(item.getId())
        .title(item.getBriefDescription())
        .price(item.getPrice())
        .imageUrl(imageUrl)
        .latitude(item.getLatitude())
        .longitude(item.getLongitude())
        .build();
  }

  /**
   * <h3>Convert To Related Item DTO</h3>
   * <p>Creates a minimal item reference DTO.</p>
   *
   * @param item the item to convert
   * @return {@link ConversationPreviewDto.RelatedItemDto}
   */
  private ConversationPreviewDto.RelatedItemDto toRelatedItemDto(Item item) {
    return ConversationPreviewDto.RelatedItemDto.builder()
        .id(item.getId())
        .title(item.getBriefDescription())
        .build();
  }

  /**
   * <h3>Mark Messages as Read</h3>
   * <p>Updates specified messages as read if the current user is the recipient.</p>
   *
   * @param messageIds  list of message IDs to mark as read TODO: Add transactional to any and all transactional methods
   * @param currentUser the current authenticated user
   */
  @Transactional
  public void markMessagesAsRead(List<Long> messageIds, User currentUser) {
    if (messageIds != null && !messageIds.isEmpty()) {
      messageRepository.markMessagesAsRead(messageIds, currentUser.getId());
    }
  }

  /**
   * <h3>Find or Create Conversation</h3>
   * <p>Finds an existing conversation thread between the user and the item's seller.
   * If no messages exist, it creates an initial message to establish the thread.</p>
   * <p>Returns an ID that can identify the conversation (currently the ID of the earliest message).</p>
   *
   * @param currentUser The user initiating the conversation.
   * @param itemId      The ID of the item being discussed.
   * @return The ID of the earliest message in the conversation thread.
   * @throws EntityNotFoundException If the item or seller doesn't exist.
   * @throws IllegalArgumentException If the user tries to start a conversation with themselves.
   */
  @Transactional // Make method transactional
  public Long findOrCreateConversation(User currentUser, Long itemId) {
    log.info("Finding or creating conversation for item {} by user {}", itemId, currentUser.getId());

    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> {
          log.error("Item not found with id: {}", itemId);
          return new EntityNotFoundException("Item not found with id: " + itemId);
        });

    User seller = item.getSeller();

    if (seller == null) {
      log.error("Seller not found for item id: {}", itemId);
      throw new EntityNotFoundException("Seller not found for item id: " + itemId);
    }

    if (currentUser.getId().equals(seller.getId())) {
      log.warn("User {} attempted to start a conversation with themselves over item {}", currentUser.getId(), itemId);
      throw new IllegalArgumentException("Cannot start a conversation with yourself.");
    }

    // 1. Check if any messages already exist for this item between these two users
    List<Message> existingMessages = messageRepository.findConversation(currentUser.getId(), seller.getId(), itemId);

    if (!existingMessages.isEmpty()) {
      // Conversation exists, find the earliest message ID to use as a stable identifier
      log.info("Found existing conversation for item {} between users {} and {}. Returning earliest message ID.",
          itemId, currentUser.getId(), seller.getId());
      return existingMessages.stream()
          .min(Comparator.comparing(Message::getSentAt))
          .map(Message::getId)
          .orElseThrow(() -> new IllegalStateException("Conversation found but no messages retrieved.")); // Should not happen
    } else {
      // 2. No existing messages, create an initial "system" message to establish the thread
      log.info("No existing conversation found for item {} between users {} and {}. Creating initial message.",
          itemId, currentUser.getId(), seller.getId());
      Message initialMessage = Message.builder()
          .sender(currentUser) // Or potentially a system user if you have one
          .receiver(seller)
          .item(item)
          .content("Conversation started about: " + item.getBriefDescription()) // System message
          .read(true) // Mark as read for sender, receiver will see it as unread initially
          .build();
      Message savedInitialMessage = messageRepository.save(initialMessage);
      log.info("Initial message saved with ID: {}", savedInitialMessage.getId());

      // Return the ID of this newly created initial message
      return savedInitialMessage.getId();
    }
  }
}