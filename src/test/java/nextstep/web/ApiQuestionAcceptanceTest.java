package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static nextstep.domain.QuestionTest.newQuestion;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Test
    public void create() throws Exception {
        User loginUser = defaultUser();
        String location = createQuestion(loginUser);

        Question dbQuestion = selectQuestion(location);
        softly.assertThat(dbQuestion).isNotNull();
    }

    private String createQuestion(User loginUser) {
        Question newQuestion = newQuestion();

        ResponseEntity<Void> response = basicAuthTemplate(loginUser)
                .postForEntity("/api/questions", newQuestion, Void.class);

        String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return location;
    }

    private Question selectQuestion(String location) {
        return template().getForObject(location, Question.class);
    }

    @Test
    public void create_no_login() throws Exception {
        Question newQuestion = newQuestion();

        ResponseEntity<Void> response = template()
                .postForEntity("/api/questions", newQuestion, Void.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update() throws Exception {
        User loginUser = defaultUser();
        String location = createQuestion(loginUser);

        Question original = selectQuestion(location);
        Question updateQuestion = new Question("제목2", "내용2")
                .setId(original.getId());

        ResponseEntity<Question> responseEntity = basicAuthTemplate(loginUser)
                .exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateQuestion.equalsTitleAndContents(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_no_login() throws Exception {
        User loginUser = defaultUser();
        String location = createQuestion(loginUser);

        Question original = selectQuestion(location);
        Question updateQuestion = new Question("제목2", "내용2")
                .setId(original.getId());

        ResponseEntity<String> responseEntity = template()
                .exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }

    @Test
    public void delete() throws Exception {
        User loginUser = defaultUser();
        String location = createQuestion(loginUser);

        Question original = selectQuestion(location);

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .exchange(location, HttpMethod.DELETE, null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_no_login() throws Exception {
        User loginUser = defaultUser();
        String location = createQuestion(loginUser);

        Question original = selectQuestion(location);

        ResponseEntity<String> response = template()
                .exchange(location, HttpMethod.DELETE, null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
