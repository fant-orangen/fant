package stud.ntnu.backend.exception;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * <h2>GlobalExceptionHandler</h2>
 * <p>Centralized exception handler for REST controllers.</p>
 * <p>This class provides methods to handle various exceptions that might occur during the processing
 * of REST requests, returning appropriate HTTP status codes and error messages.</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * <h3>Handle Entity Not Found</h3>
   * <p>Handles {@link EntityNotFoundException} which occurs when requested entities are not found.</p>
   *
   * @param ex the caught {@code EntityNotFoundException}
   * @return {@code ResponseEntity} with 404 Not Found status and the exception message as the body.
   */
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  /**
   * <h3>Handle Resource Not Found</h3>
   * <p>Handles {@link NoResourceFoundException} which occurs when requested resources do not exist.</p>
   *
   * @param ex the caught {@code NoResourceFoundException}
   * @return {@code ResponseEntity} with 404 Not Found status and the exception message as the body.
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  /**
   * <h3>Handle Method Not Supported</h3>
   * <p>Handles {@link HttpRequestMethodNotSupportedException} which occurs when an invalid HTTP
   * method is used for a particular endpoint.</p>
   *
   * @param ex the caught {@code HttpRequestMethodNotSupportedException}
   * @return {@code ResponseEntity} with 405 Method Not Allowed status and the exception message as
   * the body.
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<String> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ex.getMessage());
  }

  /**
   * <h3>Handle Validation Errors</h3>
   * <p>Handles {@link HandlerMethodValidationException} which occurs during handler method
   * argument validation failures.</p>
   *
   * @param ex the caught {@code HandlerMethodValidationException}
   * @return {@code ResponseEntity} with 400 Bad Request status and a generic "Invalid request"
   * message as the body.
   */
  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<String> handleHandlerMethodValidationException(
      HandlerMethodValidationException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  /**
   * <h3>Handle Already Favorited Exception</h3>
   * <p>Handles {@link AlreadyFavoritedException} which occurs when a user tries to favorite an item
   * that is already in their favorites.</p>
   *
   * @param ex the caught {@code AlreadyFavoritedException}
   * @return {@code ResponseEntity} with 400 Bad Request status and the exception message as the
   * body.
   */
  @ExceptionHandler(AlreadyFavoritedException.class)
  public ResponseEntity<String> handleAlreadyFavoritedException(AlreadyFavoritedException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  /**
   * <h3>Handle Unknown User</h3>
   * <p>Handles {@link UsernameNotFoundException} which occurs during authentication when the user
   * is not found.</p>
   *
   * @param ex the caught {@code UsernameNotFoundException}
   * @return {@code ResponseEntity} with 401 Unauthorized status and the exception message as the
   * body.
   */
  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

  /**
   * <h3>Handle Bad Credentials</h3>
   * <p>Handles {@link BadCredentialsException} which occurs during authentication when the provided
   * credentials are invalid.</p>
   *
   * @param ex the caught {@code BadCredentialsException}
   * @return {@code ResponseEntity} with 401 Unauthorized status and the exception message as the
   * body.
   */
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

  /**
   * <h3>Handle Number Format Errors</h3>
   * <p>Handles {@link NumberFormatException} which occurs when a string cannot be converted to a
   * number.</p>
   *
   * @param ex the caught {@code NumberFormatException}
   * @return {@code ResponseEntity} with 400 Bad Request status and the exception message as the
   * body.
   */
  @ExceptionHandler(NumberFormatException.class)
  public ResponseEntity<String> handleNumberFormatException(NumberFormatException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  /**
   * <h3>Handle Access Denied</h3>
   * <p>Handles {@link AccessDeniedException} which occurs when the user does not have the necessary
   * permissions to access a resource.</p>
   *
   * @param ex the caught {@code AccessDeniedException}
   * @return {@code ResponseEntity} with 403 Forbidden status and the exception message as the
   * body.
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
  }

  /**
   * <h3>Handle IO Exceptions</h3>
   * <p>Handles {@link IOException} which might occur during input/output operations, such as file
   * processing.</p>
   *
   * @param ex the caught {@code IOException}
   * @return {@code ResponseEntity} with 400 Bad Request status and a generic "Invalid file" message
   * as the body.
   */
  @ExceptionHandler(IOException.class)
  public ResponseEntity<String> handleIOException(IOException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file");
  }

  /**
   * <h3>Handle Method Argument Not Valid</h3>
   * <p>Handles {@link MethodArgumentNotValidException} which occurs when method arguments annotated
   * with {@code @Valid} fail validation.</p>
   *
   * @param ex the caught {@code MethodArgumentNotValidException}
   * @return {@code ResponseEntity} with 400 Bad Request status and a generic "Invalid request"
   * message as the body.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request");
  }

  /**
   * <h3>Handle File Format Exception</h3>
   * <p>Handles {@link FileFormatException} which occurs when a file is in an unexpected or
   * unsupported format.</p>
   *
   * @param ex the caught {@code FileFormatException}
   * @return {@code ResponseEntity} with 400 Bad Request status and the exception message as the
   * body.
   */
  @ExceptionHandler(FileFormatException.class)
  public ResponseEntity<String> handleFileFormatException(FileFormatException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  /**
   * <h3>Handle Runtime Exception</h3>
   * <p>Handles generic {@link RuntimeException} that are not specifically caught elsewhere.</p>
   *
   * @param ex the caught {@code RuntimeException}
   * @return {@code ResponseEntity} with 500 Internal Server Error status with a generic response.
   */
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An unexpected error occurred");
  }

  /**
   * <h3>Handle Missing Servlet Request Part Exception</h3>
   * <p>Handles {@link MissingServletRequestPartException} which occurs when a required part of a
   * multipart request is missing.</p>
   *
   * @param ex the caught {@code MissingServletRequestPartException}
   * @return {@code ResponseEntity} with 400 Bad Request status and the exception message as the
   * body.
   */
  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<String> handleMissingServletRequestPartException(
      MissingServletRequestPartException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  /**
   * <h3>Handle Illegal State Exception</h3>
   * <p>Handles {@link IllegalStateException} which signals that a method has been invoked at an
   * inappropriate time.</p>
   *
   * @param ex the caught {@code IllegalStateException}
   * @return {@code ResponseEntity} with 400 Bad Request status and the exception message as the
   * body.
   */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  /**
   * <h3>Handle Unexpected Errors</h3>
   * <p>Catches all unhandled {@link Exception} instances.</p>
   *
   * @param ex the caught {@code Exception}
   * @return {@code ResponseEntity} with 500 Internal Server Error status and a generic "An
   * unexpected error occurred" message as the body. The full error is logged for debugging
   * purposes.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGeneralException(Exception ex) {
    log.error("Unexpected error", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An unexpected error occurred");
  }
}