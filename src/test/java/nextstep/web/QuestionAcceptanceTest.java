package nextstep.web;

import static nextstep.domain.UserTest.newUser;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import nextstep.domain.QuestionRepository;
import support.test.AcceptanceTest;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void createForm() {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create_login() {
        ResponseEntity<String> response = create(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findById(3l).isPresent()).isTrue();
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/home");
        log.info(response.getHeaders().getLocation().getPath());
    }

    @Test
    public void create_no_login() {
        ResponseEntity<String> response = create(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void detail() {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", 1), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains("국내에서 Ruby on Rails와 Play");
    }

    @Test
    public void updateForm_no_login() {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", 2), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void updateForm_login() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity(String.format("/questions/%d/form", 1), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?");
    }

    @Test
    public void update_no_login() {
        ResponseEntity<String> response = update(template(), 1);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update_not_found() {
        ResponseEntity<String> response = update(basicAuthTemplate(), 10);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    
    @Test
    public void update_no_owner() {
        ResponseEntity<String> response = update(basicAuthTemplate(newUser("testUser")), 1);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update_owner() {
        ResponseEntity<String> response = update(basicAuthTemplate(), 1);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/qna");
    }

    @Test
    public void delete_not_found() {
        ResponseEntity<String> response = delete(basicAuthTemplate(), 10);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    
    @Test
    public void delete_no_owner() {
        ResponseEntity<String> response = delete(basicAuthTemplate(), 2);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_owner() {
        ResponseEntity<String> response = delete(basicAuthTemplate(findByUserId("sanjigi")), 2);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }


    private ResponseEntity<String> create(TestRestTemplate template) {
        HtmlFormData htmlFormData = HtmlFormData.urlEncodedFormBuilder()
                .addParameter("title", "객체지향 생활 체조 원칙이란?")
                .addParameter("contents", "객체지향 생활 체조 원칙은 소트웍스 앤솔러지 책에서 다루고 있는 내용으로 객체지향 프로그래밍을 잘 하기 위한 9가지 원칙을 제시하고 있다")
                .build();

        return template
                .postForEntity("/questions", htmlFormData.newHttpEntity(), String.class);
    }

    private ResponseEntity<String> update(TestRestTemplate template, long id) {
        HtmlFormData htmlFormData = HtmlFormData.urlEncodedFormBuilder()
                .addParameter("title", "국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?")
                .addParameter("contents", "음.. 그건 ")
                .build();

        return template.postForEntity(String.format("/questions/%d", id), htmlFormData.newHttpEntity(), String.class);
    }
    
    private ResponseEntity<String> delete(TestRestTemplate template, long id) {
        HtmlFormData htmlFormData = HtmlFormData.urlEncodedFormBuilder()
                .addParameter("_method", "DELETE")
                .build();

        return template
                .postForEntity(String.format("/questions/%d", id), htmlFormData.newHttpEntity(), String.class);
    }
}
