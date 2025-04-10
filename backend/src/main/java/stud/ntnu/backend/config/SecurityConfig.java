package stud.ntnu.backend.config;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import stud.ntnu.backend.filter.JwtAuthFilter;
import stud.ntnu.backend.filter.LoggingFilter;

/**
 * <h2>SecurityConfig</h2>
 * <p>Central configuration class for Spring Security settings.</p>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  /**
   * <h3>UserDetailsService</h3>
   * <p>Service for loading user-specific data during authentication.</p>
   *
   * @see UserDetailsService
   */
  private final UserDetailsService userDetailsService;

  /**
   * <h3>JWT Authentication Filter</h3>
   * <p>Custom filter for processing JWT authentication tokens.</p>
   *
   * @see JwtAuthFilter
   */
  private final JwtAuthFilter jwtAuthFilter;

  private final LoggingFilter loggingFilter;

  /**
   * <h3>Password Encoder</h3>
   * <p>Provides BCrypt password encoding functionality.</p>
   *
   * @return configured {@link BCryptPasswordEncoder} instance
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * <h3>Authentication Provider</h3>
   * <p>Configures DAO authentication with user details service and password encoder.</p>
   *
   * @return configured {@link DaoAuthenticationProvider}
   */
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  /**
   * <h3>Authentication Manager</h3>
   * <p>Creates and configures the authentication manager.</p>
   *
   * @param authenticationConfiguration the authentication configuration
   * @return configured {@link AuthenticationManager}
   * @throws Exception if configuration fails
   */
  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  /**
   * <h3>Security Filter Chain</h3>
   * <p>Defines the security filter chain configuration.</p>
   *
   * @param http the HTTP security builder
   * @return configured {@link SecurityFilterChain}
   * @throws Exception if configuration fails
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable).sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth -> auth.requestMatchers("/auth/**", "/swagger-ui/**", "/swagger-ui.html",
                    "/v3/api-docs/**", "/ws", "/ws/**", "/topic/**", "/app/**", "/h2-console/**",
                    "/api/items/all", "/api/items/details/**", "/api/items/category/**",
                    "/api/items/search/**", "/api/category/**", "/actuator/health").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN").anyRequest().authenticated())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class);


    http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

    return http.build();
  }

  /**
   * <h3>CORS Configuration</h3>
   * <p>Configures Cross-Origin Resource Sharing settings.</p>
   *
   * @return configured {@link CorsConfigurationSource}
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(
        Arrays.asList("http://localhost:5173", "http://localhost:5174", "http://localhost:80",
            "http://localhost"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}