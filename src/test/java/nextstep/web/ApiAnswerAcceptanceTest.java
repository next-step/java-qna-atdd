package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);
    private static final String QUESTION_API_PATH = "/api/questions";
    private static final String ANSWER_API_PATH = "/answers";
    private Question originQuestion;
    private String newAnswer;

    @Before
    public void setUp() throws Exception {
        String location = createResource(QUESTION_API_PATH, new Question("제목이다", "내용이다"));
        originQuestion = getResource(defaultUser(), location, Question.class);

        newAnswer = "답변입니당";
    }

    private String getAnswerCreateApiPath(Question question) {
        return "/api/" + originQuestion.generateUrl() + ANSWER_API_PATH;
    }

    @Test
    public void create() throws Exception {
        String location = createResource(getAnswerCreateApiPath(originQuestion), newAnswer);
        Answer dbAnswer = getResource(defaultUser(), location, Answer.class);
        softly.assertThat(dbAnswer).isNotNull();
    }

    @Test
    public void create_no_login() throws Exception {
        ResponseEntity<Void> response = template().postForEntity(getAnswerCreateApiPath(originQuestion), newAnswer, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() {
        String location = createResource(getAnswerCreateApiPath(originQuestion), newAnswer);

        ResponseEntity<Void> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.DELETE, createHttpEntity(null), Void.class);
        Answer deletedAnswer = getResource(User.GUEST_USER, location, Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getHeaders().getLocation().getPath()).isEqualTo("/api/" + originQuestion.generateUrl());
        softly.assertThat(deletedAnswer.isDeleted()).isTrue();
    }


    @Test
    public void delete_no_login() {
        String location = createResource(getAnswerCreateApiPath(originQuestion), newAnswer);

        ResponseEntity<Void> responseEntity
                = template().exchange(location, HttpMethod.DELETE, createHttpEntity(null), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_not_owner() {
        String location = createResource(getAnswerCreateApiPath(originQuestion), newAnswer);

        ResponseEntity<Void> responseEntity =
                basicAuthTemplate(findByUserId("sanjigi")).exchange(location, HttpMethod.DELETE, createHttpEntity(null), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    }
}
