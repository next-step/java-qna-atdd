package nextstep.web;


import nextstep.domain.Answer;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);

    @Test
    public void add_answer() throws Exception {
        User loginUser = defaultUser();
        Answer newAnswer = new Answer(loginUser, "test contents");
        ResponseEntity<Void> response = basicAuthTemplate(loginUser).postForEntity("/api/answers/1", newAnswer, Void.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        Answer createdAnswer = basicAuthTemplate(findByUserId(loginUser.getUserId())).getForObject(location, Answer.class);
        softly.assertThat(createdAnswer).isNotNull();
    }

    @Test
    public void update_my_answer() throws Exception {
        User loginUser = defaultUser();
        Answer newAnswer = new Answer(loginUser, "test contents");
        ResponseEntity<Void> response = basicAuthTemplate(loginUser).postForEntity("/api/answers/1", newAnswer, Void.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        Answer updateAnswer = new Answer(loginUser, "update test contents");
        ResponseEntity<Answer> responseEntity =
                basicAuthTemplate(loginUser).exchange(location, HttpMethod.PUT, createHttpEntity(updateAnswer), Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_my_answer() throws Exception {
        User loginUser = defaultUser();
        Answer newAnswer = new Answer(loginUser, "test contents");
        ResponseEntity<Void> response = basicAuthTemplate(loginUser).postForEntity("/api/answers/1", newAnswer, Void.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        ResponseEntity<Answer> responseEntity =
                basicAuthTemplate(loginUser).exchange(location, HttpMethod.DELETE, createHttpEntity(newAnswer), Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
