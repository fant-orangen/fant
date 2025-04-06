package stud.ntnu.backend.exception;

public class AlreadyFavoritedException extends RuntimeException {
  public AlreadyFavoritedException(String message) {
    super(message);
  }
}
