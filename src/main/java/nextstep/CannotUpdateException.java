package nextstep;

public class CannotUpdateException extends Exception {
    private static final long serialVersionUID = 1L;

    public CannotUpdateException(String message) {
        super(message);
    }
}
