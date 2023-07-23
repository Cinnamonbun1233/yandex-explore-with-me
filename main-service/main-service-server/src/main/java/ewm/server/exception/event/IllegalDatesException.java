package ewm.server.exception.event;

public class IllegalDatesException extends RuntimeException {
    public IllegalDatesException(final String message) {
        super(message);
    }
    @Override
    public String getLocalizedMessage() {
        return "Wrong dates were provided";
    }
}