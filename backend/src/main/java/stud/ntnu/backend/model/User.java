package stud.ntnu.backend.model;

  import jakarta.persistence.*;
  import lombok.AllArgsConstructor;
  import lombok.Data;
  import lombok.NoArgsConstructor;

  import java.time.LocalDateTime;

  @Entity
  @Table(name = "users")
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public class User {

      @Id
      private String email;

      @Column(name = "password_hash")
      private String passwordHash;

      @Enumerated(EnumType.STRING)
      private Role role;

      @Column(name = "first_name")
      private String firstName;

      @Column(name = "last_name")
      private String lastName;

      private String username;

      private String phone;

      @Column(name = "created_at")
      private LocalDateTime createdAt;

      @Column(name = "updated_at")
      private LocalDateTime updatedAt;
  }