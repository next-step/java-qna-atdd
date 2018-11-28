package nextstep.domain;

import org.junit.Before;
import org.junit.Test;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import support.test.BaseTest;

import static org.assertj.core.api.Assertions.assertThat;


public class QuestionTest extends BaseTest {

    public static Question newQuestion() {
        return newQuestion(UserTest.JAVAJIGI);
    }

    public static Question newQuestion(User writer) {
        return new Question("타이틀입니다.", "내용입니다.", writer);
    }

    public static Question newQuestion(String title, String contents, User writer) {
        return new Question("타이틀입니다.", "내용입니다.", writer);
    }

    private User origin;
    private Question question;
    private Question updatedQuestion;
    
    @Before
    public void setup() {
        origin = UserTest.JAVAJIGI;
        question = newQuestion();
        updatedQuestion = newQuestion( "Hi 뜻은?", "한국어 뜻은?", origin);
    }
    
    @Test
    public void update_owner() {
        User loginUser = origin;
        question.update(loginUser, updatedQuestion);
        assertThat(question.getTitle()).isEqualTo(updatedQuestion.getTitle());
        assertThat(question.getContents()).isEqualTo(updatedQuestion.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        User loginUser = UserTest.newUser("korkorna");
        question.update(loginUser, updatedQuestion);
    }
    
    @Test
    public void delete_owner() throws CannotDeleteException {
        question.delete(origin);
        assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_owner() throws CannotDeleteException {
        User loginUser = UserTest.newUser("korkorna");
        question.delete(loginUser);
    }
}