package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

public class AnswerTest extends BaseTest {

    @Test
    public void 업데이트_작성자() {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Answer answer = notDeletedAnswer();

        Answer modifiedAnswer = getAnswerBody("Hello");

        // when
        answer.update(loginUser, modifiedAnswer);

        // then
        softly.assertThat(answer.getContents()).isEqualTo(modifiedAnswer.getContents());
    }

    @Test
    public void 업데이트_작성자_아닐_경우_UnAuthorizedException() {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Answer answerOfOther = answerOfOther();

        Answer modifiedAnswer = getAnswerBody("Hello");

        // when
        // then
        assertThatExceptionOfType(UnAuthorizedException.class).isThrownBy(() -> answerOfOther.update(loginUser, modifiedAnswer));
    }

    @Test
    public void 업데이트_삭제된_질문일_경우_IllegalStateException() {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Answer deletedAnswer = deletedAnswer();

        Answer modifiedAnswer = getAnswerBody("Hello");

        // when
        // then
        assertThatIllegalStateException().isThrownBy(() -> deletedAnswer.update(loginUser, modifiedAnswer));
    }

    @Test
    public void 업데이트_삭제된_답변일_경우_IllegalStateException() {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Answer answerOfDeletedQuestion = answerOfDeletedQuestion();

        Answer modifiedAnswer = getAnswerBody("Hello");

        // when
        // then
        assertThatIllegalStateException().isThrownBy(() -> answerOfDeletedQuestion.update(loginUser, modifiedAnswer));
    }

    @Test
    public void 삭제_작성자() throws CannotDeleteException {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Answer answer = notDeletedAnswer();

        // when
        answer.delete(loginUser);

        // then
        softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test
    public void 삭제_작성자가_아닐_경우_UnAuthorizedException() {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Answer answerOfOther = answerOfOther();

        // when
        // then
        assertThatExceptionOfType(UnAuthorizedException.class).isThrownBy(() -> answerOfOther.delete(loginUser));
    }

    @Test
    public void 삭졔_이미_삭제되있을_경우_CannotDeleteException() {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Answer sanAnswer = deletedAnswer();

        // when
        // then
        assertThatExceptionOfType(CannotDeleteException.class).isThrownBy(() -> sanAnswer.delete(loginUser));
    }

    @Test
    public void 삭제_질문이_삭제되있을_경우_CannotDeleteException() {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Answer deletedAnswer = answerOfDeletedQuestion();

        // when
        // then
        assertThatExceptionOfType(CannotDeleteException.class).isThrownBy(() -> deletedAnswer.delete(loginUser));
    }

    private Answer notDeletedAnswer() {
        return new Answer(1L, UserTest.JAVAJIGI, QuestionTest.JAVA_QUESTION, "일반 답변", false);
    }

    private Answer deletedAnswer() {
        return new Answer(1L, UserTest.JAVAJIGI, QuestionTest.JAVA_QUESTION, "삭제된 답변", true);
    }

    private Answer answerOfDeletedQuestion() {
        return new Answer(1L, UserTest.JAVAJIGI, QuestionTest.DELETED_QUESTION, "삭제된 질문의 답변", false);
    }

    private Answer answerOfOther() {
        return new Answer(1L, UserTest.SANJIGI, QuestionTest.JAVA_QUESTION, "타인의 답변", false);
    }

    private Answer getAnswerBody(String contents) {
        Answer answerBody = new Answer();
        answerBody.setContents(contents);
        return answerBody;
    }
}
