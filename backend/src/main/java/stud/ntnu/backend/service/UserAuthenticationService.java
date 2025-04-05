package stud.ntnu.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.UserRepository;
import java.util.Collections;

/**
 * <h2>UserAuthenticationService</h2>
 * <p>Service handling user authentication and authorization.</p>
 */
@Service
@RequiredArgsConstructor
public class UserAuthenticationService implements UserDetailsService {

  /**
   * <h3>User Repository</h3>
   * <p>Data access component for user accounts.</p>
   */
  private final UserRepository userRepository;

  /**
   * <h3>Load User By Username</h3>
   * <p>Authenticates users by email and provides their security context.</p>
   *
   * @param email the user's email address
   * @return Spring Security {@link UserDetails}
   * @throws UsernameNotFoundException if user not found
   */
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return new org.springframework.security.core.userdetails.User(
        user.getEmail(),
        user.getPasswordHash(),
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
    );
  }
}