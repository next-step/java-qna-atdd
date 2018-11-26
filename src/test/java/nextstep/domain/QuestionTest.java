package nextstep.domain;

import org.junit.Before;
import org.junit.Test;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import support.test.BaseTest;

import static org.assertj.core.api.Assertions.assertThat;


public class QuestionTest extends BaseTest {

    private User origin;
    private Question question;
    private Question updatedQuestion;
    
    @Before
    public void setup() {
        origin = UserTest.JAVAJIGI;
        question = new Question("하이 뜻은?", "한국어 뜻은?", origin);
        updatedQuestion = new Question( "Hi 뜻은?", "한국어 뜻은?");
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
        User loginUser = origin;
        question.delete(loginUser);
        assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_owner() throws CannotDeleteException {
        User loginUser = UserTest.newUser("korkorna");
        question.delete(loginUser);
    }
}