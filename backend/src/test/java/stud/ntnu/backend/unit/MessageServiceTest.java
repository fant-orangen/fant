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

/**
 * <h2>Unit Tests for Message Service</h2>
 * <p>This class contains unit tests for the {@link MessageService} class, focusing on the business logic for saving, retrieving, and managing messages and conversations.</p>
 * <p>The tests utilize Mockito to mock the dependencies of the {@code MessageService}, such as {@link MessageRepository}, {@link UserRepository}, {@link ItemRepository}, and {@link SimpMessagingTemplate}, allowing for isolated testing of the service's methods.</p>
 */
@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

  /**
   * <h3>Mock MessageRepository</h3>
   * <p>A Mockito mock for the {@link MessageRepository} dependency, used to simulate database interactions related to messages.</p>
   */
  @Mock
  private MessageRepository messageRepository;

  /**
   * <h3>Mock UserRepository</h3>
   * <p>A Mockito mock for the {@link UserRepository} dependency, used to simulate database interactions related to users.</p>
   */
  @Mock
  private UserRepository userRepository;

  /**
   * <h3>Mock ItemRepository</h3>
   * <p>A Mockito mock for the {@link ItemRepository} dependency, used to simulate database interactions related to items.</p>
   */
  @Mock
  private ItemRepository itemRepository;

  /**
   * <h3>Mock SimpMessagingTemplate</h3>
   * <p>A Mockito mock for the {@link SimpMessagingTemplate} dependency, used to simulate sending WebSocket messages.</p>
   */
  @Mock
  private SimpMessagingTemplate messagingTemplate;

  /**
   * <h3>InjectMocks MessageService</h3>
   * <p>The instance of {@link MessageService} being tested, with its dependencies injected via Mockito.</p>
   */
  @InjectMocks
  private MessageService messageService;

  /**
   * <h3>Test Sender User</h3>
   * <p>A {@link User} entity representing the sender in the test scenarios.</p>
   */
  private User sender;

  /**
   * <h3>Test Receiver User</h3>
   * <p>A {@link User} entity representing the receiver in the test scenarios.</p>
   */
  private User receiver;

  /**
   * <h3>Test Item</h3>
   * <p>An {@link Item} entity associated with the messages in the test scenarios.</p>
   */
  private Item item;

  /**
   * <h3>Test MessageCreateDto</h3>
   * <p>A {@link MessageCreateDto} used as input for creating messages in the test scenarios.</p>
   */
  private MessageCreateDto messageCreateDto;

  /**
   * <h3>Setup Method</h3>
   * <p>This method is executed before each test case. It initializes mock user and item data, as well as a mock {@link MessageCreateDto}.</p>
   */
  @BeforeEach
  void setUp() {
    // Mock User data
    sender = User.builder().id(1L).email("sender@example.com").displayName("Sender").build();
    receiver = User.builder().id(2L).email("receiver@example.com").displayName("Receiver").build();

    // Mock Item data
    item = Item.builder().id(1L).seller(receiver).briefDescription("Item 1")
        .price(BigDecimal.valueOf(100.0)).build();

    // Mock MessageCreateDto
    messageCreateDto =
        MessageCreateDto.builder().senderId(sender.getId()).receiverId(receiver.getId())
            .itemId(item.getId()).content("Hello, I am interested in this item!").build();
  }

  /**
   * <h3>Test Save Message - Success</h3>
   * <p>Tests the successful saving of a new message. It verifies that the sender, receiver, and item exist, and that the saved message is returned as a {@link MessageResponseDto} with the correct content and participants.</p>
   */
  @Test
  void testSaveMessage() {
    // Arrange
    Message message = Message.builder().sender(sender).receiver(receiver).item(item)
        .content("Hello, I am interested in this item!").read(false).build();

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

  /**
   * <h3>Test Save Message - Sender Not Found - Throws Exception</h3>
   * <p>Tests the scenario where the sender specified in the {@link MessageCreateDto} does not exist. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void testSaveMessage_ThrowsEntityNotFoundException_WhenSenderNotFound() {
    // Arrange
    when(userRepository.findById(sender.getId())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> messageService.saveMessage(messageCreateDto));
  }

  /**
   * <h3>Test Get Item Messages - Success - Empty Page</h3>
   * <p>Tests the successful retrieval of messages related to a specific item for a given user, resulting in an empty page. It verifies that the item exists and an empty {@link Page} of {@link MessageResponseDto} is returned.</p>
   */
  @Test
  void testGetItemMessages() {
    // Arrange
    PageRequest pageRequest = PageRequest.of(0, 10);
    Message message = Message.builder().sender(sender).receiver(receiver).item(item)
        .content("Hello, I am interested in this item!").read(false).build();

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

  /**
   * <h3>Test Get Item Messages - Item Not Found - Throws Exception</h3>
   * <p>Tests the scenario where the item specified in the request does not exist. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
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

  /**
   * <h3>Test Find Or Create Conversation - Existing Conversation</h3>
   * <p>Tests the scenario where a conversation already exists between the sender and the seller for a given item. It verifies that the ID of the existing conversation's last message is returned.</p>
   */
  @Test
  void testFindOrCreateConversation() {
    // Arrange
    Message existingMessage = Message.builder().id(1L).sender(sender).receiver(receiver).item(item)
        .content("Conversation started about: Item 1").read(true).build();

    // Mock repository behavior
    when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
    when(messageRepository.findConversation(sender.getId(), receiver.getId(),
        item.getId())).thenReturn(java.util.Collections.singletonList(existingMessage));

    // Act
    Long conversationId = messageService.findOrCreateConversation(sender, item.getId());

    // Assert
    assertNotNull(conversationId);
    assertEquals(existingMessage.getId(), conversationId);
  }

  /**
   * <h3>Test Find Or Create Conversation - Item Not Found - Throws Exception</h3>
   * <p>Tests the scenario where the item specified in the request does not exist. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void testFindOrCreateConversation_ItemNotFound() {
    // Arrange
    when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class,
        () -> messageService.findOrCreateConversation(sender, item.getId()));
  }

  /**
   * <h3>Test Find Or Create Conversation - Self Conversation - Throws Exception</h3>
   * <p>Tests the scenario where a user attempts to start a conversation with themselves regarding their own item. It verifies that an {@link IllegalArgumentException} is thrown.</p>
   */
  @Test
  void testFindOrCreateConversation_SelfConversation() {
    // Arrange
    when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
    item.setSeller(sender);  // Setting the seller to the sender

    // Act & Assert
    assertThrows(IllegalArgumentException.class,
        () -> messageService.findOrCreateConversation(sender, item.getId()));
  }

  /**
   * <h3>Test Send WebSocket Message - Success</h3>
   * <p>Tests the successful sending of a WebSocket message. It verifies that the sender, receiver, and item exist, a new message is saved, and a WebSocket message is sent via {@link SimpMessagingTemplate}.</p>
   */
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
    WebSocketMessageDto messageDto =
        WebSocketMessageDto.builder().receiver(socketReceiver).item(socketItem)
            .messageContent("Hello!").build();

    when(userRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
    when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
    when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
    when(messageRepository.save(any(Message.class))).thenReturn(message);

    // Act
    messageService.sendWebSocketMessage(sender, messageDto);

    // Assert
    verify(messagingTemplate).convertAndSend(eq("/topic/messages/2"),
        any(MessageResponseDto.class));
  }

  /**
   * <h3>Test Send WebSocket Message - Item Not Found - Throws Exception</h3>
   * <p>Tests the scenario where the item specified in the WebSocket message DTO does not exist. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
  @Test
  void testSendWebSocketMessage_ItemNotFound() {
    WebSocketMessageDto.ReceiverDto socketReceiver = new WebSocketMessageDto.ReceiverDto();
    socketReceiver.setId(String.valueOf(2L));

    WebSocketMessageDto.ItemDto socketItem = new WebSocketMessageDto.ItemDto();
    socketItem.setId(String.valueOf(1L));
    // Arrange
    WebSocketMessageDto messageDto =
        WebSocketMessageDto.builder().receiver(socketReceiver).item(socketItem)
            .messageContent("Hello!").build();

    when(userRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
    when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
    when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class,
        () -> messageService.sendWebSocketMessage(sender, messageDto));
    verify(messagingTemplate, never()).convertAndSend(anyString(), Optional.ofNullable(any()));
  }

  /**
   * <h3>Test Get User Conversations - Empty Messages</h3>
   * <p>Tests the scenario where a user has no messages. It verifies that an empty list of {@link ConversationPreviewDto} is returned.</p>
   */
  @Test
  void testGetUserConversations_EmptyMessages() {
    // Arrange
    when(messageRepository.findAllByUserInvolved(sender.getId())).thenReturn(List.of());

    // Act
    List<ConversationPreviewDto> conversations = messageService.getUserConversations(sender);

    // Assert
    assertTrue(conversations.isEmpty());
  }

  /**
   * <h3>Test Get User Conversations - Valid Messages</h3>
   * <p>Tests the successful retrieval of conversations for a user. It verifies that the messages are fetched and mapped to a list of {@link ConversationPreviewDto} with the correct last message content.</p>
   */
  @Test
  void testGetUserConversations_ValidMessages() {
    // Arrange
    Message message =
        Message.builder().sender(sender).receiver(receiver).item(item).content("Hello!").read(true)
            .build();

    when(messageRepository.findAllByUserInvolved(sender.getId())).thenReturn(List.of(message));

    // Act
    List<ConversationPreviewDto> conversations = messageService.getUserConversations(sender);

    // Assert
    assertFalse(conversations.isEmpty());
    assertEquals("Hello!", conversations.getFirst().getLastMessage().getContent());
  }

  /**
   * <h3>Test Find Or Create Conversation - Seller Not Found - Throws Exception</h3>
   * <p>Tests the scenario where the seller of the item does not exist. It verifies that an {@link EntityNotFoundException} is thrown.</p>
   */
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