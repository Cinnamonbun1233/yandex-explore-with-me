package ewm.server.exception.event;

public class UnknownActionException extends RuntimeException {
    public UnknownActionException(final String message) {
        super(message);
    }
}