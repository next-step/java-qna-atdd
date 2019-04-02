package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.User;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    @Test
    public void create() throws Exception {
        long questionId = 1L;
        String contents = "test answer contents";

        ResponseEntity<Void> response = createAnswer(defaultUser(), contents, questionId);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        Answer dbAnswer = basicAuthTemplate(defaultUser()).getForObject(location, Answer.class);
        softly.assertThat(dbAnswer).isNotNull();
    }

    @Test
    public void create_for_wrong_question_id() throws Exception {
        long questionId = 100L;
        String contents = "test answer contents";

        ResponseEntity<Void> response = createAnswer(defaultUser(), contents, questionId);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void create_for_not_login_user() throws Exception {
        long questionId = 100L;
        String contents = "test answer contents";

        ResponseEntity<Void> response = createAnswer(null, contents, questionId);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<Void> createAnswer(User loginUser, String contents, long questionId) {
        TestRestTemplate testRestTemplate = ( loginUser != null ) ? basicAuthTemplate(loginUser) : template();

        return testRestTemplate.postForEntity(
            String.format("/api/questions/%d/answers", questionId),
            contents,
            Void.class
        );
    }

    @Test
    public void show() throws Exception {
        long questionId = 1L;
        long answerId = 1L;

        ResponseEntity<Answer> response = template().getForEntity(
                                                String.format("/api/questions/%d/answers/%d", questionId, answerId),
                                                Answer.class
                                            );

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Answer actualAnswer = response.getBody();
        softly.assertThat(actualAnswer).isNotNull();
        softly.assertThat(actualAnswer.getId()).isEqualTo(answerId);
        softly.assertThat(actualAnswer.getQuestion().getId()).isEqualTo(questionId);
    }

    @Test
    public void show_for_wrong_question_id() throws Exception {
        long questionId = 100L;
        long answerId = 1L;

        ResponseEntity<Answer> response = template().getForEntity(
                String.format("/api/questions/%d/answers/%d", questionId, answerId),
                Answer.class
        );

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void show_for_wrong_answer_id() throws Exception {
        long questionId = 1L;
        long answerId = 100L;

        ResponseEntity<Answer> response = template().getForEntity(
                String.format("/api/questions/%d/answers/%d", questionId, answerId),
                Answer.class
        );

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void show_for_wrongly_matched_question_answer_pair() throws Exception {
        long questionId = 2L;
        long answerId = 1L;

        ResponseEntity<Answer> response = template().getForEntity(
                String.format("/api/questions/%d/answers/%d", questionId, answerId),
                Answer.class
        );

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void update() throws Exception {
        long questionId = 1L;
        String originalContents = "original contents";

        String location = createResource(defaultUser(), String.format("/api/questions/%d/answers", questionId), originalContents);
        Answer original = getResource(location, Answer.class, defaultUser());

        String updatedContents = "updated contents";
        Answer updatedAnswer = new Answer(original.getId(), original.getWriter(), original.getQuestion(), updatedContents);

        ResponseEntity<Answer> responseEntity =
                basicAuthTemplate(defaultUser()).exchange(location, HttpMethod.PUT, createHttpEntity(updatedAnswer), Answer.class);
        Answer responseAnswer = responseEntity.getBody();

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseAnswer).isNotNull();
        softly.assertThat(responseAnswer).isEqualTo(updatedAnswer);
    }

    @Test
    public void update_for_wrong_user() throws Exception {
        long questionId = 1L;
        String originalContents = "original contents";

        String location = createResource(defaultUser(), String.format("/api/questions/%d/answers", questionId), originalContents );
        Answer original = getResource(location, Answer.class, defaultUser());

        String updatedContents = "updated contents";
        Answer updatedAnswer = new Answer(original.getId(), original.getWriter(), original.getQuestion(), updatedContents);

        ResponseEntity<Answer> responseEntity =
                basicAuthTemplate(anotherUser()).exchange(location, HttpMethod.PUT, createHttpEntity(updatedAnswer), Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_for_not_login_user() throws Exception {
        long questionId = 1L;
        String originalContents = "original contents";

        String location = createResource(defaultUser(), String.format("/api/questions/%d/answers", questionId), originalContents );
        Answer original = getResource(location, Answer.class, defaultUser());

        String updatedContents = "updated contents";
        Answer updatedAnswer = new Answer(original.getId(), original.getWriter(), original.getQuestion(), updatedContents);

        ResponseEntity<Answer> responseEntity =
                template().exchange(location, HttpMethod.PUT, createHttpEntity(updatedAnswer), Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void delete() throws Exception {
        long questionId = 1L;
        long answerId = 1L;

        ResponseEntity<Void> responseEntity = basicAuthTemplate(defaultUser())
                                                .exchange(
                                                        String.format("/api/questions/%d/answers/%d", questionId, answerId),
                                                        HttpMethod.DELETE,
                                                        createHttpEntity(null),
                                                        Void.class
                                                );

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_for_wrong_question() throws Exception {
        long questionId = 100L;
        long answerId = 1L;

        ResponseEntity<Void> responseEntity = basicAuthTemplate(defaultUser())
                .exchange(
                        String.format("/api/questions/%d/answers/%d", questionId, answerId),
                        HttpMethod.DELETE,
                        createHttpEntity(null),
                        Void.class
                );

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void delete_for_wrong_answer() throws Exception {
        long questionId = 1L;
        long answerId = 100L;

        ResponseEntity<Void> responseEntity = basicAuthTemplate(defaultUser())
                .exchange(
                        String.format("/api/questions/%d/answers/%d", questionId, answerId),
                        HttpMethod.DELETE,
                        createHttpEntity(null),
                        Void.class
                );

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void delete_for_wrongly_matched_question_answer() throws Exception {
        long questionId = 2L;
        long answerId = 1L;

        ResponseEntity<Void> responseEntity = basicAuthTemplate(defaultUser())
                .exchange(
                        String.format("/api/questions/%d/answers/%d", questionId, answerId),
                        HttpMethod.DELETE,
                        createHttpEntity(null),
                        Void.class
                );

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void delete_for_not_login_user() throws Exception {
        long questionId = 2L;
        long answerId = 1L;

        ResponseEntity<Void> responseEntity = template()
                .exchange(
                        String.format("/api/questions/%d/answers/%d", questionId, answerId),
                        HttpMethod.DELETE,
                        createHttpEntity(null),
                        Void.class
                );

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
