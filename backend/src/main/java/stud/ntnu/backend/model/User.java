package stud.ntnu.backend.model;

  import com.fasterxml.jackson.annotation.JsonIgnore;
  import jakarta.persistence.*;
  import jakarta.validation.constraints.Email;
  import lombok.AllArgsConstructor;
  import lombok.Data;
  import lombok.NoArgsConstructor;

  import java.time.LocalDateTime;
  import org.hibernate.annotations.CreationTimestamp;
  import org.hibernate.annotations.UpdateTimestamp;

@Entity
  @Table(name = "users")
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public class User {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;

      @Column(nullable = false, unique = true)
      @Email(message = "Email should be valid")
      private String email;  // Primary identifier

      @JsonIgnore
      @Column(nullable = false, name = "password_hash")
      private String passwordHash;

      @Column(nullable = false, name = "display_name")
      private String displayName;

      @Enumerated(EnumType.STRING)
      private Role role;

      @Column(name = "first_name")
      private String firstName;

      @Column(name = "last_name")
      private String lastName;

      private String phone;

      @CreationTimestamp
      @Column(name = "created_at")
      private LocalDateTime createdAt;

      @UpdateTimestamp
      @Column(name = "updated_at")
      private LocalDateTime updatedAt;
  }