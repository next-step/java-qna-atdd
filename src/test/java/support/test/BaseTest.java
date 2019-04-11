package support.test;

import nextstep.domain.User;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;

import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.SANJIGI;

public class BaseTest {
    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();

    // resource/import.sql
    protected static final long DEFAULT_QUESTION_ID = 1;
    protected static final long ANOTHER_QUESTION_ID = 2;
    protected static final long DEFAULT_ANSWER_ID = 1;
    protected static final long ANOTHER_ANSWER_ID = 2;

    protected User selfUser() {
        return JAVAJIGI;
    }

    protected User anotherUser() {
        return SANJIGI;
    }
}
