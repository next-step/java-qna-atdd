package nextstep;

public class CannotUpdateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CannotUpdateException(final String message) {
        super(message);
    }
    
}
