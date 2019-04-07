package nextstep.web;

import javax.persistence.EntityNotFoundException;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

public class QuestionAcceptanceTest extends AcceptanceTest {

  private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

  @Autowired
  private QuestionRepository questionRepository;

  @Test
  public void createForm() throws Exception {

    // When
    ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);

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
    ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/questions", request, String.class);

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
        .orElseThrow(EntityNotFoundException::new);

    softly.assertThat(response.getBody()).contains(question.generateUrl());
    softly.assertThat(response.getBody()).contains(question.getTitle());
  }

  @Test
  public void show() throws Exception {

    // Given
    Question question = questionRepository.findAll().get(0);

    // When
    ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", question.getId()), String.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    log.debug("body : {}", response.getBody());

    softly.assertThat(response.getBody()).contains(question.generateUrl());
    softly.assertThat(response.getBody()).contains(question.getTitle());
  }

  @Test
  public void show_notFound() throws Exception {

    // When
    ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", 100L), String.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  public void updateForm_no_login() throws Exception {

    // Given
    Question question = questionRepository.findAll().get(0);

    // When
    ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", question.getId()),
        String.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  public void updateForm_notFound() throws Exception {

    // Given
    User loginUser = defaultUser();

    // When
    ResponseEntity<String> response = basicAuthTemplate(loginUser).getForEntity(String.format("/questions/%d/form", 100L),
        String.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  public void updateForm_notOwner() throws Exception {

    // Given
    User loginUser = findByUserId("sanjigi");
    Question question = questionRepository.findById(1L)
        .orElseThrow(EntityNotFoundException::new);

    // When
    ResponseEntity<String> response = basicAuthTemplate(loginUser).getForEntity(String.format("/questions/%d/form", question.getId()),
        String.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  public void updateForm_isOwner() throws Exception {

    // Given
    User loginUser = defaultUser();
    Question question = questionRepository.findById(1L)
        .orElseThrow(EntityNotFoundException::new);

    // When
    ResponseEntity<String> response = basicAuthTemplate(loginUser)
        .getForEntity(String.format("/questions/%d/form", question.getId()), String.class);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    softly.assertThat(response.getBody()).contains(question.getTitle());
    softly.assertThat(response.getBody()).contains(question.getContents());
  }

  @Test
  public void update_no_login() throws Exception {

    // Given
    Question question = questionRepository.findById(1L)
        .orElseThrow(EntityNotFoundException::new);

    // When
    ResponseEntity<String> response = update(template(), question.getId());

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    log.debug("body : {}", response.getBody());
  }

  @Test
  public void update_notFound() throws Exception {

    // Given
    User loginUser = defaultUser();

    // When
    ResponseEntity<String> response = update(basicAuthTemplate(loginUser), 100L);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  public void update_notOwner() throws Exception {

    // Given
    User loginUser = findByUserId("sanjigi");
    Question question = questionRepository.findById(1L)
        .orElseThrow(EntityNotFoundException::new);

    // When
    ResponseEntity<String> response = update(basicAuthTemplate(loginUser), question.getId());

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  public void update() throws Exception {

    // Given
    Question question = questionRepository.findById(1L)
        .orElseThrow(EntityNotFoundException::new);

    // When
    ResponseEntity<String> response = update(basicAuthTemplate(), question.getId());

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");

    Question updateQuestion = questionRepository.findById(1L)
        .orElseThrow(EntityNotFoundException::new);
    softly.assertThat(updateQuestion.getTitle()).isEqualTo("질문 제목 수정");
    softly.assertThat(updateQuestion.getContents()).isEqualTo("질문 내용 수정");
  }

  private ResponseEntity<String> update(TestRestTemplate template, long id) throws Exception {

    HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
        .put()
        .addParameter("title", "질문 제목 수정")
        .addParameter("contents", "질문 내용 수정")
        .build();

    return template.postForEntity(String.format("/questions/%d", id), request, String.class);
  }

  @Test
  public void delete_no_login() throws Exception {

    // Given
    Question question = questionRepository.findById(1L)
        .orElseThrow(EntityNotFoundException::new);

    // When
    ResponseEntity<String> response = delete(template(), question.getId());

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  public void delete_notFound() throws Exception {

    // Given
    User loginUser = defaultUser();

    // When
    ResponseEntity<String> response = delete(basicAuthTemplate(loginUser), 100L);

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  public void delete_notOwner() throws Exception {

    // Given
    User loginUser = findByUserId("sanjigi");
    Question question = questionRepository.findById(1L)
        .orElseThrow(EntityNotFoundException::new);

    // When
    ResponseEntity<String> response = delete(basicAuthTemplate(loginUser), question.getId());

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }


  @Test
  public void delete() throws Exception {

    // Given
    User loginUser = findByUserId("sanjigi");
    Question question = questionRepository.findById(2L)
        .orElseThrow(EntityNotFoundException::new);

    //When
    ResponseEntity<String> response = delete(basicAuthTemplate(loginUser), question.getId());

    // Then
    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    softly.assertThat(questionRepository.findById(2L).orElseThrow(EntityNotFoundException::new).isDeleted()).isTrue();
  }

  private ResponseEntity<String> delete(TestRestTemplate testRestTemplate, long id) {

    HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
        .delete()
        .build();

    return testRestTemplate
        .postForEntity(String.format("/questions/%d", id), request, String.class);
  }
}
