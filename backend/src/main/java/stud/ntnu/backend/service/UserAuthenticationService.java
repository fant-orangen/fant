package stud.ntnu.backend.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.UserRepository;

import java.util.Collections;

/**
 * <h2>UserAuthenticationService</h2>
 * <p>Service responsible for authenticating users by loading their details from the database.</p>
 * <p>Implements Spring Security's {@link UserDetailsService} interface to integrate
 * with the authentication framework.</p>
 */
@Service
@RequiredArgsConstructor
public class UserAuthenticationService implements UserDetailsService {

    /**
     * <h3>User repository for accessing user data</h3>
     */
    private final UserRepository userRepository;

    /**
     * <h3>Password encoder for hashing and verification</h3>
     */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * <h3>Logger for diagnostic information</h3>
     */
    Logger logger = LoggerFactory.getLogger(UserAuthenticationService.class); // TODO: remove logger?

    /**
     * <h3>Loads a user's details by username</h3>
     * <p>Retrieves a user from the database and converts it to Spring Security's UserDetails object.</p>
     *
     * @param username the username identifying the user whose data is required
     * @return a fully populated UserDetails object
     * @throws UsernameNotFoundException if the user could not be found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        String passwordHash = passwordEncoder.encode("password123");
        logger.info("Hash of password123: " + passwordHash);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}