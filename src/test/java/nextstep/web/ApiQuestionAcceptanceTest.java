package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);

    @Test
    public void create_login() throws Exception {
        User loginUser = defaultUser();
        Question newQuestion = new Question("title", "contents");
        ResponseEntity<Void> response = basicAuthTemplate(loginUser).postForEntity("/api/questions", newQuestion, Void.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        Question createdQuestion = getResource(location, Question.class, loginUser);
        softly.assertThat(createdQuestion).isNotNull();
    }

    @Test
    public void read_one_question() throws Exception {
        ResponseEntity<String> response = template().getForEntity(String.format("/api/questions/%d", defaultUser().getId()), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains(defaultUser().getName());
    }

    @Test
    public void update_my_question() throws Exception {
        User loginUser = defaultUser();
        Question newQuestion = new Question("title", "contents");
        ResponseEntity<Void> response = basicAuthTemplate(loginUser).postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        Question updateQuestion = new Question("update title", "update contents");

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate(loginUser).exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateQuestion.equalsTitleAndContents(responseEntity.getBody())).isTrue();
    }

    @Test
    public void delete_my_question() throws Exception {
        User loginUser = defaultUser();
        Question newQuestion = new Question("title", "contents");
        ResponseEntity<Void> response = basicAuthTemplate(loginUser).postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate(loginUser).exchange(location, HttpMethod.DELETE, createHttpEntity(newQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
