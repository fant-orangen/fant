package stud.ntnu.backend.exception;

/**
 * <h2>FileFormatException</h2>
 * <p>Exception thrown when a file is in an unexpected or unsupported format.</p>
 */
public class FileFormatException extends RuntimeException {

  /**
   * <h3>Constructor</h3>
   * <p>Constructs a new {@code FileFormatException} with the specified detail message.</p>
   *
   * @param message the detail message. The detail message is saved for later retrieval by the
   *                {@link #getMessage()} method.
   */
  public FileFormatException(String message) {
    super(message);
  }
}