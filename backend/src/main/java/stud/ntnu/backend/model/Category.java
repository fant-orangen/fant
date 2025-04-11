package stud.ntnu.backend.model;

import jakarta.persistence.*;
import lombok.*; // Import individual annotations
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "categories")
// Replace @Data with individual annotations
@Getter // Generate getters
@Setter // Generate setters
@NoArgsConstructor // Generate no-args constructor
@AllArgsConstructor // Generate all-args constructor
@Builder // Keep builder if you use it
@ToString(exclude = {"parent"}) // Exclude parent from toString to avoid potential recursion
@EqualsAndHashCode(exclude = {"parent"}) // Exclude parent from equals/hashCode to avoid issues with cycles/lazy loading
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(name = "image_url")
  private String imageUrl;

  @ManyToOne(fetch = FetchType.LAZY) // Consider LAZY fetching for parent
  @JoinColumn(name = "parent_id")
  @OnDelete(action = OnDeleteAction.SET_NULL)
  private Category parent;
}