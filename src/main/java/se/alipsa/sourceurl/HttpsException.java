package se.alipsa.sourceurl;

/**
 * Represents HTTPS problems
 */
public class HttpsException extends Exception {
  public HttpsException() {
    super();
  }

  public HttpsException(String message) {
    super(message);
  }

  public HttpsException(String message, Throwable cause) {
    super(message, cause);
  }

  public HttpsException(Throwable cause) {
    super(cause);
  }
}
