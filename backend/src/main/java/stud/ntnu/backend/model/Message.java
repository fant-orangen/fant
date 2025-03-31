package stud.ntnu.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "sender_id")
  private User sender;

  @ManyToOne(optional = false)
  @JoinColumn(name = "receiver_id")
  private User receiver;

  @ManyToOne
  @JoinColumn(name = "item_id")
  private Item item;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(nullable = false, updatable = false)
  private LocalDateTime sentAt;

  @PrePersist
  void onSend() {
    sentAt = LocalDateTime.now();
  }
}

