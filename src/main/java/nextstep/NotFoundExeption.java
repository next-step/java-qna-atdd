package nextstep;

public class NotFoundExeption extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotFoundExeption() {
        super();
    }

    public NotFoundExeption(String message) {
        super(message);
    }
}
