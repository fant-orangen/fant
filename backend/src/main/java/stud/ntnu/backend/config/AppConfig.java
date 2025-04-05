package stud.ntnu.backend.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Central configuration class for application-wide beans and settings.
 * <p>
 * This class defines Spring Beans that are used throughout the application.
 * Currently, configures the {@link ModelMapper} for object mapping operations.
 * </p>
 *
 * @see ModelMapper
 */
@Configuration
public class AppConfig {

  /**
   * Creates and configures a {@link ModelMapper} instance for object-to-object mapping.
   * <p>
   * The provided {@link ModelMapper} is used to simplify conversion between:
   * - Entity objects and DTOs
   * - Different object models
   * </p>
   *
   * @return a fully configured {@link ModelMapper} instance
   */
  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }
}