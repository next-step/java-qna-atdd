package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.domain.UserTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import util.HtmlFormDataBuilder;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void form_show_success() {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body: {}", response.getBody());
    }

    @Test
    public void form_show_fail() {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body: {}", response.getBody());
    }

    @Test
    public void detail_user() {
        ResponseEntity<String> response = template().getForEntity("/questions/1", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains("자바지기");
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() {
        User user = new User(1, "javajigi", "test", "자바지기", "javajigi@slipp.net");
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", user.getUserId())
                .addParameter("title", "question title")
                .addParameter("contents", "question contents")
                .build();

        ResponseEntity<String> response = basicAuthTemplate(user).postForEntity("/questions", request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }

    @Test
    public void list() {
        PageRequest pageRequest = PageRequest.of(1, 10);
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/list", pageRequest),  String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void update_success() {
        Question question = questionRepository.findById(UserTest.JAVAJIGI.getId()).get();
        ResponseEntity<String> response = this.update(basicAuthTemplate(), question);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void update_fail() {
        Question question = questionRepository.findById(UserTest.JAVAJIGI.getId()).get();
        ResponseEntity<String> response = this.update(template(), question);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void delete_success() {
        Question question = questionRepository.findById(UserTest.JAVAJIGI.getId()).get();
        ResponseEntity<String> response = this.delete(basicAuthTemplate(), question);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void delete_fail() {
        Question question = questionRepository.findById(UserTest.JAVAJIGI.getId()).get();
        ResponseEntity<String> response = this.delete(template(), question);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    private ResponseEntity<String> update(TestRestTemplate template, Question question) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", "update title")
                .addParameter("contents", "update contnets")
                .build();

        return template.postForEntity(String.format("/questions/%d", question.getId()), request, String.class);
    }

    private ResponseEntity<String> delete(TestRestTemplate template, Question question) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();

        return template.postForEntity(String.format("/questions/%d", question.getId()), request, String.class);
    }
}
