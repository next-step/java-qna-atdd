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
        String questionLocation = createResourceWithDefaultUser("/api/questions",
            new Question(QUESTION_WEATHER.getTitle(), QUESTION_WEATHER.getContents()));
        Answer newAnswer = new Answer("답변 내용");
        String answerLocation = createResourceWithDefaultUser(questionLocation + "/answers",
            newAnswer);
        softly.assertThat(answerLocation).startsWith(questionLocation + "/answers/");
        Answer dbAnswer = getResource(answerLocation, Answer.class, defaultUser());
        softly.assertThat(dbAnswer.getContents()).isEqualTo(newAnswer.getContents());
    }

    @Test
    public void create_guest() {
        String questionLocation = createResourceWithDefaultUser("/api/questions",
            new Question(QUESTION_WEATHER.getTitle(), QUESTION_WEATHER.getContents()));
        Answer newAnswer = new Answer("답변 내용");
        ResponseEntity<Answer> response = template()
            .postForEntity(questionLocation + "/answers", newAnswer, Answer.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update() {
        String questionLocation = createResourceWithDefaultUser("/api/questions",
            new Question(QUESTION_WEATHER.getTitle(), QUESTION_WEATHER.getContents()));

        Answer newAnswer = new Answer("답변 내용");
        String answerLocation = createResourceWithDefaultUser(questionLocation + "/answers",
            newAnswer);
        softly.assertThat(answerLocation).startsWith(questionLocation + "/answers/");

        Answer updated = new Answer("답변 내용 수정");
        ResponseEntity<Answer> responseEntity = basicAuthTemplate()
            .exchange(answerLocation, HttpMethod.PUT, createHttpEntity(updated), Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updated.getContents())
            .isEqualTo(getResource(answerLocation, Answer.class, defaultUser()).getContents());
    }

    @Test
    public void update_not_owner() {
        String questionLocation = createResourceWithDefaultUser("/api/questions",
            new Question(QUESTION_WEATHER.getTitle(), QUESTION_WEATHER.getContents()));

        Answer newAnswer = new Answer("답변 내용");
        String answerLocation = createResourceWithDefaultUser(questionLocation + "/answers",
            newAnswer);
        softly.assertThat(answerLocation).startsWith(questionLocation + "/answers/");

        Answer updated = new Answer("답변 내용 수정");
        ResponseEntity<Answer> responseEntity = basicAuthTemplate(newUser("sanjigi"))
            .exchange(answerLocation, HttpMethod.PUT, createHttpEntity(updated), Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() {
        String questionLocation = createResourceWithDefaultUser("/api/questions",
            new Question(QUESTION_WEATHER.getTitle(), QUESTION_WEATHER.getContents()));
        Answer newAnswer = new Answer("답변 내용");
        String answerLocation = createResourceWithDefaultUser(questionLocation + "/answers",
            newAnswer);
        softly.assertThat(answerLocation).startsWith(questionLocation + "/answers/");

        ResponseEntity<Answer> responseEntity = basicAuthTemplate()
            .exchange(answerLocation, HttpMethod.DELETE, createHttpEntity(newAnswer), Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_not_owner() {
        String questionLocation = createResourceWithDefaultUser("/api/questions",
            new Question(QUESTION_WEATHER.getTitle(), QUESTION_WEATHER.getContents()));
        Answer newAnswer = new Answer("답변 내용");
        String answerLocation = createResourceWithDefaultUser(questionLocation + "/answers",
            newAnswer);
        softly.assertThat(answerLocation).startsWith(questionLocation + "/answers/");

        ResponseEntity<Answer> responseEntity = template()
            .exchange(answerLocation, HttpMethod.DELETE, createHttpEntity(newAnswer), Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}