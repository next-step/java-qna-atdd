package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.newUser;

public class QuestionTest extends BaseTest {
    final static User SANGGU = newUser("sangu", "1234");

    public static Question newQuestion(String title, String contents) {
        return newQuestion(0L, title, contents, JAVAJIGI);
    }

    public static Question newQuestion(String title, String contents, User loginUser) {
        return newQuestion(0L, title, contents, loginUser);
    }

    public static Question newQuestion(long id, String title, String contents, User loginUser) {
        return new Question(id, title, contents, loginUser);
    }

    @Test
    public void update_test() {
        User loginUser = JAVAJIGI;

        Question question = newQuestion("제목", "내용", loginUser);
        Question target = newQuestion("수정 제목", "수정 내용", loginUser);

        question.update(loginUser, target);

        softly.assertThat(question.getTitle()).isEqualTo(target.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(target.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_no_login_test() {
        User noLoginUser = newUser("sangu", "1234");

        Question question = newQuestion("제목", "내용", JAVAJIGI);
        Question target = newQuestion("수정 제목", "수정 내용", noLoginUser);

        question.update(noLoginUser, target);
    }

    @Test
    public void delete_test() {
        User loginUser = JAVAJIGI;

        Question question = newQuestion("제목", "내용", loginUser);

        question.delete(loginUser);

        softly.assertThat(question.isDeleted()).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_no_login_test() {
        User loginUser = JAVAJIGI;

        Question question = newQuestion("제목", "내용", loginUser);

        question.delete(SANGGU);
    }
}
