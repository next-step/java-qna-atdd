package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public void delete_답변_없을때() {
        final boolean beforeState = question.isDeleted();
        final User writer = new User(1L, "id", "password", "name", "email");
        question.writeBy(writer);

        question.delete(writer);

        softly.assertThat(beforeState).isFalse();
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test
    public void delete_동일_작성자_답변만_있을때() {
        final User writer = new User(1L, "id", "password", "name", "email");
        question.writeBy(writer);
        final List<Answer> answers = createSameWriterAnswers(writer);
        question.addAllAnswer(answers);

        final List<DeleteHistory> deleteHistories = question.delete(writer);
        final Map<ContentType, List<DeleteHistory>> historyMap = deleteHistories.stream().collect(Collectors.groupingBy(DeleteHistory::getContentType, Collectors.toList()));

        softly.assertThat(question.isDeleted()).isTrue();
        softly.assertThat(question.getAnswers()).allSatisfy(answer -> assertThat(answer.isDeleted()).isTrue());
        softly.assertThat(historyMap.get(ContentType.QUESTION).size()).isEqualTo(1);
        softly.assertThat(historyMap.get(ContentType.ANSWER).size()).isEqualTo(answers.size());
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_다른_작성자_답변_있을때() {
        final User writer = new User(1L, "id", "password", "name", "email");
        question.writeBy(writer);
        final List<Answer> answers = createSameWriterAnswers(writer);
        question.addAllAnswer(answers);
        final User anotherWriter = new User(2L, "id", "password", "name", "email");
        final Answer anotherWriterAnswer = new Answer((long) (answers.size() + 1), anotherWriter, question, "contents!!");
        question.addAnswer(anotherWriterAnswer);


        question.delete(writer);
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

    @Test
    public void addAnswer() {
        final User writer = new User(1L, "id", "password", "name", "email");
        final String contents = "contents!!";
        final Answer answer = new Answer(writer, contents);
        final int beforeAnswersSize = question.getAnswers().size();

        question.addAnswer(answer);

        final List<Answer> answers = question.getAnswers();
        final int afterAnswersSize = answers.size();
        softly.assertThat(afterAnswersSize).isEqualTo(beforeAnswersSize + 1);
        softly.assertThat(answers.get(afterAnswersSize - 1)).isEqualTo(answer);
    }

    @Test
    public void addAnswer_동일_Answer_추가시() {
        final User writer = new User(1L, "id", "password", "name", "email");
        final String contents = "contents!!";
        final Answer answer = new Answer(writer, contents);
        final int beforeAnswersSize = question.getAnswers().size();


        question.addAnswer(answer);
        question.addAnswer(answer);


        softly.assertThat(question.getAnswers().size()).isEqualTo(beforeAnswersSize + 1);
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
        final User writer = new User(1L, "id", "password", "name", "email");
        final String contents = "contents!!";
        final Answer answer = new Answer(1L, writer, question, contents);
        question.addAnswer(answer);

        boolean contains = question.containsAnswer(answer);

        softly.assertThat(contains).isTrue();
    }

    @Test
    public void containsAnswer_없을때() {
        final User writer = new User(1L, "id", "password", "name", "email");
        final String contents = "contents!!";
        final Answer answer = new Answer(1L, writer, question, contents);
        final Answer another = new Answer(2L, writer, question, contents);
        question.addAnswer(answer);

        boolean answerContains = question.containsAnswer(answer);
        boolean anotherContains = question.containsAnswer(another);

        softly.assertThat(answerContains).isTrue();
        softly.assertThat(anotherContains).isFalse();
    }
}