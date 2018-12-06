package support.test;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class BaseTest {
    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Rule
    public ExpectedException exception = ExpectedException.none();
}
