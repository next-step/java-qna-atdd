package nextstep.web;

import static nextstep.domain.QuestionTest.newQuestion;

import nextstep.domain.Question;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

  private static final Logger logger = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

  @Test
  public void create() throws Exception {

    // Given
    String title = "질문 제목";
    String content = "질문 내용";
    Question newQuestion = newQuestion(title, content);

    // When
    ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).postForEntity("/api/questions", newQuestion, Void.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    String location = response.getHeaders().getLocation().getPath();
    Question question = template().getForObject(location, Question.class);
    softly.assertThat(question.getTitle()).isEqualTo(title);
    softly.assertThat(question.getContents()).isEqualTo(content);
  }

  @Test
  public void show() throws Exception {

    // Given
    long questionId = 1L;

    // When
    ResponseEntity<Question> response = template().getForEntity(String.format("/api/questions/%d", questionId), Question.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Question question = response.getBody();
    softly.assertThat(question.getId()).isEqualTo(questionId);
  }

  @Test
  public void show_notFound() throws Exception {

    // Given
    long questionId = 100L;

    // When
    ResponseEntity<Question> response = template().getForEntity(String.format("/api/questions/%d", questionId), Question.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }
}
