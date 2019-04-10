package support.test;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;

public class BaseTest {
    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();

    // resource/import.sql
    protected static final String DEFAULT_LOGIN_USER = "javajigi";
    protected static final String ANOTHER_LOGIN_USER = "another";
    protected static final long DEFAULT_QUESTION_ID = 1;
    protected static final long ANOTHER_QUESTION_ID = 2;
    protected static final long DEFAULT_ANSWER_ID = 1;
    protected static final long ANOTHER_ANSWER_ID = 2;
}
