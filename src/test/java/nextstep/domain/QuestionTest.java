package nextstep.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.SANJIGI;
import static org.assertj.core.api.Assertions.assertThat;

public class QuestionTest extends BaseTest {

    public static final Question DEFAULT_QUESTION = new Question(1L, "title", "contents!!");
    
    private Question question;

    @Before
    public void setup() {
        this.question = new Question(1L, "title", "contents!!");
    }

    @Test
    public void isOwner() {
        question.writeBy(JAVAJIGI);

        softly.assertThat(question.isOwner(JAVAJIGI)).isTrue();
    }

    @Test
    public void isOwner_다를때() {
        question.writeBy(JAVAJIGI);

        softly.assertThat(question.isOwner(SANJIGI)).isFalse();
    }

    @Test
    public void delete_답변_없을때() {
        final boolean beforeState = question.isDeleted();
        question.writeBy(JAVAJIGI);

        question.delete(JAVAJIGI);

        softly.assertThat(beforeState).isFalse();
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test
    public void delete_동일_작성자_답변만_있을때() {
        question.writeBy(JAVAJIGI);
        final List<Answer> answers = createSameWriterAnswers(JAVAJIGI);
        question.addAllAnswer(answers);

        final List<DeleteHistory> deleteHistories = question.delete(JAVAJIGI);
        final Map<ContentType, List<DeleteHistory>> historyMap = deleteHistories.stream().collect(Collectors.groupingBy(DeleteHistory::getContentType, Collectors.toList()));

        softly.assertThat(question.isDeleted()).isTrue();
        softly.assertThat(question.isAllAnswerDeleted()).isTrue();
        softly.assertThat(historyMap.get(ContentType.QUESTION).size()).isEqualTo(1);
        softly.assertThat(historyMap.get(ContentType.ANSWER).size()).isEqualTo(answers.size());
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_다른_작성자_답변_있을때() {
        question.writeBy(JAVAJIGI);
        final List<Answer> answers = createSameWriterAnswers(JAVAJIGI);
        question.addAllAnswer(answers);
        final Answer anotherWriterAnswer = new Answer((long) (answers.size() + 1), SANJIGI, question, "contents!!");
        question.addAnswer(anotherWriterAnswer);


        question.delete(JAVAJIGI);
    }

    private List<Answer> createSameWriterAnswers(User writer) {
        List<Answer> answers = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            Answer answer = new Answer(i, writer, null, "contents!!");
            answers.add(answer);
        }

        return answers;
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_작성자_다를때() {
        question.writeBy(JAVAJIGI);

        question.delete(SANJIGI);
    }

    @Test
    public void update() {
        final Question newQuestion = new Question("title2", "contents2");
        question.writeBy(JAVAJIGI);

        question.update(JAVAJIGI, newQuestion);

        assertThat(question.getTitle()).isEqualTo(newQuestion.getTitle());
        assertThat(question.getContents()).isEqualTo(newQuestion.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_작성자_다를때() {
        final Question newQuestion = new Question("title2", "contents2");
        question.writeBy(JAVAJIGI);

        question.update(SANJIGI, newQuestion);

        assertThat(question.getTitle()).isEqualTo(newQuestion.getTitle());
        assertThat(question.getContents()).isEqualTo(newQuestion.getContents());
    }

    @Test
    public void addAnswer() {
        final String contents = "contents!!";
        final Answer answer = new Answer(JAVAJIGI, contents);
        final int beforeAnswersSize = question.answerCount();

        question.addAnswer(answer);

        final int afterAnswersSize = question.answerCount();
        softly.assertThat(afterAnswersSize).isEqualTo(beforeAnswersSize + 1);
        softly.assertThat(question.getAnswer(afterAnswersSize - 1)).isEqualTo(answer);
    }

    @Test
    public void addAnswer_동일_Answer_추가시() {
        final String contents = "contents!!";
        final Answer answer = new Answer(JAVAJIGI, contents);
        final int beforeAnswersSize = question.answerCount();


        question.addAnswer(answer);
        question.addAnswer(answer);


        softly.assertThat(question.answerCount()).isEqualTo(beforeAnswersSize + 1);
    }

    @Test
    public void isEqualQuestion() {
        final Long id = 1L;
        final String title = "title";
        final String contents = "contents";
        final Question origin = new Question(id, title, contents);
        final Question other = new Question(id, title, contents);

        softly.assertThat(origin.isEqualQuestion(other)).isTrue();
    }

    @Test
    public void isEqualQuestion_아이디_다를때() {
        final String title = "title";
        final String contents = "contents";
        final Question origin = new Question(1L, title, contents);
        final Question other = new Question(2L, title, contents);

        softly.assertThat(origin.isEqualQuestion(other)).isFalse();
    }

    @Test
    public void isEqualQuestion_제목_다를때() {
        final Long id = 1L;
        final String contents = "contents";
        final Question origin = new Question(id, " title1", contents);
        final Question other = new Question(id, "title2", contents);

        softly.assertThat(origin.isEqualQuestion(other)).isFalse();
    }

    @Test
    public void isEqualQuestion_내용_다를때() {
        final Long id = 1L;
        final String title = "title";
        final Question origin = new Question(id, title, "contents1");
        final Question other = new Question(id, title, "contents2");

        softly.assertThat(origin.isEqualQuestion(other)).isFalse();
    }

    @Test
    public void containsAnswer() {
        final String contents = "contents!!";
        final Answer answer = new Answer(1L, JAVAJIGI, question, contents);
        question.addAnswer(answer);

        boolean contains = question.containsAnswer(answer);

        softly.assertThat(contains).isTrue();
    }

    @Test
    public void containsAnswer_없을때() {
        final String contents = "contents!!";
        final Answer answer = new Answer(1L, JAVAJIGI, question, contents);
        final Answer another = new Answer(2L, JAVAJIGI, question, contents);
        question.addAnswer(answer);

        boolean answerContains = question.containsAnswer(answer);
        boolean anotherContains = question.containsAnswer(another);

        softly.assertThat(answerContains).isTrue();
        softly.assertThat(anotherContains).isFalse();
    }
}