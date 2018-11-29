package nextstep.web;

import nextstep.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.newUser;
import static org.assertj.core.api.Assertions.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);
    private final String PREFIX_API = "/api";
    private final String SUFFIX_ANSWERS = "/answers";
    private Question question;

    @Before
    public void setUp() throws Exception {
        question = defaultQuestion();
    }

    @Test
    public void create() throws Exception {
        Answer answer = AnswerTest.newAnswer();
        String location = createLoginResourceLocation(JAVAJIGI, PREFIX_API + question.generateUrl() + SUFFIX_ANSWERS, answer);
        Answer result = getResource(location, Answer.class, JAVAJIGI);
        softly.assertThat(result).isNotNull();
    }

    @Test
    public void create_손님() throws Exception {
        Answer answer = AnswerTest.newAnswer();
        ResponseEntity<String> response = basicAuthTemplate(User.GUEST_USER).postForEntity(PREFIX_API + question.generateUrl() + SUFFIX_ANSWERS, answer, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void show() throws Exception {
        Answer answer = AnswerTest.newAnswer();
        String location = createLoginResourceLocation(defaultUser(), PREFIX_API + question.generateUrl() + SUFFIX_ANSWERS, answer);
        Answer result = getResource(location, Answer.class, defaultUser());
        softly.assertThat(result).isNotNull();
    }

    @Test
    public void update() throws Exception {
        Answer answer = AnswerTest.newAnswer("수정전입니다.");
        String location = createLoginResourceLocation(defaultUser(), PREFIX_API + question.generateUrl() + SUFFIX_ANSWERS, answer);

        Answer updateAnswer = Answer.ofQuestion(answer.getId(), defaultUser(), question, "수정된 내용입니다.");
        ResponseEntity<Answer> responseEntity = putLoginResponseEntity(location, Answer.class, defaultUser(), updateAnswer);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateAnswer.getContents()).isEqualTo(responseEntity.getBody().getContents());
    }

    @Test
    public void update_손님() throws Exception {
        Answer answer = AnswerTest.newAnswer("수정전입니다.");
        String location = createLoginResourceLocation(defaultUser(), PREFIX_API + question.generateUrl() + SUFFIX_ANSWERS, answer);

        Answer updateQuestion = AnswerTest.newAnswer("수정된 내용입니다.");
        ResponseEntity<Answer> responseEntity = putLoginResponseEntity(location, Answer.class, User.GUEST_USER, updateQuestion);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update_타인() throws Exception {
        User newUser = newUser("taintest");
        Answer answer = AnswerTest.newAnswer("수정전입니다.");
        String location = createLoginResourceLocation(defaultUser(), PREFIX_API + question.generateUrl() + SUFFIX_ANSWERS, answer);

        Answer updateQuestion = AnswerTest.newAnswer("수정된 내용입니다.");
        ResponseEntity<Answer> responseEntity = putLoginResponseEntity(location, Answer.class, newUser, updateQuestion);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() throws Exception {
        Answer answer = AnswerTest.newAnswer();
        String location = createLoginResourceLocation(defaultUser(), PREFIX_API + question.generateUrl() + SUFFIX_ANSWERS, answer);
        ResponseEntity<Answer> responseEntity = deleteLoginResponseEntity(location, Answer.class, defaultUser(), answer);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_손님() throws Exception {
        User newUser = newUser("taintest");
        Answer answer = AnswerTest.newAnswer();
        String location = createLoginResourceLocation(defaultUser(), PREFIX_API + question.generateUrl() + SUFFIX_ANSWERS, answer);
        ResponseEntity<Answer> responseEntity = deleteLoginResponseEntity(location, Answer.class, newUser, answer);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_타인() throws Exception {
        User newUser = newUser("taintest");
        Answer answer = AnswerTest.newAnswer();
        String location = createLoginResourceLocation(defaultUser(), PREFIX_API + question.generateUrl() + SUFFIX_ANSWERS, answer);
        ResponseEntity<Answer> responseEntity = deleteLoginResponseEntity(location, Answer.class, newUser, answer);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

}
