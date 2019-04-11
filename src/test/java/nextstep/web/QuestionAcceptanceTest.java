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

import static nextstep.domain.QuestionTest.ANOTHER_QUESTION_ID;
import static nextstep.domain.QuestionTest.SELF_QUESTION_ID;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void show() throws Exception {
        Question question = selfQuestion();

        ResponseEntity<String> response = template().getForEntity(
                String.format("/questions/%d", question.getId()), String.class);

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
        User loginUser = selfUser();

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
        User loginUser = selfUser();

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
        ResponseEntity<String> response = template().getForEntity(
                String.format("/questions/%d/form", selfQuestion().getId()), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        log.debug("body: {}", response.getBody());
    }

    @Test
    public void updateForm_login() {
        Question question = selfQuestion();

        ResponseEntity<String> response = basicAuthTemplate().getForEntity(
                String.format("/questions/%d/form", question.getId()), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains(question.getTitle());

        log.debug("body: {}", response.getBody());
    }

    @Test
    public void update_no_login() {
        ResponseEntity<String> response = update(template(), selfQuestion());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        log.debug("body : {}", response.getBody());
    }

    @Test
    public void update_login_request_self() {
        ResponseEntity<String> response = update(basicAuthTemplate(), selfQuestion());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
        softly.assertThat(selfQuestion().getTitle()).isEqualTo("updateTitle");
        softly.assertThat(selfQuestion().getContents()).isEqualTo("updateContents");

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
        ResponseEntity<String> response = delete(template(), selfQuestion());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        log.debug("body : {}", response.getBody());
    }

    @Test
    public void delete_login_another() {
        ResponseEntity<String> response = delete(basicAuthTemplate(), anotherQuestion());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        log.debug("body : {}", response.getBody());
    }

    @Test
    public void delete_login_self_contains_another_answers() throws Exception {
        ResponseEntity<String> response = delete(basicAuthTemplate(), selfQuestion());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        log.debug("body : {}", response.getBody());
    }

    @Test
    public void delete_login_self_contains_only_self_answers() {
        ResponseEntity<String> response = delete(basicAuthTemplate(anotherUser()), anotherQuestion());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");

        log.debug("body : {}", response.getBody());
    }


    private ResponseEntity<String> delete(TestRestTemplate template, Question question) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();

        return template.postForEntity(String.format("/questions/%d", question.getId()), request, String.class);
    }

    public Question selfQuestion() {
        return questionRepository.findById(SELF_QUESTION_ID).get();
    }

    public Question anotherQuestion() {
        return questionRepository.findById(ANOTHER_QUESTION_ID).get();
    }
}
