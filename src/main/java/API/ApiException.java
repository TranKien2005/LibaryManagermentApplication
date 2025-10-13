package API;

/**
 * Simple runtime exception to represent API-level failures communicated by server envelope.
 */
public class ApiException extends RuntimeException {
    private final int statusCode; // optional, keep for future

    public ApiException(String message) {
        super(message);
        this.statusCode = -1;
    }

    public ApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
