package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    @Test
    public void create() throws Exception {
        Question expected = new Question("title", "contents");

        ResponseEntity<Void> response = createQuestion(defaultUser(), expected);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        Question actual = basicAuthTemplate(defaultUser()).getForObject(location, Question.class);
        softly.assertThat(actual).isNotNull();
        softly.assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        softly.assertThat(actual.getContents()).isEqualTo(expected.getContents());
    }

    @Test
    public void create_not_login_user() throws Exception {
        Question question = new Question("title", "contents");

        ResponseEntity<Void> response = createQuestion(null, question);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void show() throws Exception {
        long expectedId = 1L;

        ResponseEntity<Question> response = template().getForEntity(
                String.format("/api/questions/%d", expectedId),
                Question.class
        );

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Question actual = response.getBody();
        softly.assertThat(actual).isNotNull();
        softly.assertThat(actual.getId()).isEqualTo(expectedId);
    }

    @Test
    public void show_for_wrong_question_id() throws Exception {
        long questionId = 100L;

        ResponseEntity<Question> response = template().getForEntity(
                String.format("/api/questions/%d", questionId),
                Question.class
        );

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void update() throws Exception {
        Question question = new Question("original title", "original contents");
        String location = createResource(defaultUser(), "/api/questions", question);
        Question original = getResource(location, Question.class, defaultUser());

        Question expected = new Question(original.getId(), "updated title", "updated contents", defaultUser());

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate(defaultUser()).exchange(location, HttpMethod.PUT, createHttpEntity(expected), Question.class);
        Question actual = responseEntity.getBody();

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(actual).isNotNull();
        softly.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void update_for_wrong_user() throws Exception {
        Question question = new Question("original title", "original contents");
        String location = createResource(defaultUser(), "/api/questions", question);
        Question original = getResource(location, Question.class, defaultUser());

        Question expected = new Question(original.getId(), "updated title", "updated contents", defaultUser());

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate(anotherUser()).exchange(location, HttpMethod.PUT, createHttpEntity(expected), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_for_not_login_user() throws Exception {
        Question question = new Question("original title", "original contents");
        String location = createResource(defaultUser(), "/api/questions", question);
        Question original = getResource(location, Question.class, defaultUser());

        Question expected = new Question(original.getId(), "updated title", "updated contents", defaultUser());

        ResponseEntity<Question> responseEntity =
                template().exchange(location, HttpMethod.PUT, createHttpEntity(expected), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void delete() throws Exception {
        long questionId = 1L;

        ResponseEntity<Void> responseEntity = basicAuthTemplate(defaultUser())
                .exchange(
                        String.format("/api/questions/%d", questionId),
                        HttpMethod.DELETE,
                        createHttpEntity(null),
                        Void.class
                );

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_for_wrong_question() throws Exception {
        long questionId = 100L;

        ResponseEntity<Void> responseEntity = basicAuthTemplate(defaultUser())
                .exchange(
                        String.format("/api/questions/%d", questionId),
                        HttpMethod.DELETE,
                        createHttpEntity(null),
                        Void.class
                );

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void delete_for_not_login_user() throws Exception {
        long questionId = 1L;

        ResponseEntity<Void> responseEntity = template()
                .exchange(
                        String.format("/api/questions/%d", questionId),
                        HttpMethod.DELETE,
                        createHttpEntity(null),
                        Void.class
                );

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<Void> createQuestion(User loginUser, Question question) {
        TestRestTemplate testRestTemplate = ( loginUser != null ) ? basicAuthTemplate(loginUser) : template();

        return testRestTemplate.postForEntity(
                "/api/questions/",
                question,
                Void.class
        );
    }
}
