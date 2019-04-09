package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

public class AnswerTest extends BaseTest {
    public static final Answer JAVA_ANSWER = new Answer(1L, UserTest.JAVAJIGI, QuestionTest.JAVA_QUESTION, "자바 댓글", false);
    public static final Answer SAN_ANSWER = new Answer(2L, UserTest.SANJIGI, QuestionTest.SAN_QUESTION, "산 댓글", false);
    public static final Answer DELETED_ANSWER = new Answer(3L, UserTest.JAVAJIGI, QuestionTest.JAVA_QUESTION, "삭제된 답변", true);

    private Answer newAnswer(long id) {
        return new Answer(id, UserTest.JAVAJIGI, QuestionTest.JAVA_QUESTION, "자바 답변");
    }

    private Answer getAnswerBody(String contents) {
        Answer answerBody = new Answer();
        answerBody.setContents(contents);
        return answerBody;
    }

    @Test
    public void 업데이트_작성자() {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Answer answer = newAnswer(1L);

        Answer modifiedAnswer = getAnswerBody("Hello");

        // when
        answer.update(loginUser, modifiedAnswer);

        // then
        softly.assertThat(answer.getContents()).isEqualTo(modifiedAnswer.getContents());
    }

    @Test
    public void 업데이트_작성자_아닐_경우_UnAuthorizedException() {
        // given
        User loginUser = UserTest.SANJIGI;
        Answer answer = JAVA_ANSWER;

        Answer modifiedAnswer = getAnswerBody("Hello");

        // when
        // then
        assertThatExceptionOfType(UnAuthorizedException.class).isThrownBy(() -> answer.update(loginUser, modifiedAnswer));
    }

    @Test
    public void 업데이트_삭제된_질문일_경우_IllegalStateException() {
        // given
        User loginUser = UserTest.SANJIGI;
        Answer answer = SAN_ANSWER;

        Answer modifiedAnswer = getAnswerBody("Hello");

        // when
        // then
        assertThatIllegalStateException().isThrownBy(() -> answer.update(loginUser, modifiedAnswer));
    }

    @Test
    public void 업데이트_삭제된_답변일_경우_IllegalStateException() {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Answer answer = DELETED_ANSWER;

        Answer modifiedAnswer = getAnswerBody("Hello");

        // when
        // then
        assertThatIllegalStateException().isThrownBy(() -> answer.update(loginUser, modifiedAnswer));
    }

    @Test
    public void 삭제_작성자() throws CannotDeleteException {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Answer answer = newAnswer(1L);

        // when
        answer.delete(loginUser);

        // then
        softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test
    public void 삭제_작성자가_아닐_경우_UnAuthorizedException() {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Answer sanAnswer = SAN_ANSWER;

        // when
        // then
        assertThatExceptionOfType(UnAuthorizedException.class).isThrownBy(() -> sanAnswer.delete(loginUser));
    }

    @Test
    public void 삭졔_이미_삭제되있을_경우_CannotDeleteException() {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Answer sanAnswer = DELETED_ANSWER;

        // when
        // then
        assertThatExceptionOfType(CannotDeleteException.class).isThrownBy(() -> sanAnswer.delete(loginUser));
    }

    @Test
    public void 삭제_질문이_삭제되있을_경우_CannotDeleteException() {
        // given
        User loginUser = UserTest.SANJIGI;
        Answer sanAnswer = SAN_ANSWER;

        // when
        // then
        assertThatExceptionOfType(CannotDeleteException.class).isThrownBy(() -> sanAnswer.delete(loginUser));
    }
}
