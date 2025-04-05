package stud.ntnu.backend.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebSocketMessageDto {

  private String id;
  private ReceiverDto receiver;
  private ItemDto item;
  private String messageContent;
  private Date sentDate;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ReceiverDto {

    private String id;
    private String username;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ItemDto {

    private String id;
    private String title;
  }
}