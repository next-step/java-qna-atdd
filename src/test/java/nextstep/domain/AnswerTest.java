package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import org.junit.*;
import support.test.BaseTest;

import static nextstep.domain.QuestionTest.newQuestion;

public class AnswerTest extends BaseTest {
    private Question question;

    private Answer selfAnswer;
    private Answer anotherAnswer;

    @Before
    public void setup() {
        question = newQuestion("selfTitle", "selfContent");
        question.writeBy(selfUser());

        selfAnswer = new Answer(selfUser(), "selfAnswer");
        anotherAnswer = new Answer(anotherUser(), "anotherAnswer");

        selfAnswer.toQuestion(question);
        anotherAnswer.toQuestion(question);
    }

    @Test(expected = CannotUpdateException.class)
    public void update_another() throws Exception {
        selfAnswer.update(anotherUser(), "contents");
    }

    @Test
    public void update_self() throws Exception {
        String contents = "update contents";
        selfAnswer.update(selfUser(), contents);

        softly.assertThat(selfAnswer.getContents()).isEqualTo(contents);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_another() throws Exception {
        selfAnswer.delete(anotherUser());
    }

    @Test
    public void delete_self() throws Exception {
        selfAnswer.delete(selfUser());
        softly.assertThat(selfAnswer.isDeleted()).isTrue();
    }
}