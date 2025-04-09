package stud.ntnu.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <h2>BackendApplication</h2>
 * <p>Main application class that serves as the entry point for the Spring Boot marketplace backend.</p>
 * <p>This class initializes the Spring application context, enabling component scanning,
 * autoconfiguration, and web capabilities. It automatically detects and wires application
 * components in the stud.ntnu.backend package hierarchy.</p>
 */
@SpringBootApplication
public class BackendApplication {

  /**
   * <h3>Application Entry Point</h3>
   * <p>Main method that bootstraps and launches the Spring Boot application.</p>
   *
   * @param args command line arguments passed to the application
   */
  public static void main(String[] args) {
    SpringApplication.run(BackendApplication.class, args);
  }
}