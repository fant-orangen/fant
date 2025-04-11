package stud.ntnu.backend.data.message;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2>MessageReadRequestDto</h2>
 * <p>Data transfer object for marking multiple messages as read in a single request.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageReadRequestDto {

  /**
   * <h3>Message IDs</h3>
   * <p>List of unique identifiers for the messages to be marked as read.</p>
   */
  private List<Long> messageIds;
}