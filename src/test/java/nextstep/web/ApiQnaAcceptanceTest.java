package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.UserTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.util.stream.StreamSupport;

public class ApiQnaAcceptanceTest extends AcceptanceTest {
    private static final String QUESTION_URL = "/api/questions";
    private Question question = new Question("new title", "new contents");
    private Question updateQuestion = new Question("update title", "update contents");
    private Answer answer = new Answer(1L, UserTest.JAVAJIGI, this.question, "answer");
    private String location;
    private String questionLocation;

    @Before
    public void init() {
        this.question.writeBy(defaultUser());
        this.updateQuestion.writeBy(defaultUser());
        this.location = createResourceBasicAuth(QUESTION_URL, this.question);
        this.questionLocation = createResourceBasicAuth(QUESTION_URL, this.question);
    }

    @Test
    public void list() {
        Iterable questions = getResource(QUESTION_URL, Iterable.class, defaultUser());
        softly.assertThat(StreamSupport.stream(questions.spliterator(),false).count()).isGreaterThanOrEqualTo(0);
    }

    @Test
    public void create_question() {
        Question dbQuestion = getResource(this.location, Question.class, defaultUser());
        softly.assertThat(dbQuestion.getTitle()).isEqualTo(this.question.getTitle());
        softly.assertThat(dbQuestion.getContents()).isEqualTo(this.question.getContents());
    }

    @Test
    public void create_question_no_login() {
        ResponseEntity<Void> response = template().postForEntity(QUESTION_URL, this.question, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update_question() {
        ResponseEntity<Question> responseEntity = basicAuthTemplate(defaultUser())
                .exchange(this.location, HttpMethod.PUT, createHttpEntity(this.updateQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(this.updateQuestion.getTitle()).isEqualTo(responseEntity.getBody().getTitle());
        softly.assertThat(this.updateQuestion.getContents()).isEqualTo(responseEntity.getBody().getContents());
    }

    @Test
    public void update_question_no_login() {
        ResponseEntity<Question> responseEntity = template().exchange(this.location, HttpMethod.PUT, createHttpEntity(this.updateQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update_question_not_owner() {
        ResponseEntity<Question> responseEntity = basicAuthTemplate(UserTest.SANJIGI)
                .exchange(this.location, HttpMethod.PUT, createHttpEntity(this.updateQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() {
        ResponseEntity<Question> responseEntity = basicAuthTemplate(defaultUser())
                .exchange(this.location, HttpMethod.DELETE, createHttpEntity(this.question), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_question_no_login() {
        ResponseEntity<Question> responseEntity = template()
                .exchange(this.location, HttpMethod.DELETE, createHttpEntity(this.question), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_not_owner() {
        ResponseEntity<Question> responseEntity = basicAuthTemplate(UserTest.SANJIGI)
                .exchange(this.location, HttpMethod.DELETE, createHttpEntity(this.question), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void create_answer() {
        String answerLocation = createResourceBasicAuth(this.questionLocation + "/answers", this.answer);
        softly.assertThat(answerLocation).startsWith(this.questionLocation + "/answers/");

        Answer dbAnswer = getResource(answerLocation, Answer.class, defaultUser());
        softly.assertThat(dbAnswer.getContents()).isEqualTo(this.answer.getContents());
    }


    @Test
    public void create_answer_no_login() {
        String questionLocation = createResourceBasicAuth(QUESTION_URL, this.question);
        ResponseEntity<Void> response = template().postForEntity(questionLocation + "/answers", this.answer, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_answer() {
        String answerLocation = createResourceBasicAuth(this.questionLocation + "/answers", this.answer);
        softly.assertThat(answerLocation).startsWith(this.questionLocation + "/answers/");

        ResponseEntity<Answer> responseEntity = basicAuthTemplate()
                .exchange(answerLocation, HttpMethod.DELETE, createHttpEntity(this.answer), Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_answer_no_login() {
        String answerLocation = createResourceBasicAuth(this.questionLocation + "/answers", this.answer);
        softly.assertThat(answerLocation).startsWith(this.questionLocation + "/answers/");

        ResponseEntity<Answer> responseEntity = template()
                .exchange(answerLocation, HttpMethod.DELETE, createHttpEntity(this.answer), Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_answer_not_owner() {
        String answerLocation = createResourceBasicAuth(this.questionLocation + "/answers", this.answer);
        softly.assertThat(answerLocation).startsWith(this.questionLocation + "/answers/");

        ResponseEntity<Answer> responseEntity = basicAuthTemplate(UserTest.SANJIGI)
                .exchange(answerLocation, HttpMethod.DELETE, createHttpEntity(this.answer), Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
