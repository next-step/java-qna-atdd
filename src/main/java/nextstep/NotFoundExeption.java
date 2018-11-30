package nextstep;

public class NotFoundExeption extends Exception {
    private static final long serialVersionUID = 1L;

    public NotFoundExeption() {
        super();
    }

    public NotFoundExeption(String message) {
        super(message);
    }
}
