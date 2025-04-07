package stud.ntnu.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "item_images")
public class Image {

  // Getters and setters
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(name = "image_url", nullable = false)
  private String url;

  @Setter
  @ManyToOne
  @JoinColumn(name = "item_id", nullable = false)
  private Item item;

}