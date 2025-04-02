package stud.ntnu.backend.service;

   import java.util.List;
   import lombok.RequiredArgsConstructor;
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;
   import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
   import org.springframework.stereotype.Service;
   import stud.ntnu.backend.data.AuthRequestDto;
   import stud.ntnu.backend.data.UserRegistrationDto;
   import stud.ntnu.backend.model.User;
   import stud.ntnu.backend.repository.UserRepository;

   import java.util.Optional;

   /**
    * <h2> UserService for user-related business logic. </h2>
    */
   @Service
   @RequiredArgsConstructor
   public class UserService {

       /**
        * <h3> The UserRepository. </h3>
        */
       private final UserRepository userRepository;

     private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

       private static final Logger logger = LoggerFactory.getLogger(UserService.class);

     /**
      * <h3>Find all users in the repository.</h3>
      *
      * @return All users as list.
      */
       public List<User> findAll() {
         return userRepository.findAll();
       }

       /**
        * <h3> Find user by username. </h3>
        *
        * @param username The username of the user.
        * @return An Optional containing the user if found, or empty if not found.
        */
       public Optional<User> findByUsername(String username) {
           return userRepository.findByUsername(username);
       }

       /**
        * <h3> Get user by id. </h3>
        *
        * @param id The id of the user.
        * @return The user with the given id.
        */
       public UserRegistrationDto getUserById(Long id) {
           logger.info("fetching user with id: {} ", id);
           User user = userRepository.findById(id)
                   .orElseThrow(() -> new RuntimeException("User not found"));
           return mapToDto(user);
       }

       public User createUser(AuthRequestDto authRequestDto) {
         logger.info("creating user: {}", authRequestDto);
         User user = new User();
         user.setEmail(authRequestDto.getEmail());
         user.setPasswordHash(passwordEncoder.encode(authRequestDto.getPassword()));
         return userRepository.save(user);
       }

       /**
        * <h3> Map User entity to UserDto. </h3>
        *
        * @param user The User entity.
        * @return The UserDto.
        */
       private UserRegistrationDto mapToDto(User user) {
           UserRegistrationDto dto = new UserRegistrationDto();
           // Map user properties to DTO
           // (Implementation depends on your UserDto structure)
           return dto;
       }
   }