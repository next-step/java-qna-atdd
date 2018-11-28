package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

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

    @Test
    public void update_owner() throws Exception {
        User loginUser = JAVAJIGI;
        Question origin = newQuestionByWriter(loginUser);
        Question target = newQuestion("제목2", "내용2");

        origin.update(loginUser, target);
        softly.assertThat(origin.getTitle()).isEqualTo(target.getTitle());
        softly.assertThat(origin.getContents()).isEqualTo(target.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() throws Exception {
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

    @Test(expected = UnAuthorizedException.class)
    public void delete_not_owner() throws Exception {
        Question origin = newQuestionByWriter(JAVAJIGI);
        User loginUser = SANJIGI;

        origin.delete(loginUser);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_has_answers_of_other() throws Exception {
        Question origin = newQuestionByDeleted();
        User loginUser = JAVAJIGI;
        origin.delete(loginUser);
    }

    @Test(expected = CannotDeleteException.class)
    public void can_not_delete() throws Exception {
        Question origin = newQuestionByDeleted();
        User loginUser = JAVAJIGI;
        origin.delete(loginUser);
    }
}
