package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.test.BaseTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

public class QuestionTest extends BaseTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionTest.class);
    public static final Question JAVA_QUESTION = new Question(1L, "자바 제목", "자바 내용", UserTest.JAVAJIGI, false);
    public static final Question DELETED_QUESTION = new Question(2L, "삭제된 질문", "삭제됨", UserTest.JAVAJIGI, true);

    @Test
    public void 업데이트_작성자() {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Question question = noAnswerQuestion();

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
        User loginUser = UserTest.JAVAJIGI;
        Question questionOfOther = questionOfOther();

        Question modifiedQuestion = getQuestionBody("Hello", "World");

        // when
        // then
        assertThatExceptionOfType(UnAuthorizedException.class).isThrownBy(() -> questionOfOther.update(loginUser, modifiedQuestion));
    }

    @Test
    public void 업데이트_삭제된_글일_경우_IllegalStateException() {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Question question = deletedQuestion();

        Question modifiedQuestion = getQuestionBody("Hello", "World");

        // when
        // then
        assertThatIllegalStateException().isThrownBy(() -> question.update(loginUser, modifiedQuestion));
    }

    @Test
    public void 삭제_작성자가_아닐_경우_UnAuthorizedException() {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Question questionOfOther = questionOfOther();

        // when
        assertThatExceptionOfType(UnAuthorizedException.class).isThrownBy(() -> questionOfOther.delete(loginUser));
    }

    @Test
    public void 삭제_이미_삭제되있을_경우_CannotDeleteException() {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Question deletedQuestion = deletedQuestion();

        // when
        // then
        assertThatExceptionOfType(CannotDeleteException.class).isThrownBy(() -> deletedQuestion.delete(loginUser));
    }

    @Test
    public void 삭졔_작성자_답변이_없는_경우_정상_삭제() throws CannotDeleteException {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Question noAnswerQuestion = noAnswerQuestion();

        // when
        noAnswerQuestion.delete(loginUser);

        // then
        softly.assertThat(noAnswerQuestion.isDeleted()).isTrue();
    }

    @Test
    public void 삭제_작성자_본인의_답변만_있는_경우_정상_삭제() throws CannotDeleteException {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Question questionWithOwnAnswer = questionWithOwnAnswer();

        // when
        questionWithOwnAnswer.delete(loginUser);

        // then
        softly.assertThat(questionWithOwnAnswer.isDeleted()).isTrue();
        softly.assertThat(questionWithOwnAnswer.getUsedAnswers()).isEmpty();
    }

    @Test
    public void 삭제_작성자_타인의_답변이_있는_경우_CannotDeleteException() {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Question questionWithOtherUserAnswer = questionWithOtherUserAnswer();

        // when
        // then
        assertThatExceptionOfType(CannotDeleteException.class).isThrownBy(() -> questionWithOtherUserAnswer.delete(loginUser));
        softly.assertThat(questionWithOtherUserAnswer.isDeleted()).isFalse();
        softly.assertThat(questionWithOtherUserAnswer.getUsedAnswers()).isNotEmpty();
    }

    @Test
    public void 삭제_작성자_삭제된_타인의_답변이_있는_경우_정상_삭제() throws CannotDeleteException {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Question questionWithDeletedOtherUserAnswer = questionWithDeletedOtherUserAnswer();

        // when
        questionWithDeletedOtherUserAnswer.delete(loginUser);

        // then
        softly.assertThat(questionWithDeletedOtherUserAnswer.isDeleted()).isTrue();
        softly.assertThat(questionWithDeletedOtherUserAnswer.getUsedAnswers()).isEmpty();
    }

    @Test
    public void 삭제_후_DeleteHistory_확인() throws CannotDeleteException {
        // given
        User loginUser = UserTest.JAVAJIGI;
        Question question = questionWithDeletedOtherUserAnswer();

        int numberOfQuestionAndAnswers = 1 + question.getUsedAnswers().size(); // 질문 + 답변

        // when
        List<DeleteHistory> deleteHistories = question.delete(loginUser);

        // then
        softly.assertThat(deleteHistories).hasSize(numberOfQuestionAndAnswers);

        deleteHistories.forEach(deleteHistory -> log.debug("{}", deleteHistory));
    }

    private Question noAnswerQuestion() {
        return new Question(1L, "답변 없는 질문", "no content", UserTest.JAVAJIGI, false);
    }

    private Question deletedQuestion() {
        return new Question(1L, "삭제된 질문", "no content", UserTest.JAVAJIGI, true);
    }

    private Question questionOfOther() {
        return new Question(1L, "타인의 질문", "no content", UserTest.SANJIGI, false);
    }

    private Question questionWithOwnAnswer() {
        Question question = new Question(1L, "본인만 답변한 질문", "no content", UserTest.JAVAJIGI, false);
        question.addAnswer(new Answer(1L, UserTest.JAVAJIGI, question, "본인의 답변 1"));
        question.addAnswer(new Answer(2L, UserTest.JAVAJIGI, question, "본인의 답변 2"));
        return question;
    }

    private Question questionWithOtherUserAnswer() {
        Question question = new Question(1L, "타인이 답변한 질문", "no content", UserTest.JAVAJIGI, false);
        question.addAnswer(new Answer(1L, UserTest.JAVAJIGI, question, "본인의 답변"));
        question.addAnswer(new Answer(2L, UserTest.SANJIGI, question, "타인의 답변"));
        return question;
    }

    private Question questionWithDeletedOtherUserAnswer() {
        Question question = new Question(1L, "삭제된 타인의 답변이 있는 질문", "no content", UserTest.JAVAJIGI, false);
        question.addAnswer(new Answer(1L, UserTest.JAVAJIGI, question, "삭제되지 않은 본인의 답변", false));
        question.addAnswer(new Answer(2L, UserTest.SANJIGI, question, "삭제된 타인의 답변", true));
        return question;
    }

    private Question getQuestionBody(String title, String contents) {
        return new Question(title, contents);
    }
}
