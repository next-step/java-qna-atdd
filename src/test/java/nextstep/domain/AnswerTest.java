package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static nextstep.domain.User.GUEST_USER;
import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.SANJIGI;

public class AnswerTest extends BaseTest {
    public static final Answer ANSWER = new Answer(JAVAJIGI, "contents...");
    public static final String UPDATED_CONTENTS = "update~~~~~~";

    @Test
    public void update_owner() {
        ANSWER.update(JAVAJIGI, UPDATED_CONTENTS);
        softly.assertThat(ANSWER.hasContents(UPDATED_CONTENTS)).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_guest() {
        ANSWER.update(GUEST_USER, UPDATED_CONTENTS);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        ANSWER.update(SANJIGI, UPDATED_CONTENTS);
    }

    @Test
    public void delete_owner() {
        ANSWER.delete(JAVAJIGI);
        softly.assertThat(ANSWER.isDeleted()).isEqualTo(true);
     }

    @Test(expected = UnAuthorizedException.class)
    public void delete_guest() {
        ANSWER.delete(GUEST_USER);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_not_owner() {
        ANSWER.delete(SANJIGI);
    }
}