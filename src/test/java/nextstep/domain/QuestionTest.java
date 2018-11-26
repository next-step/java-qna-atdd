package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.SANJIGI;

public class QuestionTest extends BaseTest {

    public static Question newQuestion() {
        return newQuestion("제목1", "내용1");
    }

    public static Question newQuestion(User user) {
        Question question = new Question("제목1", "내용1");
        question.writeBy(user);
        return question;
    }

    public static Question newQuestion(String title, String contents) {
        return new Question(title, contents);
    }

    public static Question newQuestionByDeleted() {
        Question question = new Question("제목1", "내용1", true);
        question.writeBy(JAVAJIGI);
        return question;
    }

    @Test
    public void update_owner() throws Exception {
        User loginUser = JAVAJIGI;
        Question origin = newQuestion(loginUser);
        Question target = newQuestion("제목2", "내용2");

        origin.update(loginUser, target);
        softly.assertThat(origin.getTitle()).isEqualTo(target.getTitle());
        softly.assertThat(origin.getContents()).isEqualTo(target.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() throws Exception {
        Question origin = newQuestion(JAVAJIGI);
        User loginUser = SANJIGI;
        Question target = newQuestion("제목2", "내용2");

        origin.update(loginUser, target);
    }

    @Test
    public void delete_owner() throws Exception {
        User loginUser = JAVAJIGI;
        Question origin = newQuestion(loginUser);

        origin.delete(loginUser);
        softly.assertThat(origin.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_not_owner() throws Exception {
        Question origin = newQuestion(JAVAJIGI);
        User loginUser = SANJIGI;

        origin.delete(loginUser);
    }

    @Test(expected = CannotDeleteException.class)
    public void can_not_delete() throws Exception {
        Question origin = newQuestionByDeleted();
        User loginUser = JAVAJIGI;
        origin.delete(loginUser);
    }
}
