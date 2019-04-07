package nextstep.domain;

import org.junit.BeforeClass;
import org.junit.Test;
import support.test.BaseTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static nextstep.domain.Fixture.*;

public class QuestionValidationTest extends BaseTest {
    private static Validator validator;

    @BeforeClass
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void 질문생성_테스트() throws Exception {
        Set<ConstraintViolation<Question>> constraintViolcations = validator.validate(MOCK_QUESTION);
        softly.assertThat(constraintViolcations).hasSize(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void titleWhenIsEmpty() throws Exception {
        Question question = Question.builder().title("").contents(CONTENTS).build();
        Set<ConstraintViolation<Question>> constraintViolcations = validator.validate(question);
        softly.assertThat(constraintViolcations).hasSize(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void 컨텐츠_세글자_테스트() {
        Question question = Question.builder().title(TITLE).contents("No").build();
        Set<ConstraintViolation<Question>> constraintViolcations = validator.validate(question);
        softly.assertThat(constraintViolcations).hasSize(1);
    }
}
