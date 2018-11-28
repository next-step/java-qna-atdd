package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class AnswerTest extends BaseTest {

    @Test
    public void delete() {
        final User writer = new User(1L, "id", "pass", "name", "email");
        final Question question = new Question(1L, "title", "contents");
        final String contents = "contents!!";
        final Long questionId = 1L;
        final Answer answer = new Answer(writer, contents);
        answer.toQuestion(question);
        final boolean beforeDeleted = answer.isDeleted();

        answer.delete(writer, questionId);

        softly.assertThat(answer.isDeleted()).isTrue();
        softly.assertThat(answer.isDeleted()).isNotEqualTo(beforeDeleted);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_작성자_다를때() {
        final User writer = new User(1L, "id", "pass", "name", "email");
        final User another = new User(2L, "id2", "pass2", "name2", "email2");
        final Long questionId = 1L;
        final String contents = "contents!!";
        final Answer answer = new Answer(writer, contents);

        answer.delete(another, questionId);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_질문아이디_다를때() {
        final User writer = new User(1L, "id", "pass", "name", "email");
        final Question question = new Question(1L, "title", "contents");
        final String contents = "contents!!";
        final Long questionId = 2L;
        final Answer answer = new Answer(writer, contents);
        answer.toQuestion(question);


        answer.delete(writer, questionId);
    }

    @Test
    public void isEqualAnswer() {
        final User writer = new User(1L, "id", "pass", "name", "email");
        final Question question = new Question(1L, "title", "contents");
        final Long answerId = 1L;
        final String contents = "contents!!";
        final Answer answer = new Answer(answerId, writer, question, contents);
        final Answer another = new Answer(answerId, writer, question, contents);

        softly.assertThat(answer.isEqualAnswer(another)).isTrue();
    }

    @Test
    public void isEqualAnswer_작성자_다를때() {
        final User writer = new User(1L, "id", "pass", "name", "email");
        final User anotherWriter = new User(2L, "id", "pass", "name", "email");
        final Question question = new Question(1L, "title", "contents");
        final Long answerId = 1L;
        final String contents = "contents!!";
        final Answer answer = new Answer(answerId, writer, question, contents);
        final Answer another = new Answer(answerId, anotherWriter, question, contents);

        softly.assertThat(answer.isEqualAnswer(another)).isFalse();
    }

    @Test
    public void isEqualAnswer_질문_다를때() {
        final User writer = new User(1L, "id", "pass", "name", "email");
        final Question question = new Question(1L, "title", "contents");
        final Question anotherQuestion = new Question(2L, "title", "contents");
        final Long answerId = 1L;
        final String contents = "contents!!";
        final Answer answer = new Answer(answerId, writer, question, contents);
        final Answer another = new Answer(answerId, writer, anotherQuestion, contents);

        softly.assertThat(answer.isEqualAnswer(another)).isFalse();
    }

    @Test
    public void isEqualAnswer_아이디_다를때() {
        final User writer = new User(1L, "id", "pass", "name", "email");
        final Question question = new Question(1L, "title", "contents");
        final Long answerId = 1L;
        final Long anotherId = 2L;
        final String contents = "contents!!";
        final Answer answer = new Answer(answerId, writer, question, contents);
        final Answer another = new Answer(anotherId, writer, question, contents);

        softly.assertThat(answer.isEqualAnswer(another)).isFalse();
    }

    @Test
    public void isEqualAnswer_내용_다를때() {
        final User writer = new User(1L, "id", "pass", "name", "email");
        final Question question = new Question(1L, "title", "contents");
        final Long answerId = 1L;
        final String contents = "contents!!";
        final String anotherContents = "222contents!!";
        final Answer answer = new Answer(answerId, writer, question, contents);
        final Answer another = new Answer(answerId, writer, question, anotherContents);

        softly.assertThat(answer.isEqualAnswer(another)).isFalse();
    }
}