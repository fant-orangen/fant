package stud.ntnu.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * <p>Repository interface for accessing and managing {@link Message} entities in the database.</p>
 * <p>Extends Spring Data JPA's {@link JpaRepository} to provide basic CRUD operations and defines
 * custom query methods for retrieving messages based on user involvement, item context, and marking
 * messages as read.</p>
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

  /**
   * <h3>Find All By User Involved</h3>
   * <p>Retrieves all {@link Message} entities where a specific user is either the sender or the
   * receiver.</p>
   *
   * @param userId the unique identifier of the {@link stud.ntnu.backend.model.User}.
   * @return a {@link List} of {@link Message} entities in which the specified user participated,
   * ordered by the time they were sent, with the newest messages appearing first.
   */
  @Query("SELECT m FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId ORDER BY m.sentAt DESC")
  List<Message> findAllByUserInvolved(@Param("userId") Long userId);

  /**
   * <h3>Find By User Involved And Item ID (Paged)</h3>
   * <p>Retrieves a paginated list of {@link Message} entities related to a specific item where a
   * given user is either the sender or the receiver.</p>
   *
   * @param userId   the unique identifier of the {@link stud.ntnu.backend.model.User}.
   * @param itemId   the unique identifier of the {@link stud.ntnu.backend.model.Item} the messages
   *                 are about.
   * @param pageable the pagination information, including page number, size, and sort order.
   * @return a {@link Page} of {@link Message} entities involving the specified user and item,
   * ordered by the time they were sent, with the newest messages appearing first, according to the
   * provided pagination.
   */
  @Query("SELECT m FROM Message m WHERE m.item.id = :itemId AND (m.sender.id = :userId OR m.receiver.id = :userId) ORDER BY m.sentAt DESC")
  Page<Message> findByUserInvolvedAndItemId(@Param("userId") Long userId,
                                            @Param("itemId") Long itemId, Pageable pageable);

  /**
   * <h3>Find Conversation</h3>
   * <p>Retrieves the message history between two specific users regarding a particular item.</p>
   * <p>The messages are ordered chronologically based on their sent time.</p>
   *
   * @param user1Id the unique identifier of the first {@link stud.ntnu.backend.model.User} in the
   *                conversation.
   * @param user2Id the unique identifier of the second {@link stud.ntnu.backend.model.User} in the
   *                conversation.
   * @param itemId  the unique identifier of the {@link stud.ntnu.backend.model.Item} that the
   *                conversation is about.
   * @return a {@link List} of {@link Message} entities exchanged between the two users about the
   * specified item, ordered by the time they were sent (oldest first).
   */
  @Query("SELECT m FROM Message m WHERE " +
      "((m.sender.id = :user1Id AND m.receiver.id = :user2Id) OR " +
      "(m.sender.id = :user2Id AND m.receiver.id = :user1Id)) AND " +
      "m.item.id = :itemId ORDER BY m.sentAt")
  List<Message> findConversation(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id,
                                 @Param("itemId") Long itemId);

  /**
   * <h3>Mark Messages as Read</h3>
   * <p>Updates the {@code read} status to {@code true} for a list of messages where a specific user
   * is the recipient and the message is currently unread.</p>
   * <p>This operation is transactional.</p>
   *
   * @param messageIds a {@link List} of unique identifiers of the {@link Message} entities to mark
   *                   as read.
   * @param userId     the unique identifier of the recipient {@link stud.ntnu.backend.model.User}.
   */
  @Modifying(clearAutomatically = true)
  @Transactional
  @Query("UPDATE Message m SET m.read = true WHERE m.id IN :messageIds AND m.receiver.id = :userId AND m.read = false")
  void markMessagesAsRead(@Param("messageIds") List<Long> messageIds, @Param("userId") Long userId);
}