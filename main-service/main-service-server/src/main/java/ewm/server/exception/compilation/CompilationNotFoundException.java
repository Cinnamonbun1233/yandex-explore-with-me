package ewm.server.exception.compilation;

public class CompilationNotFoundException extends RuntimeException {
    public CompilationNotFoundException(final String message) {
        super(message);
    }

    @Override
    public String getLocalizedMessage() {
        return "Compilation was not found";
    }
}