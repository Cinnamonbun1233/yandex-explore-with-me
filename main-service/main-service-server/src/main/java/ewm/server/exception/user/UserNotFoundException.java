package ewm.server.exception.user;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(final String message) {
        super(message);
    }
    @Override
    public String getLocalizedMessage() {
        return "User was not found";
    }
}