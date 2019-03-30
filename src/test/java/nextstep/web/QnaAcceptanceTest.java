package nextstep.web;

import nextstep.domain.QuestionRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class QnaAcceptanceTest extends AcceptanceTest {

  private static final Logger log = LoggerFactory.getLogger(QnaAcceptanceTest.class);

  @Autowired
  private QuestionRepository questionRepository;

  @Test
  public void createForm() throws Exception {
    ResponseEntity<String> response = template().getForEntity("/qnas/form", String.class);
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    log.debug("body : {}", response.getBody());
  }
}
