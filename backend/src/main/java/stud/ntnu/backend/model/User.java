package stud.ntnu.backend.model;

import jakarta.persistence.*;
import lombok.*;

  @Entity
  @Table(name = "users")
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  private String firstName;
  private String lastName;
  private String phone;

  @Column(nullable = false, updatable = false)
  private java.time.LocalDateTime createdAt;

  @Column(nullable = false)
  private java.time.LocalDateTime updatedAt;

  @PrePersist
  void onCreate() {
    createdAt = updatedAt = java.time.LocalDateTime.now();
  }

  @PreUpdate
  void onUpdate() {
    updatedAt = java.time.LocalDateTime.now();
  }
}
