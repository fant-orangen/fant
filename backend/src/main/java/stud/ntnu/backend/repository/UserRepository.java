package stud.ntnu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import stud.ntnu.backend.model.User;

/**
 * <h2>UserRepository</h2>
 * <p>Repository for user account data operations.</p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * <h3>Find By Email</h3>
   * <p>Retrieves a user by their unique email address.</p>
   *
   * @param email the email address to search for
   * @return {@link Optional} containing the matching {@link User} if found
   */
  Optional<User> findByEmail(String email);

  boolean existsByEmail(String mail);
}