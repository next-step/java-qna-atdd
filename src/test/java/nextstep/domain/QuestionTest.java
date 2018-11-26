package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

import static org.assertj.core.api.Assertions.assertThat;

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
        final User writer = new User(1L, "id", "password", "name", "email");
        question.writeBy(writer);

        question.delete(writer);

        softly.assertThat(beforeState).isFalse();
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_작성자_다를때() {
        final User writer = new User(1L, "id", "password", "name", "email");
        final User another = new User(2L, "id2", "password2", "name2", "email2");
        question.writeBy(writer);

        question.delete(another);
    }

    @Test
    public void update() {
        final User writer = new User(1L, "id", "password", "name", "email");
        final Question newQuestion = new Question("title2", "contents2");
        question.writeBy(writer);

        question.update(writer, newQuestion);

        assertThat(question.getTitle()).isEqualTo(newQuestion.getTitle());
        assertThat(question.getContents()).isEqualTo(newQuestion.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_작성자_다를때() {
        final Question newQuestion = new Question("title2", "contents2");
        final User writer = new User(1L, "id", "password", "name", "email");
        final User another = new User(2L, "id2", "password2", "name2", "email2");
        question.writeBy(writer);

        question.update(another, newQuestion);

        assertThat(question.getTitle()).isEqualTo(newQuestion.getTitle());
        assertThat(question.getContents()).isEqualTo(newQuestion.getContents());
    }
}