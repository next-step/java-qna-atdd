package support.test;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.*;

public class BaseTest {
    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();
}
