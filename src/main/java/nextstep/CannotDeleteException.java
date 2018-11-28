package nextstep;

public class CannotDeleteException extends Exception {

    public static final String ALREADY_DELETED_EXCEPTION = "이미 삭제된 질문입니다.";
    public static final String HAS_ANSWERS_OF_OTHER_EXCEPTION = "다른사람의 답변이 있어 삭제할 수 없습니다.";

    private static final long serialVersionUID = 1L;

    public CannotDeleteException(String message) {
        super(message);
    }
}