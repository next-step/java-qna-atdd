package nextstep.web;

import nextstep.domain.Question;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.util.stream.StreamSupport;

import static nextstep.domain.UserTest.newUser;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static Question QUESTION = new Question("오늘의 날씨는?", "강수 확률 높습니다! 우산 챙기세요");

    @Test
    public void list() {
        final Iterable questions = getResource("/api/questions", Iterable.class, defaultUser());
        softly.assertThat(StreamSupport.stream(questions.spliterator(), false).count())
                .isGreaterThanOrEqualTo(0);
    }

    @Test
    public void create() {
        final String location = createResourceWithDefaultUser("/api/questions", QUESTION);
        softly.assertThat(location).startsWith("/api/questions/");
        final Question dbQuestion = getResource(location, Question.class, defaultUser());
        softly.assertThat(dbQuestion.equalsTitleAndContents(QUESTION)).isTrue();
    }

    @Test
    public void create_no_login() {
        final ResponseEntity<Void> response = template()
                .postForEntity("/api/questions", QUESTION, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update() {
        final String location = createResourceWithDefaultUser("/api/questions", QUESTION);
        final Question updatedQuestion = QUESTION
                .setTitle("title update~~~")
                .setContents("contents~~~~~~~~!!");
        final ResponseEntity<Question> responseEntity = basicAuthTemplate()
                .exchange(location, HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getBody().equalsTitleAndContents(updatedQuestion)).isTrue();
    }

    @Test
    public void update_no_login() {
        final String location = createResource("/api/questions", QUESTION, defaultUser());
        final Question updatedQuestion = QUESTION
                .setTitle(" Update !")
                .setContents("Contents update !!!");
        final ResponseEntity<Question> responseEntity = template()
                .exchange(location, HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update_not_owner() {
        final String location = createResource("/api/questions", QUESTION, defaultUser());
        final Question updatedQuestion = QUESTION
                .setTitle(" Update !")
                .setContents("Contents update !!!");
        final ResponseEntity<Question> responseEntity = basicAuthTemplate(newUser("sanjigi", "test"))
                .exchange(location, HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete() {
        final String location = createResource("/api/questions", QUESTION, defaultUser());
        final Question original = getResource(location, Question.class, defaultUser());
        final ResponseEntity<Question> responseEntity = basicAuthTemplate(defaultUser())
                .exchange(location, HttpMethod.DELETE, createHttpEntity(original), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_no_login() {
        final String location = createResource("/api/questions", QUESTION, defaultUser());
        final Question original = getResource(location, Question.class, defaultUser());
        final ResponseEntity<Question> responseEntity = template()
                .exchange(location, HttpMethod.DELETE, createHttpEntity(original), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_not_owner() {
        final String location = createResource("/api/questions", QUESTION, defaultUser());
        final Question original = getResource(location, Question.class, defaultUser());
        final ResponseEntity<Question> responseEntity = basicAuthTemplate(newUser("sanjigi", "test"))
                .exchange(location, HttpMethod.DELETE, createHttpEntity(original), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}