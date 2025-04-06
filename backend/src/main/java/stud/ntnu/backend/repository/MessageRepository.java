package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.model.Message;
import java.util.List;

/**
 * <h2>MessageRepository</h2>
 * <p>Repository for message data operations and conversation management.</p>
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

  /**
   * <h3>Find All By User Involved</h3>
   * <p>Retrieves all messages where the user is either sender or receiver.</p>
   *
   * @param userId the ID of the user
   * @return list of {@link Message} entities ordered by sent time (newest first)
   */
  @Query("SELECT m FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId ORDER BY m.sentAt DESC")
  List<Message> findAllByUserInvolved(@Param("userId") Long userId);

  /**
   * <h3>Find Conversation</h3>
   * <p>Retrieves message history between two users about a specific item.</p>
   *
   * @param user1Id the ID of the first user
   * @param user2Id the ID of the second user
   * @param itemId  the ID of the related item
   * @return list of {@link Message} entities ordered chronologically
   */
  @Query("SELECT m FROM Message m WHERE " +
      "((m.sender.id = :user1Id AND m.receiver.id = :user2Id) OR " +
      "(m.sender.id = :user2Id AND m.receiver.id = :user1Id)) AND " +
      "m.item.id = :itemId ORDER BY m.sentAt")
  List<Message> findConversation(@Param("user1Id") Long user1Id,
                                 @Param("user2Id") Long user2Id,
                                 @Param("itemId") Long itemId);

  /**
   * <h3>Mark Messages as Read</h3>
   * <p>Updates the read status of multiple messages to true.</p>
   * <p>Only updates messages where the specified user is the recipient.</p>
   *
   * @param messageIds list of message IDs to update
   * @param userId     ID of the recipient user
   */
  @Modifying
  @Query("UPDATE Message m SET m.read = true WHERE m.id IN :messageIds AND m.receiver.id = :userId AND m.read = false")
  void markMessagesAsRead(@Param("messageIds") List<Long> messageIds, @Param("userId") Long userId);
}