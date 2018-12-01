package nextstep.domain;

import com.google.common.collect.ImmutableList;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import java.util.List;

import static nextstep.domain.QuestionTest.DEFAULT_QUESTION;
import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.SANJIGI;


public class AnswersTest extends BaseTest {

    @Test
    public void addAnswer() {
        final Answer answer1 = new Answer(1L, JAVAJIGI, null, "contents1");
        final Answer answer2 = new Answer(2L, JAVAJIGI, null, "contents2");
        final Answers answers = new Answers();
        final int beforeSize = answers.size();

        answers.addAnswer(DEFAULT_QUESTION, answer1);
        softly.assertThat(answers.size()).isEqualTo(beforeSize + 1);

        answers.addAnswer(DEFAULT_QUESTION, answer2);
        softly.assertThat(answers.size()).isEqualTo(beforeSize + 2);

        softly.assertThat(answers.getAnswers()).containsExactly(answer1, answer2);
        softly.assertThat(answers.getAnswers()).allSatisfy(answer -> DEFAULT_QUESTION.isEqualQuestion(answer.getQuestion()));
    }

    @Test
    public void addAnswer_동일아이디_answer_추가() {
        final Answer answer1 = new Answer(1L, JAVAJIGI, null, "contents1");
        final Answer answer2 = new Answer(1L, JAVAJIGI, null, "contents2");
        final Answers answers = new Answers();
        final int beforeSize = answers.size();

        answers.addAnswer(DEFAULT_QUESTION, answer1);
        softly.assertThat(answers.size()).isEqualTo(beforeSize + 1);

        answers.addAnswer(DEFAULT_QUESTION, answer2);
        softly.assertThat(answers.size()).isEqualTo(beforeSize + 1);

        softly.assertThat(answers.getAnswers()).containsExactly(answer1);
        softly.assertThat(answers.getAnswers()).allSatisfy(answer -> DEFAULT_QUESTION.isEqualQuestion(answer.getQuestion()));
    }

    @Test
    public void deleteAll() {
        final Answer answer1 = new Answer(1L, JAVAJIGI, null, "contents1");
        final Answer answer2 = new Answer(2L, JAVAJIGI, null, "contents2");
        final Answers answers = new Answers();
        answers.addAnswer(DEFAULT_QUESTION, answer1);
        answers.addAnswer(DEFAULT_QUESTION, answer2);


        final List<DeleteHistory> deleteHistories = answers.deleteAll(JAVAJIGI, DEFAULT_QUESTION.getId());


        softly.assertThat(answers.getAnswers()).allSatisfy(Answer::isDeleted);
        softly.assertThat(deleteHistories.size()).isEqualTo(answers.size());
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteAll_작성자_다를때() {
        final Answer answer1 = new Answer(1L, JAVAJIGI, null, "contents1");
        final Answer answer2 = new Answer(2L, JAVAJIGI, null, "contents2");
        final Answers answers = new Answers();
        answers.addAnswer(DEFAULT_QUESTION, answer1);
        answers.addAnswer(DEFAULT_QUESTION, answer2);


        answers.deleteAll(SANJIGI, DEFAULT_QUESTION.getId());
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteAll_질문_아이디_다를때() {
        final Answer answer1 = new Answer(1L, JAVAJIGI, null, "contents1");
        final Answer answer2 = new Answer(2L, JAVAJIGI, null, "contents2");
        final Answers answers = new Answers();
        answers.addAnswer(DEFAULT_QUESTION, answer1);
        answers.addAnswer(DEFAULT_QUESTION, answer2);


        answers.deleteAll(JAVAJIGI, DEFAULT_QUESTION.getId() + 1);
    }

    @Test
    public void contains() {
        final Answer answer1 = new Answer(1L, JAVAJIGI, null, "contents1");
        final Answer answer2 = new Answer(2L, JAVAJIGI, null, "contents2");
        final Answer answer3 = new Answer(3L, JAVAJIGI, null, "contents2");
        final Answers answers = new Answers();
        answers.addAnswer(DEFAULT_QUESTION, answer1);
        answers.addAnswer(DEFAULT_QUESTION, answer2);


        softly.assertThat(answers.contains(answer1)).isTrue();
        softly.assertThat(answers.contains(answer2)).isTrue();
        softly.assertThat(answers.contains(answer3)).isFalse();
    }

    @Test
    public void addAllAnswers() {
        final Answer answer1 = new Answer(1L, JAVAJIGI, null, "contents1");
        final Answer answer2 = new Answer(2L, JAVAJIGI, null, "contents2");
        final List<Answer> answerList = ImmutableList.of(answer1, answer2);
        final Answers answers = new Answers();


        answers.addAll(answerList, DEFAULT_QUESTION);


        softly.assertThat(answers.size()).isEqualTo(answerList.size());
    }

    @Test
    public void addAllAnswers_중북_answer_있을때() {
        final Answer answer1 = new Answer(1L, JAVAJIGI, null, "contents1");
        final Answer answer2 = new Answer(1L, JAVAJIGI, null, "contents2");
        final List<Answer> answerList = ImmutableList.of(answer1, answer2);
        final Answers answers = new Answers();


        answers.addAll(answerList, DEFAULT_QUESTION);


        softly.assertThat(answers.size()).isEqualTo(answerList.size() - 1);
    }

}