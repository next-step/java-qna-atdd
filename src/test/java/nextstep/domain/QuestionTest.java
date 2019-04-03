package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import java.util.Arrays;
import java.util.List;

public class QuestionTest extends BaseTest {

    @Test
    public void update() {
        User loginUser = new User("javajigi", "test", "자바지기", "javajigi@slipp.net");
        Question original = new Question(1L, "title", "contents", loginUser);
        Question updated = new Question("updated title", "updated contents");

        original.update(loginUser, updated);

        softly.assertThat(original.getTitle()).isEqualTo(updated.getTitle());
        softly.assertThat(original.getContents()).isEqualTo(updated.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_by_unauthorized_user() {
        User writer = new User(1L, "javajigi", "test", "자바지기", "javajigi@slipp.net");
        Question original = new Question(1L, "title", "contents", writer);

        User loginUser = new User(2L,"sanjigi", "test", "산지기", "javajigi@slipp.net2");
        Question updated = new Question("updated title", "updated contents");

        original.update(loginUser, updated);
    }

    @Test
    public void delete_for_question_only() {
        User loginUser = new User(1L, "javajigi", "test", "자바지기", "javajigi@slipp.net");
        Question question = new Question(1L, "title", "contents", loginUser);

        List<DeleteHistory> deleteHistories = question.delete(loginUser);

        softly.assertThat(question.isDeleted()).isTrue();
        softly.assertThat(deleteHistories).isNotNull();
        softly.assertThat(deleteHistories.size()).isEqualTo(1);
        softly.assertThat(deleteHistories.get(0).equalsEntity(question)).isTrue();
    }

    @Test
    public void delete_for_question_having_an_answer_written_by_same_user() {
        User loginUser = new User(1L, "javajigi", "test", "자바지기", "javajigi@slipp.net");
        Question question = new Question(1L, "title", "contents", loginUser);
        Answer answer = new Answer(1L, loginUser, question, "answer contents");
        question.addAnswer(answer);
        answer.toQuestion(question);

        List<DeleteHistory> deleteHistories = question.delete(loginUser);

        softly.assertThat(question.isDeleted()).isTrue();
        softly.assertThat(deleteHistories).isNotNull();
        softly.assertThat(deleteHistories.size()).isEqualTo(2);
        softly.assertThat(deleteHistories.get(0).equalsEntity(answer)).isTrue();
        softly.assertThat(deleteHistories.get(1).equalsEntity(question)).isTrue();
    }

    @Test
    public void delete_for_question_having_two_answers_written_by_same_user() {
        User loginUser = new User(1L, "javajigi", "test", "자바지기", "javajigi@slipp.net");
        Question question = new Question(1L, "title", "contents", loginUser);

        List<Answer> answers = Arrays.asList(
                new Answer(1L, loginUser, question, "answer contents1"),
                new Answer(2L, loginUser, question, "answer contents2")
        );

        for(Answer answer : answers) {
            question.addAnswer(answer);
            answer.toQuestion(question);
        }

        List<DeleteHistory> deleteHistories = question.delete(loginUser);

        softly.assertThat(question.isDeleted()).isTrue();
        softly.assertThat(deleteHistories).isNotNull();
        softly.assertThat(deleteHistories.size()).isEqualTo(3);
        softly.assertThat(deleteHistories.get(0).equalsEntity(answers.get(0))).isTrue();
        softly.assertThat(deleteHistories.get(1).equalsEntity(answers.get(1))).isTrue();
        softly.assertThat(deleteHistories.get(2).equalsEntity(question)).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_by_unauthorized_user() {
        User writer = new User(1L, "javajigi", "test", "자바지기", "javajigi@slipp.net");
        Question question = new Question(1L, "title", "contents", writer);
        User loginUser = new User(2L,"sanjigi", "test", "산지기", "javajigi@slipp.net2");

        question.delete(loginUser);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_by_unauthorized_user_with_answers() {
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

        User loginUser = new User(2L,"sanjigi", "test", "산지기", "javajigi@slipp.net2");
        question.delete(loginUser);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_for_question_having_one_answer_written_by_different_user() {
        User questionWriter = new User(1L, "javajigi", "test", "자바지기", "javajigi@slipp.net");
        Question question = new Question(1L, "title", "contents", questionWriter);
        User answerWriter = new User(2L,"sanjigi", "test", "산지기", "javajigi@slipp.net2");;

        List<Answer> answers = Arrays.asList(
                new Answer(1L, questionWriter, question, "answer contents1"),
                new Answer(2L, answerWriter, question, "answer contents2")
        );

        for(Answer answer : answers) {
            question.addAnswer(answer);
            answer.toQuestion(question);
        }

        question.delete(questionWriter);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_for_question_having_all_answers_written_by_different_user() {
        User questionWriter = new User(1L, "javajigi", "test", "자바지기", "javajigi@slipp.net");
        Question question = new Question(1L, "title", "contents", questionWriter);
        User answerWriter = new User(2L,"sanjigi", "test", "산지기", "javajigi@slipp.net2");;

        List<Answer> answers = Arrays.asList(
                new Answer(1L, answerWriter, question, "answer contents1"),
                new Answer(2L, answerWriter, question, "answer contents2")
        );

        for(Answer answer : answers) {
            question.addAnswer(answer);
            answer.toQuestion(question);
        }

        question.delete(questionWriter);
    }

}