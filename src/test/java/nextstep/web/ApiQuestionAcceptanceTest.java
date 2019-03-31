package nextstep.web;

import static nextstep.domain.QuestionTest.editQuestion;
import static nextstep.domain.QuestionTest.newQuestion;

import java.util.List;
import nextstep.domain.Question;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
  public void list() throws Exception {

    // When
    ResponseEntity<List> response = template().getForEntity("/api/questions", List.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<Question> questions = response.getBody();
    softly.assertThat(questions.size()).isNotEqualTo(0);
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

  @Test
  public void update() throws Exception {

    // Given
    long questionId = 1L;
    String title = "질문 제목 수정";
    String content = "질문 내용 수정";
    Question editQuestion = editQuestion(questionId, title, content);

    // When
    ResponseEntity<Question> response = basicAuthTemplate(defaultUser()).exchange(String.format("/api/questions/%d", editQuestion.getId()), HttpMethod.PUT, createHttpEntity(editQuestion), Question.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Question question = response.getBody();
    softly.assertThat(question.getTitle()).isEqualTo(title);
    softly.assertThat(question.getContents()).isEqualTo(content);
  }

  @Test
  public void update_notFound() throws Exception {

    // Given
    long questionId = 100L;
    String title = "질문 제목 수정";
    String content = "질문 내용 수정";
    Question editQuestion = editQuestion(questionId, title, content);

    // When
    ResponseEntity<Question> response = basicAuthTemplate(defaultUser()).exchange(String.format("/api/questions/%d", editQuestion.getId()), HttpMethod.PUT, createHttpEntity(editQuestion), Question.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  public void update_notOwner() throws Exception {

    // Given
    long questionId = 1L;
    String title = "질문 제목 수정";
    String content = "질문 내용 수정";
    Question editQuestion = editQuestion(questionId, title, content);

    // When
    ResponseEntity<Question> response = basicAuthTemplate(findByUserId("sanjigi")).exchange(String.format("/api/questions/%d", editQuestion.getId()), HttpMethod.PUT, createHttpEntity(editQuestion), Question.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  public void delete() throws Exception {

    // Given
    long questionId = 1L;

    // When
    ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).exchange(String.format("/api/questions/%d", questionId), HttpMethod.DELETE, createHttpEntity(null), Void.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void delete_notFound() throws Exception {

    // Given
    long questionId = 100L;

    // When
    ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).exchange(String.format("/api/questions/%d", questionId), HttpMethod.DELETE, createHttpEntity(null), Void.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  public void delete_notOwner() throws Exception {

    // Given
    long questionId = 1L;

    // When
    ResponseEntity<Void> response = basicAuthTemplate(findByUserId("sanjigi")).exchange(String.format("/api/questions/%d", questionId), HttpMethod.DELETE, createHttpEntity(null), Void.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  private HttpEntity createHttpEntity(Object body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity(body, headers);
  }
}
