package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import java.util.Arrays;
import java.util.List;

public class AnswersTest extends BaseTest {

    @Test
    public void delete_answer() {
        User loginUser = new User(2L,"sanjigi", "test", "산지기", "javajigi@slipp.net2");;

        Answers answers = new Answers(Arrays.asList(
                new Answer(1L, loginUser, null, "answer contents1")
        ));

        List<DeleteHistory> deleteHistories = answers.delete(loginUser);

        softly.assertThat(deleteHistories).isNotNull();
        softly.assertThat(deleteHistories.size()).isEqualTo(1);
        softly.assertThat(deleteHistories.get(0).equalsEntity(answers.get(0))).isTrue();
    }

    @Test
    public void delete_answers_written_by_same_user() {
        User loginUser = new User(2L,"sanjigi", "test", "산지기", "javajigi@slipp.net2");;

        Answers answers = new Answers(Arrays.asList(
                new Answer(1L, loginUser, null, "answer contents1"),
                new Answer(2L, loginUser, null, "answer contents2")
        ));

        List<DeleteHistory> deleteHistories = answers.delete(loginUser);

        softly.assertThat(deleteHistories).isNotNull();
        softly.assertThat(deleteHistories.size()).isEqualTo(2);
        softly.assertThat(deleteHistories.get(0).equalsEntity(answers.get(0))).isTrue();
        softly.assertThat(deleteHistories.get(1).equalsEntity(answers.get(1))).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_answer_written_by_another_user() {
        User answerWriter = new User(2L,"sanjigi", "test", "산지기", "javajigi@slipp.net2");;
        User loginUser = new User(1L, "javajigi", "test", "자바지기", "javajigi@slipp.net");

        Answers answers = new Answers(Arrays.asList(
                new Answer(1L, answerWriter, null, "answer contents2")
        ));

        answers.delete(loginUser);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_answers_written_by_different_user() {
        User answerWriter = new User(2L,"sanjigi", "test", "산지기", "javajigi@slipp.net2");;
        User loginUser = new User(1L, "javajigi", "test", "자바지기", "javajigi@slipp.net");

        Answers answers = new Answers(Arrays.asList(
                new Answer(1L, loginUser, null, "answer contents1"),
                new Answer(2L, answerWriter, null, "answer contents2")
        ));

        answers.delete(loginUser);
    }

    @Test
    public void containsAnswer() {
        User writer = new User(1L, "javajigi", "test", "자바지기", "javajigi@slipp.net");
        Question question = new Question(1L, "title", "contents", writer);

        List<Answer> answers = Arrays.asList(
                new Answer(1L, writer, question, "answer contents1"),
                new Answer(2L, writer, question, "answer contents2")
        );

        for(Answer answer : answers) {
            question.addAnswer(answer);
            answer.toQuestion(question);
            softly.assertThat(question.containsAnswer(answer.getId())).isTrue();
        }
    }

    @Test
    public void containsAnswer_for_not_containing_answers() {
        User writer = new User(1L, "javajigi", "test", "자바지기", "javajigi@slipp.net");
        Question question = new Question(1L, "title", "contents", writer);

        List<Answer> answers = Arrays.asList(
                new Answer(1L, writer, question, "answer contents1"),
                new Answer(2L, writer, question, "answer contents2")
        );

        for(Answer answer : answers) {
            question.addAnswer(answer);
            answer.toQuestion(question);
        }

        softly.assertThat(question.containsAnswer(3L)).isFalse();
        softly.assertThat(question.containsAnswer(4L)).isFalse();
    }
}
