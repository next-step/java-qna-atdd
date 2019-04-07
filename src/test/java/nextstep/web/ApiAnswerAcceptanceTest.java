package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static nextstep.domain.UserTest.newUser;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static Question QUESTION = new Question("오늘의 날씨는?", "강수 확률 높습니다! 우산 챙기세요");
    private static final Answer ANSWER = new Answer("contents...");

    private String questionLocation;

    @Before
    public void setUp() {
        questionLocation = createResource("/api/questions", QUESTION, defaultUser());
    }

    @Test
    public void create() {
        final String answerLocation = createResource(questionLocation + "/answers", ANSWER, defaultUser());
        softly.assertThat(answerLocation).startsWith(questionLocation + "/answers/");
        final Answer dbAnswer = getResource(answerLocation, Answer.class, defaultUser());
        softly.assertThat(dbAnswer.getContents()).isEqualTo(ANSWER.getContents());
    }

    @Test
    public void create_guest() {
        final ResponseEntity<Answer> response = template()
                .postForEntity(questionLocation + "/answers", ANSWER, Answer.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update() {
        final String answerLocation = createResource(questionLocation + "/answers", ANSWER, defaultUser());
        softly.assertThat(answerLocation).startsWith(questionLocation + "/answers/");

        final Answer updated = ANSWER.setContents("UPDATE !!!");
        final ResponseEntity<Answer> responseEntity = basicAuthTemplate()
            .exchange(answerLocation, HttpMethod.PUT, createHttpEntity(updated), Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        final Answer dbAnswer = getResource(answerLocation, Answer.class, defaultUser());
        softly.assertThat(dbAnswer.equalsContents(updated)).isEqualTo(true);
    }

    @Test
    public void update_not_owner() {
        final String answerLocation = createResourceWithDefaultUser(questionLocation + "/answers", ANSWER);
        softly.assertThat(answerLocation).startsWith(questionLocation + "/answers/");

        final Answer updated = ANSWER.setContents("답변 내용 수정");
        final ResponseEntity<Answer> responseEntity = basicAuthTemplate(newUser("sanjigi"))
            .exchange(answerLocation, HttpMethod.PUT, createHttpEntity(updated), Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() {
        final String answerLocation = createResourceWithDefaultUser(questionLocation + "/answers", ANSWER);
        softly.assertThat(answerLocation).startsWith(questionLocation + "/answers/");

        final ResponseEntity<Answer> responseEntity = basicAuthTemplate()
                .exchange(answerLocation, HttpMethod.DELETE, createHttpEntity(ANSWER), Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_not_owner() {
        final String answerLocation = createResourceWithDefaultUser(questionLocation + "/answers", ANSWER);
        softly.assertThat(answerLocation).startsWith(questionLocation + "/answers/");

        final ResponseEntity<Answer> responseEntity = template()
                .exchange(answerLocation, HttpMethod.DELETE, createHttpEntity(ANSWER), Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}