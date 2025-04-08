package stud.ntnu.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import stud.ntnu.backend.model.enums.Role;

/**
 * <h2>User</h2>
 * <p>Entity representing a registered user in the marketplace system.</p>
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  /**
   * <h3>ID</h3>
   * <p>Unique identifier for the user.</p>
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * <h3>Email</h3>
   * <p>User's unique email address (primary identifier).</p>
   */
  @Column(nullable = false, unique = true)
  @Email(message = "Email should be valid")
  private String email;

  /**
   * <h3>Password Hash</h3>
   * <p>Hashed version of user's password (never exposed in responses).</p>
   */
  @JsonIgnore
  @Column(nullable = false, name = "password_hash")
  private String passwordHash;

  /**
   * <h3>Display Name</h3>
   * <p>User's public display name (required).</p>
   */
  @Column(nullable = false, name = "display_name")
  private String displayName;

  /**
   * <h3>Role</h3>
   * <p>User's system role/permissions level.</p>
   */
  @Enumerated(EnumType.STRING)
  private Role role;

  /**
   * <h3>First Name</h3>
   * <p>User's legal first name (optional).</p>
   */
  @Column(name = "first_name")
  private String firstName;

  /**
   * <h3>Last Name</h3>
   * <p>User's legal last name (optional).</p>
   */
  @Column(name = "last_name")
  private String lastName;

  /**
   * <h3>Phone</h3>
   * <p>User's contact phone number (optional).</p>
   */
  private String phone;

  /**
   * <h3>Created At</h3>
   * <p>Timestamp when user account was created (auto-generated).</p>
   */
  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  /**
   * <h3>Updated At</h3>
   * <p>Timestamp when user account was last modified (auto-updated).</p>
   */
  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}