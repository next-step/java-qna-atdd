package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

public class QnaAcceptanceTest extends AcceptanceTest {

  private static final Logger log = LoggerFactory.getLogger(QnaAcceptanceTest.class);

  @Autowired
  private QuestionRepository questionRepository;

  @Test
  public void createForm() throws Exception {

    // When
    ResponseEntity<String> response = template().getForEntity("/qnas/form", String.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    log.debug("body : {}", response.getBody());
  }

  @Test
  public void create() throws Exception {

    // Given
    User loginUser = defaultUser();
    HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
        .addParameter("title", "질문 제목")
        .addParameter("contents", "질문 내용")
        .build();

    // When
    ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/qnas", request, String.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
  }

  @Test
  public void list() throws Exception {

    // When
    ResponseEntity<String> response = template().getForEntity("/", String.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    log.debug("body : {}", response.getBody());

    Question question = questionRepository.findById(1L)
        .orElseThrow(NullPointerException::new);

    softly.assertThat(response.getBody()).contains(question.generateUrl());
    softly.assertThat(response.getBody()).contains(question.getTitle());
  }
}
