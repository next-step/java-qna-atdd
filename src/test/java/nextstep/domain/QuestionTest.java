package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.SANJIGI;

public class QuestionTest extends BaseTest {

    public static final Question defaultQuestion = new Question("동해물과백두산이", "contents::동해물과백두산이");
    public static final Question updatedQuestion = new Question("변경_동해물과백두산이", "contents::변경_동해물과백두산이");

    public static Question newTestQuestion() {
        return newTestQuestion("동해물과백두산이", "contents::동해물과백두산이");
    }

    public static Question newTestQuestion(User user) {
        Question question = new Question("제목1", "내용1");
        question.writeBy(user);
        return question;
    }

    public static Question newTestQuestion(String title, String contents) {
        return new Question(title, contents);
    }

    public static Question newQuestionByDeleted() {
        Question question = new Question("제목", "내용", true);
        question.writeBy(JAVAJIGI);
        return question;
    }

    @Test
    public void update_owner() {
        User loginUser = JAVAJIGI;
        Question origin = newTestQuestion(JAVAJIGI);
        Question target = newTestQuestion("제목", "내용");

        origin.update(loginUser, target);
        softly.assertThat(origin.getTitle()).isEqualTo(target.getTitle());
        softly.assertThat(origin.getContents()).isEqualTo(target.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() throws Exception {
        Question origin = newTestQuestion(JAVAJIGI);
        User loginUser = SANJIGI;
        Question target = newTestQuestion("제목", "내용");

        origin.update(loginUser, target);
    }

    @Test
    public void delete_owner() throws Exception {
        User loginUser = JAVAJIGI;
        Question origin = newTestQuestion(loginUser);

        origin.delete(loginUser);
        softly.assertThat(origin.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_not_owner() throws Exception {
        Question origin = newTestQuestion(JAVAJIGI);
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