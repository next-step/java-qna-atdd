package nextstep.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import nextstep.CannotDeleteException;
import support.test.BaseTest;

public class AnswerTest extends BaseTest {

    private User origin = UserTest.JAVAJIGI;
    
    @Test
    public void delete_ower() throws CannotDeleteException {
        User loginUser = origin;
        Answer answer = new Answer(loginUser, "하하하하좋은질문이네요");
        answer.delete(loginUser);
        assertThat(answer.isDeleted()).isTrue();
    }
    
    @Test(expected = CannotDeleteException.class)
    public void delete_not_ower() throws CannotDeleteException {
        User loginUser = origin;
        Answer answer = new Answer(loginUser, "하하하하좋은질문이네요");
        answer.delete(UserTest.SANJIGI);
    }
}
