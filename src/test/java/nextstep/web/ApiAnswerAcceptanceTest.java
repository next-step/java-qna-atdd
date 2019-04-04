package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static nextstep.domain.QuestionTest.QUESTION_WEATHER;
import static nextstep.domain.UserTest.newUser;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    @Test
    public void create() {
        Question newQuestion = new Question(QUESTION_WEATHER.getTitle(), QUESTION_WEATHER.getContents());
        Answer newAnaswer = new Answer("답변 내용");

        ResponseEntity<Void> questionResponse = basicAuthTemplate().postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(questionResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = questionResponse.getHeaders().getLocation().getPath();

        ResponseEntity<Answer> response =
                basicAuthTemplate().postForEntity(location + "/answers", newAnaswer, Answer.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void create_guest() {
        Question newQuestion = new Question(QUESTION_WEATHER.getTitle(), QUESTION_WEATHER.getContents());
        Answer newAnswer = new Answer("답변 내용");

        ResponseEntity<Void> questionResponse = basicAuthTemplate().postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(questionResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = questionResponse.getHeaders().getLocation().getPath();

        ResponseEntity<Answer> response =
                template().postForEntity(location + "/answers", newAnswer, Answer.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update() {
        Question newQuestion = new Question(QUESTION_WEATHER.getTitle(), QUESTION_WEATHER.getContents());
        Answer newAnswer = new Answer("답변 내용");

        ResponseEntity<Void> questionResponse = basicAuthTemplate().postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(questionResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = questionResponse.getHeaders().getLocation().getPath();

        ResponseEntity<Answer> answerResponse =
                basicAuthTemplate().postForEntity(location + "/answers/", newAnswer, Answer.class);
        softly.assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String answerLocation = answerResponse.getHeaders().getLocation().getPath();

        Answer original = basicAuthTemplate().getForObject(answerLocation, Answer.class);
        Answer updated = new Answer(original.getId(), defaultUser(), newQuestion, "답변 내용 수정");

        ResponseEntity<Answer> responseEntity =
                basicAuthTemplate().exchange(answerLocation, HttpMethod.PUT, createHttpEntity(updated), Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void update_not_owner() {
        Question newQuestion = new Question(QUESTION_WEATHER.getTitle(), QUESTION_WEATHER.getContents());
        Answer newAnaswer = new Answer("답변 내용");

        ResponseEntity<Void> questionResponse = basicAuthTemplate().postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(questionResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = questionResponse.getHeaders().getLocation().getPath();

        ResponseEntity<Answer> answerResponse =
                basicAuthTemplate().postForEntity(location + "/answers/", newAnaswer, Answer.class);
        softly.assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String answerLocation = answerResponse.getHeaders().getLocation().getPath();

        Answer original = basicAuthTemplate(newUser("sanjigi", "test")).getForObject(answerLocation, Answer.class);
        Answer updated = new Answer(original.getId(), defaultUser(), newQuestion, "답변 내용 수정");

        ResponseEntity<Answer> responseEntity =
                template().exchange(answerLocation, HttpMethod.PUT, createHttpEntity(updated), Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() {
        Question newQuestion = new Question(QUESTION_WEATHER.getTitle(), QUESTION_WEATHER.getContents());
        Answer newAnswer = new Answer("답변 내용");

        ResponseEntity<Void> questionResponse = basicAuthTemplate().postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(questionResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = questionResponse.getHeaders().getLocation().getPath();

        ResponseEntity<Answer> answerResponse =
                basicAuthTemplate().postForEntity(location + "/answers/", newAnswer, Answer.class);
        softly.assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String answerLocation = answerResponse.getHeaders().getLocation().getPath();

        ResponseEntity<Answer> responseEntity =
                basicAuthTemplate().exchange(answerLocation, HttpMethod.DELETE, createHttpEntity(newAnswer), Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_not_owner() {
        Question newQuestion = new Question(QUESTION_WEATHER.getTitle(), QUESTION_WEATHER.getContents());
        Answer newAnswer = new Answer("답변 내용");

        ResponseEntity<Void> questionResponse = basicAuthTemplate().postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(questionResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = questionResponse.getHeaders().getLocation().getPath();

        ResponseEntity<Answer> answerResponse =
                basicAuthTemplate().postForEntity(location + "/answers/", newAnswer, Answer.class);
        softly.assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String answerLocation = answerResponse.getHeaders().getLocation().getPath();

        ResponseEntity<Answer> responseEntity =
                template().exchange(answerLocation, HttpMethod.DELETE, createHttpEntity(newAnswer), Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}