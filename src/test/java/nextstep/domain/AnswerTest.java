package nextstep.domain;

import nextstep.UnAuthenticationException;
import org.junit.Test;
import support.test.BaseTest;

public class AnswerTest extends BaseTest {

    @Test
    public void 본인_만_댓글_삭제_가능() throws UnAuthenticationException {
        User user = new User("testid","test","testname","test@t.com");
        Answer answer = new Answer(user, "댓글.");
        answer.delete(user);

        softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthenticationException.class)
    public void 본인_외에_댓글_삭제_불가능() throws UnAuthenticationException {
        User writeUser = new User("testid","test","testname","test@t.com");
        Answer answer = new Answer(writeUser, "writeUser 댓글.");

        User guestUser = User.GUEST_USER;
        answer.delete(guestUser);
    }
}