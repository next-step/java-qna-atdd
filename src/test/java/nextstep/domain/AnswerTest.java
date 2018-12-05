package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;


public class AnswerTest extends BaseTest {

    public Answer newAnswer(User writer) {
        return new Answer(writer, "언더스코어 강력 추천드려요.");
    }

    User origin;
    Answer answer;

    @Before
    public void setUp() {
        origin = UserTest.JAVAJIGI;
        answer = newAnswer(origin);
    }

    @Test
    public void delete_owner() {
        User user = UserTest.JAVAJIGI;
        answer.delete(user);
        softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_not_owner() {
        User user = UserTest.SANJIGI;
        answer.delete(user);
    }
}