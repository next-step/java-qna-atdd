package nextstep.web;

import static nextstep.domain.QuestionTest.QUESTION_WEATHER;
import static nextstep.domain.UserTest.newUser;

import java.util.stream.StreamSupport;
import nextstep.domain.Question;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Test
    public void list() {
        Iterable questions = getResource("/api/questions", Iterable.class, defaultUser());
        softly.assertThat(StreamSupport.stream(questions.spliterator(), false).count())
            .isGreaterThanOrEqualTo(0);
    }

    @Test
    public void create() {
        Question newQuestion = new Question(QUESTION_WEATHER.getTitle(),
                                            QUESTION_WEATHER.getContents());
        String location = createResourceWithDefaultUser("/api/questions", newQuestion);
        softly.assertThat(location).startsWith("/api/questions/");
        Question dbQuestion = getResource(location, Question.class, defaultUser());
        softly.assertThat(newQuestion.equalsTitleAndContents(dbQuestion)).isTrue();
    }

    @Test
    public void create_no_login() {
        Question newQuestion = new Question(QUESTION_WEATHER.getTitle(),
                                            QUESTION_WEATHER.getContents());

        ResponseEntity<Void> response = template()
            .postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update() {
        Question newQuestion = new Question(QUESTION_WEATHER.getTitle(),
                                            QUESTION_WEATHER.getContents());
        String location = createResourceWithDefaultUser("/api/questions", newQuestion);
        Question original = getResource(location, Question.class, defaultUser());
        Question updatedQuestion = new Question(original.getTitle() + " Update !",
                                                "Contents update !!!");

        ResponseEntity<Question> responseEntity = basicAuthTemplate()
            .exchange(location, HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updatedQuestion.equalsTitleAndContents(responseEntity.getBody()))
            .isTrue();
    }

    @Test
    public void update_no_login() {
        Question newQuestion = new Question(QUESTION_WEATHER.getTitle(),
                                            QUESTION_WEATHER.getContents());
        String location = createResourceWithDefaultUser("/api/questions", newQuestion);
        Question original = getResource(location, Question.class, defaultUser());
        Question updatedQuestion = new Question(original.getTitle() + " Update !",
                                                "Contents update !!!");

        ResponseEntity<Question> responseEntity = template()
            .exchange(location, HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update_not_owner() {
        Question newQuestion = new Question(QUESTION_WEATHER.getTitle(),
                                            QUESTION_WEATHER.getContents());
        String location = createResourceWithDefaultUser("/api/questions", newQuestion);
        Question original = getResource(location, Question.class, defaultUser());
        Question updatedQuestion = new Question(original.getTitle() + " Update !",
                                                "Contents update !!!");

        ResponseEntity<Question> responseEntity = basicAuthTemplate(newUser("sanjigi", "test"))
            .exchange(location, HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete() {
        Question newQuestion = new Question(QUESTION_WEATHER.getTitle(),
                                            QUESTION_WEATHER.getContents());
        String location = createResourceWithDefaultUser("/api/questions", newQuestion);
        Question original = getResource(location, Question.class, defaultUser());

        ResponseEntity<Question> responseEntity = basicAuthTemplate()
            .exchange(location, HttpMethod.DELETE, createHttpEntity(original), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_no_login() {
        Question newQuestion = new Question(QUESTION_WEATHER.getTitle(),
                                            QUESTION_WEATHER.getContents());
        String location = createResourceWithDefaultUser("/api/questions", newQuestion);
        Question original = getResource(location, Question.class, defaultUser());

        ResponseEntity<Question> responseEntity = template()
            .exchange(location, HttpMethod.DELETE, createHttpEntity(original), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_not_owner() {
        Question newQuestion = new Question(QUESTION_WEATHER.getTitle(),
                                            QUESTION_WEATHER.getContents());

        String location = createResourceWithDefaultUser("/api/questions", newQuestion);
        Question original = getResource(location, Question.class, defaultUser());

        ResponseEntity<Question> responseEntity = basicAuthTemplate(newUser("sanjigi", "test"))
            .exchange(location, HttpMethod.DELETE, createHttpEntity(original), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}