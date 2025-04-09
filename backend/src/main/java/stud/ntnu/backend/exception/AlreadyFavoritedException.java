package stud.ntnu.backend.exception;

/**
 * <h2>AlreadyFavoritedException</h2>
 * <p>Exception thrown when a user attempts to favorite an item that is already in their favorites.</p>
 */
public class AlreadyFavoritedException extends RuntimeException {

  /**
   * <h3>Constructor</h3>
   * <p>Constructs a new {@code AlreadyFavoritedException} with the specified detail message.</p>
   *
   * @param message the detail message. The detail message is saved for later retrieval by the
   *                {@link #getMessage()} method.
   */
  public AlreadyFavoritedException(String message) {
    super(message);
  }
}