package nextstep;

public class CannotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public CannotFoundException(String message) {
        super(message);
    }

    public CannotFoundException() {
    }
}