package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import org.junit.*;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {

    User self;
    User another;

    Question selfQuestion;
    Question anotherQuestion;

    @Before
    public void setup() {
        self = new User(1, "self", "pass", "self", "email@email.com");
        another = new User(2, "another", "pass", "another", "email2@email.com");
        selfQuestion = new Question("selfTitle", "selfContent");
        anotherQuestion = new Question("anotherTitle", "anotherContent");

        selfQuestion.writeBy(self);
        anotherQuestion.writeBy(another);
    }

    @Test(expected = CannotUpdateException.class)
    public void update_another() throws Exception {
        selfQuestion.update(another, anotherQuestion);
    }

    @Test
    public void update_self() throws Exception {
        Question updateQuestion = new Question("updateTitle", "updateContents");

        selfQuestion.update(self, updateQuestion);

        softly.assertThat(selfQuestion.getTitle()).isEqualTo(updateQuestion.getTitle());
        softly.assertThat(selfQuestion.getContents()).isEqualTo(updateQuestion.getContents());
        softly.assertThat(selfQuestion.getWriter()).isEqualTo(self);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_another() throws Exception {
        selfQuestion.delete(another);
    }

    @Test
    public void delete_self() throws Exception {
        selfQuestion.delete(self);
        softly.assertThat(selfQuestion.isDeleted()).isTrue();
    }
}
