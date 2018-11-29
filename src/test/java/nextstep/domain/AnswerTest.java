package nextstep.domain;

import nextstep.CannotDeleteException;
import org.junit.Test;
import support.test.BaseTest;

import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.SANJIGI;
import static org.assertj.core.api.Assertions.assertThat;

public class AnswerTest extends BaseTest {

    public static Answer newAnswer() {
        return newAnswer(JAVAJIGI);
    }

    public static Answer newAnswer(User user) {
        return new Answer(user, "하하하하좋은질문이네요");
    }

    @Test
    public void delete_ower() throws CannotDeleteException {
        Answer answer = newAnswer(JAVAJIGI);
        answer.delete(JAVAJIGI);
        assertThat(answer.isDeleted()).isTrue();
    }
    
    @Test(expected = CannotDeleteException.class)
    public void delete_not_ower() throws CannotDeleteException {
        Answer answer = newAnswer(JAVAJIGI);
        answer.delete(SANJIGI);
    }
}
