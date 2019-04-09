package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

public class QuestionTest extends BaseTest {
    public static final Question JAVA_QUESTION = new Question(1L, "자바 제목", "자바 내용", UserTest.JAVAJIGI, false);
    public static final Question SAN_QUESTION = new Question(2L, "산 제목", "산 내용", UserTest.SANJIGI, true);

    public static Question newQuestion(long id) {
        return new Question(id, "자바 제목", "자바 내용", UserTest.JAVAJIGI, false);
    }

    private Question getQuestionBody(String title, String contents) {
        return new Question(title, contents);
    }

    @Test
    public void 업데이트_작성자() {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Question question = newQuestion(1L);

        Question modifiedQuestion = getQuestionBody("Hello", "World");

        // when
        question.update(loginUser, modifiedQuestion);

        // then
        softly.assertThat(question.getTitle()).isEqualTo(modifiedQuestion.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(modifiedQuestion.getContents());
    }

    @Test
    public void 업데이트_작성자_아닐_경우_UnAuthorizedException() {
        // given
        User loginUser = UserTest.SANJIGI;
        Question javajigiQuestion = JAVA_QUESTION;

        Question modifiedQuestion = new Question("Hello", "World");

        // when
        // then
        assertThatExceptionOfType(UnAuthorizedException.class).isThrownBy(() -> javajigiQuestion.update(loginUser, modifiedQuestion));
    }

    @Test
    public void 업데이트_삭제된_글일_경우_IllegalStateException() {
        // given
        User loginUser = UserTest.SANJIGI;
        Question sanQuestion = SAN_QUESTION;

        Question modifiedQuestion = new Question("Hello", "World");

        // when
        // then
        assertThatIllegalStateException().isThrownBy(() -> sanQuestion.update(loginUser, modifiedQuestion));
    }

    @Test
    public void 삭졔_작성자() throws CannotDeleteException {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Question question = newQuestion(1L);

        // when
        question.delete(loginUser);

        // then
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test
    public void 삭제_작성자가_아닐_경우_UnAuthorizedException() throws CannotDeleteException {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Question sanQuestion = SAN_QUESTION;

        // when
        assertThatExceptionOfType(UnAuthorizedException.class).isThrownBy(() -> sanQuestion.delete(loginUser));
    }

    @Test
    public void 삭제_이미_삭제되있을_경우_CannotDeleteException() {
        // given
        User loginUser = UserTest.SANJIGI;
        Question deletedQuestion = SAN_QUESTION;

        // when
        // then
        assertThatExceptionOfType(CannotDeleteException.class).isThrownBy(() -> deletedQuestion.delete(loginUser));
    }
}
