package nextstep.web;

import nextstep.domain.Question;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);
    private static final String QUESTION_API_PATH = "/api/questions";
    private Question newQuestion;

    @Before
    public void setUp() throws Exception {
        newQuestion = new Question("새로운제목", "새로운내용");
    }

    @Test
    public void create() throws Exception {
        String location = createResource(QUESTION_API_PATH, newQuestion);
        Question dbQuestion = getResource(defaultUser(), location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();
    }

    @Test
    public void create_no_login() throws Exception {
        ResponseEntity<Void> response = template().postForEntity(QUESTION_API_PATH, newQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void show_not_exist() throws Exception {
        ResponseEntity<Void> response = basicAuthTemplate().getForEntity(QUESTION_API_PATH + 100L, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void update() throws Exception {
        String location = createResource(QUESTION_API_PATH, newQuestion);
        Question dbQuestion = getResource(defaultUser(), location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();

        Question updateQuestion = new Question("수정제목", "수정내용");
        ResponseEntity<Question> responseEntity =
                basicAuthTemplate()
                        .exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateQuestion.equalsTitleAndContent(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_not_owner() throws Exception {
        String location = createResource(QUESTION_API_PATH, newQuestion);

        Question updateQuestion = new Question("수정제목", "수정내용");
        ResponseEntity<Question> responseEntity =
                basicAuthTemplate(findByUserId("sanjigi"))
                        .exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_no_login() throws Exception {
        String location = createResource(QUESTION_API_PATH, newQuestion);

        Question updateQuestion = new Question("수정제목", "수정내용");
        ResponseEntity<Question> responseEntity =
                template().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() throws Exception {
        String location = createResource(QUESTION_API_PATH, newQuestion);

        ResponseEntity<Void> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.DELETE, createHttpEntity(null), Void.class);
        Question savedQuestion = getResource(defaultUser(), location, Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getHeaders().getLocation().getPath()).isEqualTo(QUESTION_API_PATH);
        softly.assertThat(savedQuestion.isDeleted()).isTrue();
    }

    @Test
    public void delete_not_owner() throws Exception {
        String location = createResource(QUESTION_API_PATH, newQuestion);

        ResponseEntity<Void> responseEntity =
                basicAuthTemplate(findByUserId("sanjigi")).exchange(location, HttpMethod.DELETE, createHttpEntity(null), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_no_login() throws Exception {
        String location = createResource(QUESTION_API_PATH, newQuestion);

        ResponseEntity<Void> responseEntity =
                template().exchange(location, HttpMethod.DELETE, createHttpEntity(null), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
