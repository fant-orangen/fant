package stud.ntnu.backend.service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.data.MessageRequestDto;
import stud.ntnu.backend.data.MessageResponseDto;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.Message;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.ItemRepository;
import stud.ntnu.backend.repository.MessageRepository;
import stud.ntnu.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class MessageService {
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ItemRepository itemRepository;

  public MessageResponseDto sendMessage(MessageRequestDto messageRequestDto, User sender) {
    Message message = new Message();
    message.setSender(sender);
    message.setContent(messageRequestDto.getContent());
    message.setReceiver(userRepository.findById(messageRequestDto.getReceiverId()).orElseThrow());

    if (messageRequestDto.getItemId() != null) {
      message.setItem(itemRepository.findById(messageRequestDto.getItemId()).orElseThrow());
    }
    return messageResponseDto(messageRepository.save(message));
  }

  public List<MessageResponseDto> getConversation(User user, User user1) {
    return mapMessagesToResponse(
        messageRepository.findMessagesBySenderAndReceiverOrReceiverAndSenderOrderBySentAt(user,
            user1, user, user1));
  }

  private MessageResponseDto messageResponseDto(Message message) {
    MessageResponseDto response = new MessageResponseDto();
    response.setSenderId(message.getSender().getId());
    response.setReceiverId(message.getReceiver().getId());
    response.setContent(message.getContent());
    response.setSentAt(message.getSentAt());

    if (message.getItem() != null) {
      response.setItemId(message.getItem().getId());
    }
    return response;
  }

  private List<MessageResponseDto> mapMessagesToResponse(List<Message> messages) {
    return messages.stream().map(this::messageResponseDto).collect(Collectors.toList());
  }
}