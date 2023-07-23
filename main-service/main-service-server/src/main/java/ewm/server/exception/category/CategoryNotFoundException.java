package ewm.server.exception.category;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException (final String message) {
        super(message);
    }

    @Override
    public String getLocalizedMessage() {
        return "Compilation was not found";
    }
}