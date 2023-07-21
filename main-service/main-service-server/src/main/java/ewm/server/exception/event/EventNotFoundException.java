package ewm.server.exception.event;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(final String message) {
        super(message);
    }
}