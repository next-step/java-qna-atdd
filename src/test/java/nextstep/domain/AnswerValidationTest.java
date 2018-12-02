package nextstep.domain;

import org.junit.BeforeClass;
import org.junit.Test;
import support.test.BaseTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static nextstep.domain.UserTest.JAVAJIGI;

public class AnswerValidationTest  extends BaseTest {
    private static Validator validator;

    @BeforeClass
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void titleWhenIsEmpty() throws Exception {
        Answer answer = new Answer(JAVAJIGI, "");
        Set<ConstraintViolation<Answer>> constraintViolcations = validator.validate(answer);
        softly.assertThat(constraintViolcations).hasSize(1);
    }
}
