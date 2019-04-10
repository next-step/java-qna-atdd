package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import org.junit.*;
import support.test.BaseTest;

import static nextstep.domain.QuestionTest.newQuestion;

public class AnswerTest extends BaseTest {
    private User self;
    private User another;

    private Question question;

    private Answer selfAnswer;
    private Answer anotherAnswer;

    @Before
    public void setup() {
        self = new User(1, "self", "pass", "self", "email@email.com");
        another = new User(2, "another", "pass", "another", "email2@email.com");
        question = newQuestion("selfTitle", "selfContent");
        question.writeBy(self);

        selfAnswer = new Answer(self, "selfAnswer");
        anotherAnswer = new Answer(another, "anotherAnswer");

        selfAnswer.toQuestion(question);
        anotherAnswer.toQuestion(question);
    }

    @Test(expected = CannotUpdateException.class)
    public void update_another() throws Exception {
        selfAnswer.update(another, "contents");
    }

    @Test
    public void update_self() throws Exception {
        String contents = "update contents";
        selfAnswer.update(self, contents);

        softly.assertThat(selfAnswer.getContents()).isEqualTo(contents);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_another() throws Exception {
        selfAnswer.delete(another);
    }

    @Test
    public void delete_self() throws Exception {
        selfAnswer.delete(self);
        softly.assertThat(selfAnswer.isDeleted()).isTrue();
    }
}