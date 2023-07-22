package ewm.server.exception.request;

public class IllegalRequestException extends RuntimeException {
    public IllegalRequestException(final String message) {
        super(message);
    }
}