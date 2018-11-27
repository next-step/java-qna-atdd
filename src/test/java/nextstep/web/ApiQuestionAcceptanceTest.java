package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionTest;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static nextstep.domain.QuestionTest.updatedQuestion;
import static org.springframework.http.HttpStatus.*;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Test
    public void create() {
        //given,when
        User loginUser = defaultUser();
        String location = createQuestion(loginUser);
        Question dbQuestion = selectQuestion(location);

        //then
        softly.assertThat(dbQuestion).isNotNull();
    }

    private String createQuestion(User loginUser) {
        Question newQuestion = QuestionTest.newTestQuestion();

        ResponseEntity<Void> response = basicAuthTemplate(loginUser)
                .postForEntity("/api/questions", newQuestion, Void.class);

        String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(response.getStatusCode()).isEqualTo(CREATED);
        return location;
    }

    private Question selectQuestion(String location) {
        return template().getForObject(location, Question.class);
    }

    @Test
    public void create_no_login() {
        //given
        Question newQuestion = QuestionTest.newTestQuestion();

        //when
        ResponseEntity<Void> response = template()
                .postForEntity("/api/questions", newQuestion, Void.class);

        //then
        softly.assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void update() {
        //given
        User loginUser = defaultUser();
        String location = createQuestion(loginUser);
        Question updateQuestion = updatedQuestion;

        //when
        ResponseEntity<Question> responseEntity = basicAuthTemplate(loginUser)
                .exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(OK);
        softly.assertThat(updateQuestion.equalsTitleAndContents(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_no_login() {
        User loginUser = defaultUser();
        String location = createQuestion(loginUser);
        Question updateQuestion = updatedQuestion;

        ResponseEntity<String> responseEntity = template()
                .exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }

    @Test
    public void delete() {
        User loginUser = defaultUser();
        String location = createQuestion(loginUser);

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .exchange(location, HttpMethod.DELETE, null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void delete_no_login() {
        User loginUser = defaultUser();
        String location = createQuestion(loginUser);

        ResponseEntity<String> response = template()
                .exchange(location, HttpMethod.DELETE, null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }
}
