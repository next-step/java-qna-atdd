package nextstep.domain;

import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {

    private Question question;

    @Before
    public void setup() {
        question = new Question("title", "contents");
    }

    @Test
    public void isOwner() {
        final User writer = new User(1L, "id", "password", "name", "email");
        question.writeBy(writer);

        softly.assertThat(question.isOwner(writer)).isTrue();
    }

    @Test
    public void isOwner_다를때() {
        final User writer = new User(1L, "id", "password", "name", "email");
        final User another = new User(2L, "id2", "password2", "name2", "email2");
        question.writeBy(writer);

        softly.assertThat(question.isOwner(another)).isFalse();
    }

    @Test
    public void delete() {
        final boolean beforeState = question.isDeleted();

        question.delete();

        softly.assertThat(beforeState).isFalse();
        softly.assertThat(question.isDeleted()).isTrue();
    }

}