package support.test;

import nextstep.domain.User;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;

public class BaseTest {
    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();

    protected User basicUser = createTestUser("sejong");
    protected User anotherUser = createTestUser("gamja");

    private static User createTestUser(String userId) {
        return new User(userId, "password", userId, "devsejong@gmail.com");
    }
}
