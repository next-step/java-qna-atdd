package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);
    private static final long DEFAULT_QUESTION_ID = 1;
    private static final long ANOTHER_QUESTION_ID = 2;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void show() throws Exception {
        Question question = defaultQuestion();

        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", question.getId()), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body: {}", response.getBody());
    }

    @Test
    public void createForm_no_login() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body: {}", response.getBody());
    }

    @Test
    public void createForm_login() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity("/questions/form", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body: {}", response.getBody());
    }

    @Test
    public void create_no_login() throws Exception {
        ResponseEntity<String> response = create(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body: {}", response.getBody());
    }

    @Test
    public void create_login() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = create(basicAuthTemplate(loginUser));
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
        log.debug("body: {}", response.getBody());
    }

    private ResponseEntity<String> create(TestRestTemplate template) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "testTitle")
                .addParameter("contents", "testContents")
                .build();

        return template.postForEntity("/questions/", request, String.class);
    }

    @Test
    public void updateForm_no_login() {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", defaultQuestion().getId()), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body: {}", response.getBody());
    }

    @Test
    public void updateForm_login() {
        Question question = defaultQuestion();
        ResponseEntity<String> response = basicAuthTemplate().getForEntity(String.format("/questions/%d/form", question.getId()), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains(question.getTitle());
        log.debug("body: {}", response.getBody());
    }

    @Test
    public void update_no_login() {
        ResponseEntity<String> response = update(template(), defaultQuestion());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void update_login_request_self() {
        ResponseEntity<String> response = update(basicAuthTemplate(), defaultQuestion());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
        softly.assertThat(defaultQuestion().getTitle()).isEqualTo("updateTitle");
        softly.assertThat(defaultQuestion().getContents()).isEqualTo("updateContents");

        log.debug("body : {}", response.getBody());
    }

    @Test
    public void update_login_request_another() {
        ResponseEntity<String> response = update(basicAuthTemplate(), anotherQuestion());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        log.debug("body : {}", response.getBody());
    }

    private ResponseEntity<String> update(TestRestTemplate template, Question question) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", "updateTitle")
                .addParameter("contents", "updateContents")
                .build();

        return template.postForEntity(String.format("/questions/%d", question.getId()), request, String.class);
    }

    @Test
    public void delete_no_login() {
        ResponseEntity<String> response = delete(template(), defaultQuestion());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void delete_login_self() {
        ResponseEntity<String> response = delete(basicAuthTemplate(), defaultQuestion());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
        softly.assertThat(defaultQuestion().isDeleted()).isTrue();

        log.debug("body : {}", response.getBody());
    }

    @Test
    public void delete_login_another() {
        ResponseEntity<String> response = delete(basicAuthTemplate(), anotherQuestion());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        log.debug("body : {}", response.getBody());
    }

    private ResponseEntity<String> delete(TestRestTemplate template, Question question) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();

        return template.postForEntity(String.format("/questions/%d", question.getId()), request, String.class);
    }

    private Question defaultQuestion() {
        return questionRepository.findById(DEFAULT_QUESTION_ID).get();
    }

    private Question anotherQuestion() {
        return questionRepository.findById(ANOTHER_QUESTION_ID).get();
    }
}
