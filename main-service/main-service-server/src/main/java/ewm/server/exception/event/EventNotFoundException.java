package ewm.server.exception.event;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(final String message) {
        super(message);
    }

    @Override
    public String getLocalizedMessage() {
        return "Event was not found";
    }
}