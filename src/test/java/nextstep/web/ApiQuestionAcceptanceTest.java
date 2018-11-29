package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionTest;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.newUser;
import static org.assertj.core.api.Assertions.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);
    private final String API_QUESTIONS = "/api/questions";


    @Test
    public void create() throws Exception {
        Question question = QuestionTest.newQuestion();
        String location = createLoginResourceLocation(JAVAJIGI, API_QUESTIONS, question);
        Question result = getResource(location, Question.class, JAVAJIGI);
        softly.assertThat(result).isNotNull();
    }

    @Test
    public void create_손님() throws Exception {
        Question question = QuestionTest.newQuestion();

        ResponseEntity<String> response = basicAuthTemplate(User.GUEST_USER).postForEntity(API_QUESTIONS, question, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }

    @Test
    public void show() throws Exception {
        Question question = Question.ofUser("test1", "contents1", defaultUser());
        String location = createLoginResourceLocation(defaultUser(), API_QUESTIONS, question);
        Question result = getResource(location, Question.class, defaultUser());
        softly.assertThat(result).isNotNull();
    }

    @Test
    public void update() throws Exception {
        Question question = QuestionTest.newQuestion();
        String location = createLoginResourceLocation(defaultUser(), API_QUESTIONS, question);

        Question updateQuestion = QuestionTest.newQuestion("수정제목", "수정내용입니다.");

        ResponseEntity<Question> responseEntity = putLoginResponseEntity(location, Question.class, defaultUser(), updateQuestion);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateQuestion.getTitle()).isEqualTo(responseEntity.getBody().getTitle());
    }

    @Test
    public void update_손님() throws Exception {
        Question question = QuestionTest.newQuestion();
        String location = createLoginResourceLocation(defaultUser(), API_QUESTIONS, question);

        Question updateQuestion = QuestionTest.newQuestion("수정제목", "수정내용입니다.");

        ResponseEntity<Question> responseEntity = putLoginResponseEntity(location, Question.class, User.GUEST_USER, updateQuestion);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update_타인() throws Exception {
        User newUser = newUser("taintest");
        Question question = QuestionTest.newQuestion();
        String location = createLoginResourceLocation(defaultUser(), API_QUESTIONS, question);

        Question updateQuestion = QuestionTest.newQuestion("수정제목", "수정내용입니다.");

        ResponseEntity<Question> responseEntity = putLoginResponseEntity(location, Question.class, newUser, updateQuestion);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() throws Exception {
        Question question = QuestionTest.newQuestion();
        String location = createLoginResourceLocation(defaultUser(), API_QUESTIONS, question);
        ResponseEntity<Question> responseEntity = deleteLoginResponseEntity(location, Question.class, defaultUser(), question);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getBody().isDeleted()).isTrue();
    }

    @Test
    public void delete_손님() throws Exception {
        User newUser = newUser("taintest");
        Question question = QuestionTest.newQuestion();
        String location = createLoginResourceLocation(defaultUser(), API_QUESTIONS, question);
        ResponseEntity<Question> responseEntity = deleteLoginResponseEntity(location, Question.class, newUser, question);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_타인() throws Exception {
        User newUser = newUser("taintest");
        Question question = QuestionTest.newQuestion();
        String location = createLoginResourceLocation(defaultUser(), API_QUESTIONS, question);
        ResponseEntity<Question> responseEntity = deleteLoginResponseEntity(location, Question.class, newUser, question);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

}
