package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class UserTest extends BaseTest {
    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    public static User newUser(Long id) {
        return new User(id, "userId", "pass", "name", "javajigi@slipp.net");
    }

    public static User newUser(String userId) {
        return newUser(userId, "password");
    }

    public static User newUser(String userId, String password) {
        return new User(0L, userId, password, "name", "javajigi@slipp.net");
    }

    public static User newUser(long id, String userId, String password) {
        return new User(id, userId, password, "name", "javajigi@slipp.net");
    }

    @Test
    public void update_owner() {
        final User target = newUser(JAVAJIGI.getUserId());
        JAVAJIGI.update(JAVAJIGI, target);
        softly.assertThat(JAVAJIGI.equalsNameAndEmail(target)).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        final User target = newUser(JAVAJIGI.getUserId())
                .setEmail("tester@test.com")
                .setName("tester");
        JAVAJIGI.update(SANJIGI, target);
    }

    @Test
    public void update_match_password() {
        User target = newUser(JAVAJIGI.getUserId())
                .setPassword("password");
        softly.assertThat(JAVAJIGI.equalsNameAndEmail(target)).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_mismatch_password() {
        User target = newUser(JAVAJIGI.getUserId())
                .setPassword("wrongPassword");
        JAVAJIGI.update(JAVAJIGI, target);
    }
}
