package nextstep.domain;

import nextstep.CannotDeleteException;
import org.junit.Test;
import support.test.BaseTest;

import static nextstep.domain.AnswerTest.newAnswer;
import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.SANJIGI;

public class AnswersTest extends BaseTest {

    @Test
    public void deleteAll() throws CannotDeleteException {
        Answers answers = newAnswers();
        DeleteHistories histories = answers.deleteAll(JAVAJIGI);
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteAll_failed_when_other_user() throws CannotDeleteException {
        Answers answers = newAnswers();
        answers.deleteAll(SANJIGI);
    }

    private Answers newAnswers() {
        Answers answers = new Answers();
        Answer answer = newAnswer();
        answers.addAnswer(answer);
        return answers;
    }
}
