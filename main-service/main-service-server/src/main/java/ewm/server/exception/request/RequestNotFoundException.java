package ewm.server.exception.request;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(final String message) {
        super(message);
    }
}