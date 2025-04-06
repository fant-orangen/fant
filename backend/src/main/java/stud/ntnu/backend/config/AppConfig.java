package stud.ntnu.backend.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h2>AppConfig</h2>
 * <p>Configuration class for application-wide beans and settings.</p>
 */
@Configuration
public class AppConfig {

  /**
   * <h3>Model Mapper</h3>
   * <p>Bean for object-to-object mapping operations between entities and DTOs.</p>
   *
   * @return the configured {@link ModelMapper} instance
   */
  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }
}