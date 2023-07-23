package ewm.server.exception.request;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(final String message) {
        super(message);
    }

    @Override
    public String getLocalizedMessage() {
        return "Request was not found";
    }
}