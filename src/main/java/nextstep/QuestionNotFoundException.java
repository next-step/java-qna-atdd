package nextstep;

public class QuestionNotFoundException extends RuntimeException {
    public QuestionNotFoundException() {
        super("질문이 존재하지 않습니다.");
    }
}
