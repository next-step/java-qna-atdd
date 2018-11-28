package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import support.test.BaseTest;

import static nextstep.CannotDeleteException.ALREADY_DELETED_EXCEPTION;
import static nextstep.CannotDeleteException.HAS_ANSWERS_OF_OTHER_EXCEPTION;
import static nextstep.domain.AnswerTest.newAnswer;
import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.SANJIGI;

public class QuestionTest extends BaseTest {

    public static Question newQuestion() {
        return newQuestion("제목1", "내용1");
    }

    public static Question newQuestionByWriter(User user) {
        return new Question("제목1", "내용1")
                .writeBy(user);
    }

    public static Question newQuestion(String title, String contents) {
        return new Question(title, contents);
    }

    public static Question newQuestionByDeleted() {
        return new Question("제목1", "내용1")
                .writeBy(JAVAJIGI)
                .setDeleted(true);
    }

    public static Question newQuestionHasAnswersOfSelf(User loginUser) {
        Answer answer = newAnswer(loginUser, "테스트입니다2");

        Question question = new Question("제목1", "내용1")
                .writeBy(loginUser);
        question.addAnswer(answer);

        return question;
    }

    public static Question newQuestionHasAnswersOfOther(User loginUser) {
        Answer answer = newAnswer(SANJIGI, "테스트입니다2");

        Question question = new Question("제목1", "내용1")
                .writeBy(loginUser);
        question.addAnswer(answer);

        return question;
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void update_owner() throws Exception {
        User loginUser = JAVAJIGI;
        Question origin = newQuestionByWriter(loginUser);
        Question target = newQuestion("제목2", "내용2");

        origin.update(loginUser, target);
        softly.assertThat(origin.getTitle()).isEqualTo(target.getTitle());
        softly.assertThat(origin.getContents()).isEqualTo(target.getContents());
    }

    @Test
    public void update_not_owner() throws Exception {
        thrown.expect(UnAuthorizedException.class);

        Question origin = newQuestionByWriter(JAVAJIGI);
        User loginUser = SANJIGI;
        Question target = newQuestion("제목2", "내용2");

        origin.update(loginUser, target);
    }

    @Test
    public void delete_owner() throws Exception {
        User loginUser = JAVAJIGI;
        Question origin = newQuestionByWriter(loginUser);

        origin.delete(loginUser);
        softly.assertThat(origin.isDeleted()).isTrue();
    }

    @Test
    public void delete_not_owner() throws Exception {
        thrown.expect(UnAuthorizedException.class);

        Question origin = newQuestionByWriter(JAVAJIGI);
        User loginUser = SANJIGI;

        origin.delete(loginUser);
    }

    @Test
    public void delete_has_answers_of_other() throws Exception {
        thrown.expect(CannotDeleteException.class);
        thrown.expectMessage(HAS_ANSWERS_OF_OTHER_EXCEPTION);

        User loginUser = JAVAJIGI;
        Question origin = newQuestionHasAnswersOfOther(loginUser);
        origin.delete(loginUser);
    }

    @Test
    public void delete_has_answers_of_self() throws Exception {
        User loginUser = JAVAJIGI;
        Question origin = newQuestionHasAnswersOfSelf(loginUser);
        origin.delete(loginUser);
    }

    @Test
    public void delete_already_deleted() throws Exception {
        thrown.expect(CannotDeleteException.class);
        thrown.expectMessage(ALREADY_DELETED_EXCEPTION);

        Question origin = newQuestionByDeleted();
        User loginUser = JAVAJIGI;
        origin.delete(loginUser);
    }
}
