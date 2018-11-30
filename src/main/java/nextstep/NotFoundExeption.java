package nextstep;

public class NotFoundExeption extends RuntimeException { //사용자의 잘못 된 사용은 Runtime으로 처리하는 것이 맞음.
    private static final long serialVersionUID = 1L;

    public NotFoundExeption() {
        super();
    }

    public NotFoundExeption(String message) {
        super(message);
    }
}
