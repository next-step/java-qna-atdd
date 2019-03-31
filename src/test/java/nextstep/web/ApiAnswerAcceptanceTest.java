package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
}
