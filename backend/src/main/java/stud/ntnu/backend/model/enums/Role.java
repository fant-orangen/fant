package stud.ntnu.backend.model.enums;

/**
 * <h2>Role</h2>
 * <p>Enumeration representing the different roles that a {@code User} can have within the system.</p>
 * <p>These roles define the level of access and permissions granted to a user.</p>
 */
public enum Role {

  /**
   * <h3>USER</h3>
   * <p>Represents a standard, authenticated user of the marketplace platform. Users with this role
   * typically have access to basic functionalities such as browsing items, making purchases,
   * listing items for sale, and managing their own profile.</p>
   */
  USER,

  /**
   * <h3>ADMIN</h3>
   * <p>Represents an administrator with elevated privileges. Users with this role have access to
   * administrative functions, which may include managing users, categories, system settings, and
   * potentially moderating content.</p>
   */
  ADMIN
}