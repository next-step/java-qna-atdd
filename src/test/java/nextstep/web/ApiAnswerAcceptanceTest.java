package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

  private static final Logger logger = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

  @Test
  public void create() throws Exception {

    // Given
    long questionId = 1L;
    String contents = "답변 내용";

    // When
    ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).postForEntity(String.format("/api/questions/%d/answers", questionId), contents, Void.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    String location = response.getHeaders().getLocation().getPath();
    Answer answer = template().getForObject(location, Answer.class);
    softly.assertThat(answer.getQuestion().getId()).isEqualTo(questionId);
    softly.assertThat(answer.getContents()).isEqualTo(contents);
  }

  @Test
  public void create_notFound() throws Exception {

    // Given
    long questionId = 100L;
    String contents = "답변 내용";

    // When
    ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).postForEntity(String.format("/api/questions/%d/answers", questionId), contents, Void.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  public void show() throws Exception {

    // Given
    long questionId = 1L;
    long answerId = 1L;

    // When
    ResponseEntity<Answer> response = template().getForEntity(String.format("/api/questions/%d/answers/%d", questionId, answerId), Answer.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Answer answer = response.getBody();
    softly.assertThat(answer).isNotNull();
    softly.assertThat(answer.getId()).isEqualTo(answerId);
    softly.assertThat(answer.getQuestion().getId()).isEqualTo(questionId);
  }

  @Test
  public void show_notFound_question() throws Exception {

    // Given
    long questionId = 100L;
    long answerId = 1L;

    // When
    ResponseEntity<Answer> response = template().getForEntity(String.format("/api/questions/%d/answers/%d", questionId, answerId), Answer.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  public void show_notFound_answer() throws Exception {

    // Given
    long questionId = 1L;
    long answerId = 100L;

    // When
    ResponseEntity<Answer> response = template().getForEntity(String.format("/api/questions/%d/answers/%d", questionId, answerId), Answer.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  public void show_notQuestion_answer() throws Exception {

    // Given
    long questionId = 2L;
    long answerId = 1L;

    // When
    ResponseEntity<Answer> response = template().getForEntity(String.format("/api/questions/%d/answers/%d", questionId, answerId), Answer.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  public void delete() throws Exception {

    // Given
    long questionId = 1L;
    long answerId = 1L;

    // When
    ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).exchange(String.format("/api/questions/%d/answers/%d", questionId, answerId), HttpMethod.DELETE, createHttpEntity(null), Void.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void delete_notFound_question() throws Exception {

    // Given
    long questionId = 100L;
    long answerId = 1L;

    // When
    ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).exchange(String.format("/api/questions/%d/answers/%d", questionId, answerId), HttpMethod.DELETE, createHttpEntity(null), Void.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  public void delete_notFound_answer() throws Exception {

    // Given
    long questionId = 1L;
    long answerId = 100L;

    // When
    ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).exchange(String.format("/api/questions/%d/answers/%d", questionId, answerId), HttpMethod.DELETE, createHttpEntity(null), Void.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  public void delete_notQuestion_answer() throws Exception {

    // Given
    long questionId = 2L;
    long answerId = 1L;

    // When
    ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).exchange(String.format("/api/questions/%d/answers/%d", questionId, answerId), HttpMethod.DELETE, createHttpEntity(null), Void.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  public void delete_notOwner() throws Exception {

    // Given
    long questionId = 1L;
    long answerId = 1L;

    // When
    ResponseEntity<Void> response = basicAuthTemplate(findByUserId("sanjigi")).exchange(String.format("/api/questions/%d/answers/%d", questionId, answerId), HttpMethod.DELETE, createHttpEntity(null), Void.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }
}
