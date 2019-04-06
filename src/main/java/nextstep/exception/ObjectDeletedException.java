package nextstep.exception;

public class ObjectDeletedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ObjectDeletedException() {
        super();
    }

    public ObjectDeletedException(String message) {
        super(message);
    }
}