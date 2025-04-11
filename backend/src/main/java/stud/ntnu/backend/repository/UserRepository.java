package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import stud.ntnu.backend.model.User;

/**
 * <h2>UserRepository</h2>
 * <p>Repository interface for accessing and managing {@link User} entities in the database.</p>
 * <p>Extends Spring Data JPA's {@link JpaRepository} to provide basic CRUD operations and allows
 * for the definition of custom query methods related to user accounts.</p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * <h3>Find By Email</h3>
   * <p>Retrieves a {@link User} entity from the database based on their unique email address.</p>
   *
   * @param email the email address of the user to search for. Email addresses are expected to be
   *              unique within the system.
   * @return an {@link Optional} containing the matching {@link User} entity if a user with the
   * given email address exists; otherwise, an empty {@code Optional}.
   */
  Optional<User> findByEmail(String email);

  /**
   * <h3>Exists By Email</h3>
   * <p>Checks if a {@link User} entity with the given email address exists in the database.</p>
   *
   * @param mail the email address to check for existence. Email addresses are expected to be unique
   *             within the system.
   * @return {@code true} if a user with the given email address exists; {@code false} otherwise.
   */
  boolean existsByEmail(String mail);
}