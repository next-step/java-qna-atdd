package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.builder.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import static nextstep.domain.UserTest.JAVAJIGI;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    private static final String QUESTIONS = "/questions";
    private static final String ANSWERS = "/answers";

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void show() {
        Question question = questionRepository.findById(1L).get();

        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", question.getId()), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
//        softly.assertThat(response.getBody()).contains(question.getContents());
    }

    @Test
    public void createForm() {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() {
        String title = "질문 제목 입니다.";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", title)
                .addParameter("contents", "질문은 질문입니다.")
                .build();

        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);

        Question question = questionRepository.findFirstByOrderByIdDesc();
//        softly.assertThat(question.getTitle()).isEqualTo(title);

        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create_not_login() {
        String title = "질문 제목 입니다.";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", title)
                .addParameter("contents", "질문은 질문입니다.")
                .build();

        ResponseEntity<String> response = template().postForEntity("/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void updateForm_writer_login() {
        Question question = questionRepository.findById(1L).get();

        ResponseEntity<String> response = basicAuthTemplate()
                .getForEntity(String.format("/questions/%d/form", question.getId()), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
//        softly.assertThat(response.getBody()).contains(question.getQuestionBody());
    }

    @Test
    public void updateForm_login() {
        Question question = questionRepository.findById(1L).get();
        User user = findByUserId("sanjigi");

        ResponseEntity<String> response = basicAuthTemplate(user)
                .getForEntity(String.format("/questions/%d/form", question.getId()), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void updateForm_not_login() {
        Question question = questionRepository.findById(1L).get();

        ResponseEntity<String> response = template()
                .getForEntity(String.format("/questions/%d/form", question.getId()), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void update_writer_login() {
        Question question = questionRepository.findById(1L).get();
        ResponseEntity<String> response = update(basicAuthTemplate(), question.getId());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions/" + question.getId());
    }

    @Test
    public void update_login() {
        Question question = questionRepository.findById(1L).get();
        User user = findByUserId("sanjigi");

        ResponseEntity<String> response = update(basicAuthTemplate(user), question.getId());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void update_not_login() {
        Question question = questionRepository.findById(1L).get();

        ResponseEntity<String> response = update(template(), question.getId());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void delete_writer_login() {
        Question question = questionRepository.findById(3L).get();

        ResponseEntity<String> response = delete(basicAuthTemplate(), question.getId());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @Test
    public void delete_login() {
        Question question = questionRepository.findById(2L).get();

        ResponseEntity<String> response = delete(basicAuthTemplate(), question.getId());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        log.debug("body : {}", response.getBody());

    }

    @Test
    public void delete_not_login() {
        Question question = questionRepository.findById(2L).get();

        ResponseEntity<String> response = delete(template(), question.getId());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void delete_question_and_answer_writer_login() {
        Question question = questionRepository.findById(3L).get();

        ResponseEntity<String> response = delete(basicAuthTemplate(), question.getId());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @Test
    public void delete_question_and_answer_another_login() {
        Question question = questionRepository.findById(1L).get();

        ResponseEntity<String> response = delete(basicAuthTemplate(), question.getId());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<String> update(TestRestTemplate template, long id) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", "질문 수정제목 입니다.")
                .addParameter("contents", "질문은 수정질문입니다.")
                .build();

        return template.postForEntity(String.format("/questions/%d", id), request, String.class);
    }

    private ResponseEntity<String> delete(TestRestTemplate template, long id) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();

        return template.postForEntity(String.format("/questions/%d", id), request, String.class);
    }
}
