package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import org.junit.*;
import support.test.BaseTest;

import static nextstep.domain.QuestionTest.SELF_QUESTION;
import static nextstep.domain.UserTest.ANOTHER_USER;
import static nextstep.domain.UserTest.SELF_USER;

public class AnswerTest extends BaseTest {

    public static final Answer SELF_ANSWER_OF_DEFAULT_QUESTION = new Answer(SELF_USER, "selfAnswer");
    public static final Answer ANOTHER_ANSWER_OF_DEFAULT_QUESTION = new Answer(ANOTHER_USER, "anotherAnswer");

    public static final long SELF_ANSWER_ID = 1;
    public static final long ANOTHER_ANSWER_ID = 2;

    static {
        SELF_ANSWER_OF_DEFAULT_QUESTION.setId(SELF_ANSWER_ID);
        SELF_ANSWER_OF_DEFAULT_QUESTION.toQuestion(SELF_QUESTION);

        ANOTHER_ANSWER_OF_DEFAULT_QUESTION.setId(ANOTHER_ANSWER_ID);
        ANOTHER_ANSWER_OF_DEFAULT_QUESTION.toQuestion(SELF_QUESTION);
    }

    @Test(expected = CannotUpdateException.class)
    public void update_another() throws Exception {
        SELF_ANSWER_OF_DEFAULT_QUESTION.update(ANOTHER_USER, "contents");
    }

    @Test
    public void update_self() throws Exception {
        String contents = "update contents";
        SELF_ANSWER_OF_DEFAULT_QUESTION.update(SELF_USER, contents);

        softly.assertThat(SELF_ANSWER_OF_DEFAULT_QUESTION.getContents()).isEqualTo(contents);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_another() throws Exception {
        SELF_ANSWER_OF_DEFAULT_QUESTION.delete(ANOTHER_USER);
    }

    @Test
    public void delete_self() throws Exception {
        SELF_ANSWER_OF_DEFAULT_QUESTION.delete(SELF_USER);
        softly.assertThat(SELF_ANSWER_OF_DEFAULT_QUESTION.isDeleted()).isTrue();
    }
}