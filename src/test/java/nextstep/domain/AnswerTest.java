package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.SANJIGI;

public class AnswerTest extends BaseTest {

    public static Answer newAnswer(User user) {
        return new Answer(user, "새로운답변");
    }

    public static Answer newAnswer(User user, String contents) {
        return new Answer(user, contents);
    }

    public static Answer newAnswerByDeleted() {
        return new Answer(JAVAJIGI, "새로운답변", true);
    }

    @Test
    public void update_owner() {
        User loginUser = JAVAJIGI;
        Answer origin = newAnswer(loginUser);
        Answer target = newAnswer(loginUser, "새로운답변");

        origin.update(loginUser, target);
        softly.assertThat(origin.equalsContents(target)).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        Answer origin = newAnswer(JAVAJIGI);
        User loginUser = SANJIGI;
        Answer target = newAnswer(SANJIGI, "변경된답변");

        origin.update(loginUser, target);
    }

    @Test
    public void delete_owner() throws Exception {
        User loginUser = JAVAJIGI;
        Answer origin = newAnswer(loginUser);

        origin.delete(loginUser);
        softly.assertThat(origin.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_not_owner() throws Exception {
        Answer origin = newAnswer(JAVAJIGI);
        User loginUser = SANJIGI;

        origin.delete(loginUser);
    }

    @Test(expected = CannotDeleteException.class)
    public void can_not_delete() throws Exception {
        Answer origin = newAnswerByDeleted();
        origin.delete(JAVAJIGI);
    }
}