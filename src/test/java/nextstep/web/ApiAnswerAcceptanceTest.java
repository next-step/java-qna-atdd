package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;
import support.test.HttpClientRequestUtils;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);
    private static final String QUESTION_API_PATH = "/api/questions";
    private static final String ANSWER_API_PATH = "/answers";
    private Question originQuestion;
    private String newAnswer;

    @Before
    public void setUp() throws Exception {
        ResponseEntity<Void> responseEntity = HttpClientRequestUtils.createResource(basicAuthTemplate(), QUESTION_API_PATH, new Question("제목이다", "내용이다"), Void.class);
        String location = responseEntity.getHeaders().getLocation().getPath();
        originQuestion = HttpClientRequestUtils.getResource(basicAuthTemplate(), location, Question.class);

        newAnswer = "답변입니당";
    }

    private String getAnswerCreateApiPath(Question question) {
        return "/api/" + originQuestion.generateUrl() + ANSWER_API_PATH;
    }

    @Test
    public void create() throws Exception {
        createAnswer();
    }

    private String createAnswer() {
        ResponseEntity<Void> responseEntity = HttpClientRequestUtils.createResource(basicAuthTemplate(), getAnswerCreateApiPath(originQuestion), newAnswer, Void.class);
        String location = responseEntity.getHeaders().getLocation().getPath();
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Answer dbAnswer = HttpClientRequestUtils.getResource(basicAuthTemplate(), location, Answer.class);
        softly.assertThat(dbAnswer).isNotNull();
        return location;
    }

    @Test
    public void create_no_login() throws Exception {
        ResponseEntity<Void> response = HttpClientRequestUtils.createResource(template(), getAnswerCreateApiPath(originQuestion), newAnswer, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() {
        String location = createAnswer();

        ResponseEntity<Void> responseEntity = HttpClientRequestUtils.deleteResource(basicAuthTemplate(), location, Void.class);
        Answer deletedAnswer = HttpClientRequestUtils.getResource(basicAuthTemplate(), location, Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getHeaders().getLocation().getPath()).isEqualTo("/api/" + originQuestion.generateUrl());
        softly.assertThat(deletedAnswer.isDeleted()).isTrue();
    }


    @Test
    public void delete_no_login() {
        String location = createAnswer();

        ResponseEntity<Void> responseEntity = HttpClientRequestUtils.deleteResource(template(), location, Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_not_owner() {
        String location = createAnswer();

        ResponseEntity<Void> responseEntity = HttpClientRequestUtils.deleteResource(basicAuthTemplate(findByUserId("sanjigi")), location, Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
