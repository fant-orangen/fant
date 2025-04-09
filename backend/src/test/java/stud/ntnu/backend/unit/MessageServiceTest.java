package stud.ntnu.backend.unit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import stud.ntnu.backend.data.message.ConversationPreviewDto;
import stud.ntnu.backend.data.message.MessageCreateDto;
import stud.ntnu.backend.data.message.MessageResponseDto;
import stud.ntnu.backend.data.message.WebSocketMessageDto;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.Message;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.ItemRepository;
import stud.ntnu.backend.repository.MessageRepository;
import stud.ntnu.backend.repository.UserRepository;

import java.util.Optional;
import stud.ntnu.backend.service.MessageService;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ItemRepository itemRepository;

  @Mock
  private SimpMessagingTemplate messagingTemplate;

  @InjectMocks
  private MessageService messageService;

  private User sender;
  private User receiver;
  private Item item;
  private MessageCreateDto messageCreateDto;

  @BeforeEach
  void setUp() {
    // Mock User data
    sender = User.builder().id(1L).email("sender@example.com").displayName("Sender").build();
    receiver = User.builder().id(2L).email("receiver@example.com").displayName("Receiver").build();

    // Mock Item data
    item = Item.builder().id(1L).seller(receiver).briefDescription("Item 1")
        .price(BigDecimal.valueOf(100.0)).build();

    // Mock MessageCreateDto
    messageCreateDto = MessageCreateDto.builder()
        .senderId(sender.getId())
        .receiverId(receiver.getId())
        .itemId(item.getId())
        .content("Hello, I am interested in this item!")
        .build();
  }

  @Test
  void testSaveMessage() {
    // Arrange
    Message message = Message.builder()
        .sender(sender)
        .receiver(receiver)
        .item(item)
        .content("Hello, I am interested in this item!")
        .read(false)
        .build();

    // Mock repository behavior
    when(userRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
    when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
    when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
    when(messageRepository.save(any(Message.class))).thenReturn(message);

    // Act
    MessageResponseDto responseDto = messageService.saveMessage(messageCreateDto);

    // Assert
    assertNotNull(responseDto);
    assertEquals("Hello, I am interested in this item!", responseDto.getMessageContent());
    assertEquals(sender.getId(), responseDto.getSender().getId());
    assertEquals(receiver.getId(), responseDto.getReceiver().getId());
  }

  @Test
  void testSaveMessage_ThrowsEntityNotFoundException_WhenSenderNotFound() {
    // Arrange
    when(userRepository.findById(sender.getId())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> messageService.saveMessage(messageCreateDto));
  }

  @Test
  void testGetItemMessages() {
    // Arrange
    PageRequest pageRequest = PageRequest.of(0, 10);
    Message message = Message.builder()
        .sender(sender)
        .receiver(receiver)
        .item(item)
        .content("Hello, I am interested in this item!")
        .read(false)
        .build();

    Page<Message> messagePage = Page.empty(); // Simulate empty page result

    // Mock repository behavior
    when(itemRepository.existsById(item.getId())).thenReturn(true);
    when(messageRepository.findByUserInvolvedAndItemId(sender.getId(), item.getId(),
        pageRequest)).thenReturn(messagePage);

    // Act
    Page<MessageResponseDto> result =
        messageService.getItemMessages(sender, item.getId(), pageRequest);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void testGetItemMessages_ItemNotFound() {
    // Arrange
    PageRequest pageRequest = PageRequest.of(0, 10);

    // Mock repository behavior
    when(itemRepository.existsById(item.getId())).thenReturn(false);

    // Act & Assert
    assertThrows(EntityNotFoundException.class,
        () -> messageService.getItemMessages(sender, item.getId(), pageRequest));
  }

  @Test
  void testFindOrCreateConversation() {
    // Arrange
    Message existingMessage = Message.builder()
        .id(1L)
        .sender(sender)
        .receiver(receiver)
        .item(item)
        .content("Conversation started about: Item 1")
        .read(true)
        .build();

    // Mock repository behavior
    when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
    when(messageRepository.findConversation(sender.getId(), receiver.getId(), item.getId()))
        .thenReturn(java.util.Collections.singletonList(existingMessage));

    // Act
    Long conversationId = messageService.findOrCreateConversation(sender, item.getId());

    // Assert
    assertNotNull(conversationId);
    assertEquals(existingMessage.getId(), conversationId);
  }

  @Test
  void testFindOrCreateConversation_ItemNotFound() {
    // Arrange
    when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class,
        () -> messageService.findOrCreateConversation(sender, item.getId()));
  }

  @Test
  void testFindOrCreateConversation_SelfConversation() {
    // Arrange
    when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
    item.setSeller(sender);  // Setting the seller to the sender

    // Act & Assert
    assertThrows(IllegalArgumentException.class,
        () -> messageService.findOrCreateConversation(sender, item.getId()));
  }


  @Test
  void testSendWebSocketMessage() {
    WebSocketMessageDto.ReceiverDto socketReceiver = new WebSocketMessageDto.ReceiverDto();
    socketReceiver.setId(String.valueOf(2L));

    WebSocketMessageDto.ItemDto socketItem = new WebSocketMessageDto.ItemDto();
    socketItem.setId(String.valueOf(1L));

    Message message =
        Message.builder().sender(sender).receiver(receiver).item(item).content("Hello!").read(false)
            .build();
    // Arrange
    WebSocketMessageDto messageDto = WebSocketMessageDto.builder()
        .receiver(socketReceiver)
        .item(socketItem)
        .messageContent("Hello!")
        .build();

    when(userRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
    when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
    when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
    when(messageRepository.save(any(Message.class))).thenReturn(message);

    // Act
    messageService.sendWebSocketMessage(sender, messageDto);
  }

  @Test
  void testSendWebSocketMessage_ItemNotFound() {
    WebSocketMessageDto.ReceiverDto socketReceiver = new WebSocketMessageDto.ReceiverDto();
    socketReceiver.setId(String.valueOf(2L));

    WebSocketMessageDto.ItemDto socketItem = new WebSocketMessageDto.ItemDto();
    socketItem.setId(String.valueOf(1L));
    // Arrange
    WebSocketMessageDto messageDto = WebSocketMessageDto.builder()
        .receiver(socketReceiver)
        .item(socketItem)
        .messageContent("Hello!")
        .build();

    when(userRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
    when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
    when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class,
        () -> messageService.sendWebSocketMessage(sender, messageDto));
  }

  @Test
  void testGetUserConversations_EmptyMessages() {
    // Arrange
    when(messageRepository.findAllByUserInvolved(sender.getId())).thenReturn(List.of());

    // Act
    List<ConversationPreviewDto> conversations = messageService.getUserConversations(sender);

    // Assert
    assertTrue(conversations.isEmpty());
  }

  @Test
  void testGetUserConversations_ValidMessages() {
    // Arrange
    Message message = Message.builder()
        .sender(sender)
        .receiver(receiver)
        .item(item)
        .content("Hello!")
        .read(true)
        .build();

    when(messageRepository.findAllByUserInvolved(sender.getId())).thenReturn(List.of(message));

    // Act
    List<ConversationPreviewDto> conversations = messageService.getUserConversations(sender);

    // Assert
    assertFalse(conversations.isEmpty());
    assertEquals("Hello!", conversations.getFirst().getLastMessage().getContent());
  }

  @Test
  void testFindOrCreateConversation_SellerNotFound() {
    // Arrange
    Item itemWithoutSeller = Item.builder().id(1L).briefDescription("Item without seller").build();
    when(itemRepository.findById(itemWithoutSeller.getId())).thenReturn(
        Optional.of(itemWithoutSeller));

    // Act & Assert
    assertThrows(EntityNotFoundException.class,
        () -> messageService.findOrCreateConversation(sender, itemWithoutSeller.getId()));
  }
}
