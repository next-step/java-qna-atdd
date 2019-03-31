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
    User loginUser = defaultUser();
    Question newQuestion = newQuestion("질문 제목", "질문 내용");

    // When
    ResponseEntity<Void> response = basicAuthTemplate(loginUser).postForEntity("/api/questions", newQuestion, Void.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    String location = response.getHeaders().getLocation().getPath();
    Question question = template().getForObject(location, Question.class);
    softly.assertThat(question).isNotNull();
  }
}
