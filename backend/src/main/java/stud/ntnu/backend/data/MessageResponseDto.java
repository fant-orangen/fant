package stud.ntnu.backend.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponseDto {

  private Long id;
  private MessageUserDto sender;
  private MessageUserDto receiver;
  private ItemReferenceDto item;
  private String messageContent;
  private LocalDateTime sentDate;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ItemReferenceDto {

    private Long id;
    private String title;
  }
}