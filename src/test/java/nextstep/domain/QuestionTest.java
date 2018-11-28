package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");


    public static final Question newQuestion() {
        return Question.ofUser("지질하다", "내용이 엉?", JAVAJIGI);
    }

    @Test
    public void 질문하기() {
        Question question = newQuestion();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문하기_로그아웃() {
        Question question = newQuestion();
        question.writeBy(null);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문하기_손님() {
        Question question = newQuestion();
        question.writeBy(User.GUEST_USER);
    }

    @Test
    public void 질문업데이트() {
        Question question = newQuestion();
        Question updateQuestion = Question.of("제목 이상하게하기", "내용삽입");
        question.update(JAVAJIGI, updateQuestion);
        softly.assertThat(question.getTitle()).isEqualTo(updateQuestion.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(updateQuestion.getContents());

    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문타인업데이트() {
        Question question = newQuestion();
        Question updateQuestion = Question.of("제목 이상하게하기", "내용삽입");
        question.update(SANJIGI, updateQuestion);
        softly.assertThat(question.getTitle()).isEqualTo(updateQuestion.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(updateQuestion.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문손님업데이트() {
        Question question = newQuestion();
        Question updateQuestion = Question.of("제목 이상하게하기", "내용삽입");
        question.update(User.GUEST_USER, updateQuestion);
        softly.assertThat(question.getTitle()).isEqualTo(updateQuestion.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(updateQuestion.getContents());
    }

    @Test
    public void 삭제하기() throws CannotDeleteException {
        Question question = newQuestion();
        question.delete(JAVAJIGI);
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void 삭제하기타인() throws CannotDeleteException {
        Question question = newQuestion();
        question.delete(SANJIGI);
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void 삭제하기손님() throws CannotDeleteException {
        Question question = newQuestion();
        question.delete(User.GUEST_USER);
        softly.assertThat(question.isDeleted()).isTrue();
    }


}