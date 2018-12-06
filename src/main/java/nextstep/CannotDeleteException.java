package nextstep;

public class CannotDeleteException extends Exception {

    public static final String AUTHORITY_EXCEPTION = "삭제 권한이 없습니다.";
    public static final String ALREADY_DELETED_EXCEPTION = "이미 삭제된 질문입니다.";
    public static final String EXISTED_ANOTHER_USER_ANSWER_EXCEPTION = "다른 사용자의 답변이 존재해 삭제할 수 없습니다.";

    public CannotDeleteException(String message) {
        super(message);
    }
}
