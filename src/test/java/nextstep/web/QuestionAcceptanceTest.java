package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.*;
import support.test.AcceptanceTest;

import java.net.URI;

public class QuestionAcceptanceTest extends AcceptanceTest {

    private static final String CONTEXT = "/questions";

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void home() {
        final String url = "/";

        final ResponseEntity<String> response = get(url, template());

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.OK);
    }

    @Test
    public void form() {
        final String url = CONTEXT + "/form";

        final ResponseEntity<String> response = basicAuthTemplate().getForEntity(url, String.class);

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.OK);
    }

    @Test
    public void form_no_login() {
        final String url = CONTEXT + "/form";

        final ResponseEntity<String> response = get(url, template());

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void create() {
        final String url = CONTEXT;
        final long questionCount = questionRepository.count();

        final ResponseEntity<String> response = post(url, basicAuthTemplate());

        final URI location = response.getHeaders().getLocation();
        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.FOUND);
        softly.assertThat(location.getPath()).isEqualTo(CONTEXT + "/" + (questionCount + 1));
    }

    @Test
    public void create_no_login() {
        final String url = CONTEXT;

        final ResponseEntity<String> response = post(url, template());

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void show() {
        final Long questionId = 1L;
        final String url = CONTEXT + "/{questionId}";
        final ResponseEntity<String> response = get(url, template(), questionId);

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.OK);
    }

    @Test
    public void show_not_exist() {
        final Long questionId = Long.MAX_VALUE;
        final String url = CONTEXT + "/{questionId}";
        final ResponseEntity<String> response = get(url, template(), questionId);

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateForm() {
        final Long questionId = 1L;
        final String url = CONTEXT + "/{questionId}/form";

        final ResponseEntity<String> response = get(url, basicAuthTemplate(), questionId);

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.OK);
    }

    @Test
    public void updateForm_no_login() {
        final Long questionId = 1L;
        final String url = CONTEXT + "/{questionId}/form";

        final ResponseEntity<String> response = get(url, template(), questionId);

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void updateForm_not_exist() {
        final Long questionId = Long.MAX_VALUE;
        final String url = CONTEXT + "/{questionId}/form";

        final ResponseEntity<String> response = get(url, basicAuthTemplate(), questionId);

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void update() {
        final Long questionId = 1L;
        final String url = CONTEXT + "/{questionId}";

        final ResponseEntity<String> response = put(url, basicAuthTemplate(), questionId);
        final URI location = response.getHeaders().getLocation();

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.FOUND);
        softly.assertThat(location.getPath()).isEqualTo(CONTEXT + "/" + questionId);
    }

    @Test
    public void update_no_login() {
        final Long questionId = 1L;
        final String url = CONTEXT + "/{questionId}";

        final ResponseEntity<String> response = put(url, template(), questionId);

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update_not_exist() {
        final Long questionId = Long.MAX_VALUE;
        final String url = CONTEXT + "/{questionId}";

        final ResponseEntity<String> response = put(url, basicAuthTemplate(), questionId);

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.BAD_REQUEST);
    }

   @Test
   public void update_different_writer() {
       final String anotherUserId = "sanjigi";
       final Long questionId = 1L;
       final String url = CONTEXT + "/{questionId}";
       final TestRestTemplate basicAuthTemplate = basicAuthTemplate(findByUserId(anotherUserId));

       final ResponseEntity<String> response = put(url, basicAuthTemplate, questionId);

       softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.FORBIDDEN);
   }

   @Test
   public void delete() {
       final String url = CONTEXT + "/{questionId}";
       final Question question = saveQuestion();

       final ResponseEntity<String> response = delete(url, basicAuthTemplate(), question.getId());
       final URI location = response.getHeaders().getLocation();

       softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.FOUND);
       softly.assertThat(location.getPath()).isEqualTo("/");
   }

    @Test
    public void delete_no_login() {
        final String url = CONTEXT + "/{questionId}";
        final Question question = saveQuestion();

        final ResponseEntity<String> response = delete(url, template(), question.getId());

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_different_writer() {
        final String anotherUserId = "sanjigi";
        final String url = CONTEXT + "/{questionId}";
        final TestRestTemplate basicAuthTemplate = basicAuthTemplate(findByUserId(anotherUserId));
        final Question question = saveQuestion();

        final ResponseEntity<String> response = delete(url, basicAuthTemplate, question.getId());

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.FORBIDDEN);
    }

    private Question saveQuestion() {
        final Question newQuestion = new Question("title", "contents");
        newQuestion.writeBy(findByUserId("javajigi"));
        return questionRepository.save(newQuestion);
    }

    private ResponseEntity<String> get(String url, TestRestTemplate testRestTemplate, Object... uriVariables) {
        return testRestTemplate.getForEntity(url, String.class, uriVariables);
    }

    private ResponseEntity<String> post(String url, TestRestTemplate testRestTemplate) {
        final MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("title", "title!!");
        params.add("contents", "contents!!");

        return testRestTemplate.postForEntity(url, params, String.class);
    }

    private ResponseEntity<String> put(String url, TestRestTemplate testRestTemplate, Object... uriVariables) {
        final MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("title", "title!!");
        params.add("contents", "contents!!");

        final URI requestUri = UriComponentsBuilder.fromPath(url).buildAndExpand(uriVariables).toUri();
        final RequestEntity<MultiValueMap<String, Object>> request = RequestEntity.put(requestUri).body(params);

        return testRestTemplate.exchange(request, String.class);
    }

    private ResponseEntity<String> delete(String url, TestRestTemplate testRestTemplate, Object... uriVariables) {
        final URI requestUri = UriComponentsBuilder.fromPath(url).buildAndExpand(uriVariables).toUri();
        final RequestEntity<Void> requestEntity = RequestEntity.delete(requestUri).build();

        return testRestTemplate.exchange(requestEntity, String.class);
    }
}
