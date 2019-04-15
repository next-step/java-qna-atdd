package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionBody;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;
import support.test.HttpClientRequestUtils;

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
        createQuestion();
    }

    private String createQuestion() {
        ResponseEntity<Void> responseEntity = HttpClientRequestUtils.createResource(basicAuthTemplate(), QUESTION_API_PATH, newQuestion, Void.class);
        String location = responseEntity.getHeaders().getLocation().getPath();
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Question dbQuestion = HttpClientRequestUtils.getResource(basicAuthTemplate(), location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();
        return location;
    }

    @Test
    public void create_no_login() throws Exception {
        ResponseEntity<Void> response = HttpClientRequestUtils.createResource(template(), QUESTION_API_PATH, newQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void show_not_exist() throws Exception {
        ResponseEntity<Void> response = HttpClientRequestUtils.showResource(template(), QUESTION_API_PATH + "/" + 100L, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void update() throws Exception {
        String location = createQuestion();

        QuestionBody updateQuestion = new QuestionBody("수정제목", "수정내용");
        ResponseEntity<Question> responseEntity = HttpClientRequestUtils.updateResource(basicAuthTemplate(), location, updateQuestion, Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateQuestion.equalsQuestionBody(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_not_owner() throws Exception {
        String location = createQuestion();

        Question updateQuestion = new Question("수정제목", "수정내용");
        ResponseEntity<Question> responseEntity = HttpClientRequestUtils.updateResource(basicAuthTemplate(findByUserId("sanjigi")), location, updateQuestion, Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_no_login() throws Exception {
        String location = createQuestion();

        Question updateQuestion = new Question("수정제목", "수정내용");
        ResponseEntity<Question> responseEntity = HttpClientRequestUtils.updateResource(template(), location, updateQuestion, Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() throws Exception {
        String location = createQuestion();

        ResponseEntity<Void> responseEntity = HttpClientRequestUtils.deleteResource(basicAuthTemplate(), location, Void.class);
        Question savedQuestion = HttpClientRequestUtils.getResource(basicAuthTemplate(), location, Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getHeaders().getLocation().getPath()).isEqualTo(QUESTION_API_PATH);
        softly.assertThat(savedQuestion.isDeleted()).isTrue();
    }

    @Test
    public void delete_not_owner() throws Exception {
        String location = createQuestion();

        ResponseEntity<Void> responseEntity = HttpClientRequestUtils.deleteResource(basicAuthTemplate(findByUserId("sanjigi")), location, Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_no_login() throws Exception {
        String location = createQuestion();

        ResponseEntity<Void> responseEntity = HttpClientRequestUtils.deleteResource(template(), location, Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_has_other_answer() throws Exception {
        Question question = HttpClientRequestUtils.getResource(template(), QUESTION_API_PATH + "/" + 1L, Question.class);
        String location = "/api" + question.generateUrl();

        ResponseEntity<Void> responseEntity2 = HttpClientRequestUtils.deleteResource(basicAuthTemplate(), location, Void.class);
        softly.assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
