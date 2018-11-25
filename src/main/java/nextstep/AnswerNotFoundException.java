package nextstep;

public class AnswerNotFoundException extends RuntimeException {
    public AnswerNotFoundException() {
        super("답변이 존재하지 않습니다.");
    }
}
