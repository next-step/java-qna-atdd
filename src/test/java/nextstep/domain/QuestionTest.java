package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {

    public static Question newQuestion() { return new Question(UserTest.JAVAJIGI); }

    public static Question newQuestion(String title, String contents) {
        return new Question(title, contents);
    }

    public static Question newQuestion(String title, String contents, User writer) {
        return new Question("국내 skills", "국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", writer);
    }

    User origin;
    Question question;
    Question updatedQuestion;

    @Before
    public void setUp() {
        origin = UserTest.JAVAJIGI;
        question = newQuestion();
        updatedQuestion = newQuestion("runtime~~", "runtime 에 reflect 발동 주체 객체가 뭔지 알 방법이 있을까요?", origin);
    }

    @Test
    public void update_owner() {
        User user = origin;
        question.update(user, updatedQuestion);
        softly.assertThat(question.getTitle()).isEqualTo(updatedQuestion.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(updatedQuestion.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        User user = UserTest.newUser("sanjigi");
        question.update(user, updatedQuestion);
    }

    @Test
    public void delete_owner() throws CannotDeleteException {
        question.delete(origin);
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_owner() throws CannotDeleteException {
        User user = UserTest.newUser("sanjigi");
        question.delete(user);
    }
}