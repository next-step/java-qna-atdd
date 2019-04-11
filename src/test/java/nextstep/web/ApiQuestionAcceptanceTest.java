package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.dto.QuestionDto;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;
import support.test.RestApiCallUtils;

import java.util.List;

import static nextstep.domain.QuestionTest.newQuestion;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);
    private static final String BASE_URL = "/api/questions";

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void create_no_login() {
        // Given
        Question question = newQuestion();

        // When
        ResponseEntity<Void> response = RestApiCallUtils.createResource(
                template(), BASE_URL, question);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void create_login() throws Exception {
        // Given
        User loginUser = selfUser();
        Question question = newQuestion();

        // When
        ResponseEntity<Void> response = RestApiCallUtils.createResource(
                basicAuthTemplate(loginUser), BASE_URL, question);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Given
        String location = response.getHeaders().getLocation().getPath();

        // When
        Question createdQuestion = RestApiCallUtils.getResource(
                basicAuthTemplate(loginUser), location, Question.class).getBody();

        // Then
        softly.assertThat(createdQuestion).isNotNull();
    }

    @Test
    public void show_all() throws Exception {
        // Given & When
        ResponseEntity<List<Question>> response = RestApiCallUtils.getListResource(
                template(), BASE_URL, Question.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().size())
                .isEqualTo(questionRepository.findAllByDeleted(false).size());
    }

    @Test
    public void show_one() throws Exception {
        // Given
        Question question = selfQuestion();

        // When
        ResponseEntity<Question> response = RestApiCallUtils.getResource(
                template(), getUrl(question), Question.class);
        Question body = response.getBody();

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(body).isNotNull();
        softly.assertThat(body.getId()).isEqualTo(selfQuestion().getId());
    }

    @Test
    public void update_no_login() throws Exception {
        // Given
        Question question = selfQuestion();

        // When
        ResponseEntity<Question> response = RestApiCallUtils.updateResource(
                template(), getUrl(question), new QuestionDto(), Question.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update_login_another() throws Exception {
        // Given
        User loginUser = selfUser();
        Question question = anotherQuestion();

        // When
        ResponseEntity<Question> response = RestApiCallUtils.updateResource(
                basicAuthTemplate(loginUser), getUrl(question), new QuestionDto(), Question.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_login_self() throws Exception {
        // Given
        User loginUser = selfUser();
        QuestionDto questionDto = new QuestionDto("updateQuestion title", "updateQuestion contents");
        Question question = selfQuestion();

        // When
        ResponseEntity<Question> response = RestApiCallUtils.updateResource(
                basicAuthTemplate(loginUser), getUrl(question), questionDto, Question.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().getTitle()).isEqualTo(questionDto.getTitle());
        softly.assertThat(response.getBody().getContents()).isEqualTo(questionDto.getContents());
    }

    @Test
    public void delete_no_login() {
        // Given
        Question question = selfQuestion();

        // When
        ResponseEntity<Void> response = RestApiCallUtils.deleteResource(
                template(), getUrl(question));

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_login_another() {
        // Given
        User loginUser = selfUser();
        Question question = anotherQuestion();

        // When
        ResponseEntity<Void> response =
                RestApiCallUtils.deleteResource(basicAuthTemplate(loginUser), getUrl(question));

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_login_self() {
        // Given
        User loginUser = selfUser();
        Question question = selfQuestion();

        // When
        ResponseEntity<Void> response = RestApiCallUtils.deleteResource(
                basicAuthTemplate(loginUser), getUrl(question));

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private String getUrl(Question question) {
        return String.format(BASE_URL + "/%d", question.getId());
    }
}
