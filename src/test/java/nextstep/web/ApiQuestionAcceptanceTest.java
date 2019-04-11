package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static nextstep.domain.QuestionTest.newQuestion;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);
    static final String TITLE = "제목 내용";
    static final String CONTENTS = "본문 내용";
    static final String API_QUESTION_LOCATION = "/api/questions";

    @Test
    public void create() throws Exception {
        User loginUser = defaultUser();

        Question question = newQuestion(TITLE, CONTENTS, loginUser);

        ResponseEntity<Void> response = basicAuthTemplate().postForEntity(API_QUESTION_LOCATION, question, Void.class);
        Question dbQuestion = basicAuthTemplate().getForObject(API_QUESTION_LOCATION, Question.class);

        softly.assertThat(dbQuestion).isNotNull();
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }


    @Test
    public void update() throws Exception {
        User loginUser = defaultUser();

        String location = createLocation(loginUser);

        Question original = getResource(location, Question.class, loginUser);
        Question updateQuestion = new Question(original.getId(), original.getTitle(), original.getContents(), loginUser);

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateQuestion.equals(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_no_login() throws Exception {
        User loginUser = defaultUser();

        String location = createLocation(loginUser);

        Question original = getResource(location, Question.class, loginUser);
        Question updateQuestion = new Question(original.getId(), original.getTitle(), original.getContents(), loginUser);

        ResponseEntity<Question> responseEntity =
                template().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }

    @Test
    public void delete() throws Exception {
        User loginUser = defaultUser();

        String location = createLocation(loginUser);
        Question original = getResource(location, Question.class, loginUser);

        ResponseEntity<Void> responseEntity
                = basicAuthTemplate().exchange(location, HttpMethod.DELETE, createHttpEntity(original), Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private String createLocation(User loginUser) {
        Question question = newQuestion(TITLE, CONTENTS, loginUser);
        return createResource(API_QUESTION_LOCATION, question);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
