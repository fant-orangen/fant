package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

  /**
   * Find all messages where the specified user is either sender or receiver.
   *
   * @param userId The ID of the user
   * @return List of messages involving the specified user
   */
  @Query("SELECT m FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId ORDER BY m.sentAt DESC")
  List<Message> findAllByUserInvolved(@Param("userId") Long userId);

  /**
   * Find all messages between two users about a specific item.
   *
   * @param user1Id The ID of the first user
   * @param user2Id The ID of the second user
   * @param itemId  The ID of the item
   * @return List of messages between the users about the specified item
   */
  @Query("SELECT m FROM Message m WHERE " +
      "((m.sender.id = :user1Id AND m.receiver.id = :user2Id) OR " +
      "(m.sender.id = :user2Id AND m.receiver.id = :user1Id)) AND " +
      "m.item.id = :itemId ORDER BY m.sentAt")
  List<Message> findConversation(@Param("user1Id") Long user1Id,
      @Param("user2Id") Long user2Id,
      @Param("itemId") Long itemId);
}