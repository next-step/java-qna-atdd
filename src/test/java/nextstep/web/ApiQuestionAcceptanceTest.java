package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionTest;
import nextstep.domain.User;
import nextstep.domain.UserTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    public static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Test
    public void create() {
        User loginUser = defaultUser();
        Question question = QuestionTest.newQuestion(loginUser);
        Question newQuestion = getResource(createResource("/api/questions", question, loginUser), Question.class, loginUser);

        softly.assertThat(newQuestion).isNotNull();
        softly.assertThat(question.getTitle()).isEqualTo(question.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(question.getContents());
    }

    @Test
    public void update() throws Exception {
        User loginUser = defaultUser();
        Question newQuestion = QuestionTest.newQuestion(loginUser);
        String location = createResource("/api/questions", newQuestion, loginUser);
        Question original = getResource(location, Question.class, loginUser);
        Question updateQuestion = new Question(original.getId(), "wwwwwww", "eeeeeee", loginUser);
        ResponseEntity<Question> responseEntity = getExchange(location, updateQuestion, loginUser, Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateQuestion.equals(responseEntity.getBody())).isTrue();

    }

    @Test
    public void update_no_login() throws Exception {
        User loginUser = defaultUser();
        Question newQuestion = QuestionTest.newQuestion(loginUser);
        String location = createResource("/api/questions", newQuestion, loginUser);
        Question original = getResource(location, Question.class, loginUser);
        Question updateQuestion = new Question(original.getId(), "wwwwwww", "eeeeeee", loginUser);
        ResponseEntity<Question> responseEntity = getExchange(template(), location, updateQuestion, Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update_not_equal_writer() throws Exception {
        User loginUser = defaultUser();
        Question newQuestion = QuestionTest.newQuestion(loginUser);
        String location = createResource("/api/questions", newQuestion, loginUser);
        Question original = getResource(location, Question.class, loginUser);
        Question updateQuestion = new Question(original.getId(), "wwwwwww", "eeeeeee", loginUser);
        ResponseEntity<Question> responseEntity = getExchange(location, updateQuestion, UserTest.SANJIGI, Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
