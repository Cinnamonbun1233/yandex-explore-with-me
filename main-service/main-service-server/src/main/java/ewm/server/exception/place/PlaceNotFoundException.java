package ewm.server.exception.place;

public class PlaceNotFoundException extends RuntimeException {
    public PlaceNotFoundException(final String message) {
        super(message);
    }

    @Override
    public String getLocalizedMessage() {
        return "Place was not found";
    }
}