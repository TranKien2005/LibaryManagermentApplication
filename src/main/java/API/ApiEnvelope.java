package API;

/**
 * Simple envelope matching server responses: { success, message, data }
 */
public class ApiEnvelope<T> {
    public boolean success;
    public String message;
    public T data;
}
