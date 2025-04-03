package stud.ntnu.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.Message;
import stud.ntnu.backend.model.User;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
  List<Message> findMessagesBySenderAndReceiverOrReceiverAndSenderOrderBySentAt(User sender,
                                                                                User receiver,
                                                                                User receiver1,
                                                                                User sender1);
}
