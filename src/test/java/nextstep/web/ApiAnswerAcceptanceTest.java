package nextstep.web;

import nextstep.domain.*;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import support.test.AcceptanceTest;
import support.test.RestApiCallUtils;

import java.util.List;

import static nextstep.domain.AnswerTest.ANOTHER_ANSWER_ID;
import static nextstep.domain.AnswerTest.SELF_ANSWER_ID;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);
    public static final String BASE_URL = "/api/questions/%d/answers";

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    public void create_no_login() {
        // Given
        Question question = selfQuestion();
        String contents = "answer";

        // When
        ResponseEntity<Void> response = RestApiCallUtils.createResource(
                template(), getUrl(question), contents);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void create_login() {
        // Given
        User loginUser = selfUser();
        Question question = selfQuestion();
        String contents = "answer";

        // When
        ResponseEntity<Void> response = RestApiCallUtils.createResource(
                basicAuthTemplate(loginUser), getUrl(question), contents);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void show_one_after_create() {
        // Given
        ResponseEntity<Void> createResponse = RestApiCallUtils.createResource(
                basicAuthTemplate(selfUser()), getUrl(selfQuestion()), "answer");
        String location = createResponse.getHeaders().getLocation().getPath();

        // When
        Answer createdAnswer = RestApiCallUtils.getResource(
                template(), location, Answer.class).getBody();

        // Then
        softly.assertThat(createdAnswer).isNotNull();
    }

    @Test
    @Transactional
    public void show_all() throws Exception {
        // Given
        Question question = selfQuestion();

        // When
        ResponseEntity<List<Answer>> response = RestApiCallUtils.getListResource(
                template(), getUrl(question), Answer.class);

        // Then
        int size = question.sizeAnswers();
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).hasSize(size);
    }

    @Test
    public void show_one() {
        // Given
        Question question = selfQuestion();
        Answer answer = selfAnswer();

        // When
        ResponseEntity<Answer> response = RestApiCallUtils.getResource(
                template(), getUrl(question, answer), Answer.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).isNotNull();
        softly.assertThat(response.getBody().getId()).isEqualTo(answer.getId());
    }

    @Test
    public void update_no_login() {
        // Given
        Question question = selfQuestion();
        Answer answer = selfAnswer();
        String updateContents = "update answer";

        // When
        ResponseEntity<Answer> response = RestApiCallUtils.updateResource(
                template(), getUrl(question, answer), updateContents, Answer.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update_login_another() {
        // Given
        User loginUser = selfUser();
        Question question = selfQuestion();
        Answer answer = anotherAnswer();
        String updateContents = "update answer";

        // When
        ResponseEntity<Answer> response = RestApiCallUtils.updateResource(
                basicAuthTemplate(loginUser), getUrl(question, answer), updateContents, Answer.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_login_self() {
        // Given
        User loginUser = selfUser();
        Question question = selfQuestion();
        Answer answer = selfAnswer();
        String updateContents = "update answer";

        // When
        ResponseEntity<Answer> response = RestApiCallUtils.updateResource(
                basicAuthTemplate(loginUser), getUrl(question, answer), updateContents, Answer.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().getContents()).isEqualTo(updateContents);
    }

    @Test
    public void delete_no_login() {
        // Given
        Question question = selfQuestion();
        Answer answer = selfAnswer();

        ResponseEntity<Void> response = RestApiCallUtils.deleteResource(template(), getUrl(question, answer));
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_login_another() {
        // Given
        User loginUser = selfUser();
        Question question = selfQuestion();
        Answer answer = anotherAnswer();

        // When
        ResponseEntity<Void> response = RestApiCallUtils.deleteResource(
                basicAuthTemplate(loginUser), getUrl(question, answer));

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_login_self_but_different_owner_from_question() {
        // Given
        User loginUser = selfUser();
        Question question = selfQuestion();
        Answer answer = anotherAnswer();

        // When
        ResponseEntity<Void> response = RestApiCallUtils.deleteResource(
                basicAuthTemplate(loginUser), getUrl(question, answer));

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_login_self_and_same_owner_from_question() throws Exception {
        // Given
        User loginUser = selfUser();
        Question question = selfQuestion();
        Answer answer = selfAnswer();

        // When
        ResponseEntity<Void> response = RestApiCallUtils.deleteResource(
                basicAuthTemplate(loginUser), getUrl(question, answer));

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(selfAnswer().isDeleted()).isTrue();
    }

    private String getUrl(Question question, Answer defaultAnswer) {
        return String.format(BASE_URL, question.getId()) + "/" + defaultAnswer.getId();
    }

    private String getUrl(Question question) {
        return String.format(BASE_URL, question.getId());
    }

    private Question selfQuestion() {
        return ApiQuestionAcceptanceTest.selfQuestion(questionRepository);
    }

    private Answer selfAnswer() {
        return selfAnswer(answerRepository);
    }

    public static Answer selfAnswer(AnswerRepository answerRepository) {
        return answerRepository.findById(SELF_ANSWER_ID).get();
    }

    private Answer anotherAnswer() {
        return anotherAnswer(answerRepository);
    }

    public static Answer anotherAnswer(AnswerRepository answerRepository) {
        return answerRepository.findById(ANOTHER_ANSWER_ID).get();
    }
}
