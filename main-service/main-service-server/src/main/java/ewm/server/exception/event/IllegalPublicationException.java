package ewm.server.exception.event;

public class IllegalPublicationException extends RuntimeException {
    public IllegalPublicationException(final String message) {
        super(message);
    }
}