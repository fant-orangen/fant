package stud.ntnu.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "favorites", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "item_id"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(optional = false)
  @JoinColumn(name = "item_id")
  private Item item;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;
}

