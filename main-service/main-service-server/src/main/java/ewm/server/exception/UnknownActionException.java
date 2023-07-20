package ewm.server.exception;

public class UnknownActionException extends RuntimeException {
    public UnknownActionException(final String message) {
        super(message);
    }
}