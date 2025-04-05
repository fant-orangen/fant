package stud.ntnu.backend.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2>MessageUserDto</h2>
 * <p>Data Transfer Object representing a user in message contexts.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageUserDto {

  private Long id;
  private String displayName;
  private String email;
}