package stud.ntnu.backend.exception;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * <h2>GlobalExceptionHandler</h2>
 * <p>Centralized exception handler for REST controllers.</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * <h3>Handle Entity Not Found</h3>
   * <p>Handles cases when requested entities are not found.</p>
   *
   * @param ex the caught exception
   * @return 404 Not Found response with error message
   */
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  /**
   * <h3>Handle Resource Not Found</h3>
   * <p>Handles cases when requested resources don't exist.</p>
   *
   * @param ex the caught exception
   * @return 404 Not Found response with error message
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  /**
   * <h3>Handle Method Not Supported</h3>
   * <p>Handles invalid HTTP method usage.</p>
   *
   * @param ex the caught exception
   * @return 405 Method Not Allowed response with error message
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<String> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ex.getMessage());
  }

  /**
   * <h3>Handle Validation Errors</h3>
   * <p>Handles method parameter validation failures.</p>
   *
   * @param ex the caught exception
   * @return 400 Bad Request response with generic error message
   */
  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<String> handleHandlerMethodValidationException(
      HandlerMethodValidationException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request");
  }

  @ExceptionHandler(AlreadyFavoritedException.class)
  public ResponseEntity<String> handleAlreadyFavoritedException(AlreadyFavoritedException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  /**
   * <h3>Handle Unknown User</h3>
   * <p>Handles authentication failures due to unknown users.</p>
   *
   * @param ex the caught exception
   * @return 401 Unauthorized response with error message
   */
  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

  /**
   * <h3>Handle Bad Credentials</h3>
   * <p>Handles authentication failures due to invalid credentials.</p>
   *
   * @param ex the caught exception
   * @return 401 Unauthorized response with error message
   */
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

  /**
   * <h3>Handle Number Format Errors</h3>
   * <p>Handles invalid number format conversions.</p>
   *
   * @param ex the caught exception
   * @return 400 Bad Request response with error message
   */
  @ExceptionHandler(NumberFormatException.class)
  public ResponseEntity<String> handleNumberFormatException(NumberFormatException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
  }

  @ExceptionHandler(IOException.class)
  public ResponseEntity<String> handleIOException(IOException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request");
  }

  @ExceptionHandler(FileFormatException.class)
  public ResponseEntity<String> handleFileFormatException(FileFormatException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
  }

  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<String> handleMissingServletRequestPartException(
      MissingServletRequestPartException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  /**
   * <h3>Handle Unexpected Errors</h3>
   * <p>Catches all unhandled exceptions.</p>
   *
   * @param ex the caught exception
   * @return 500 Internal Server Error with generic message
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGeneralException(Exception ex) {
    log.error("Unexpected error", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An unexpected error occurred");
  }
}