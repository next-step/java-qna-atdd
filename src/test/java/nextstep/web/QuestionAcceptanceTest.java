package nextstep.web;

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
import support.test.AcceptanceTest;

import java.util.Objects;

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
    public void create() {
        User loginUser = defaultUser();

        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "title")
                .addParameter("contents", "test contents")
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findById(3L).isPresent()).isTrue();
        softly.assertThat(Objects.requireNonNull(response.getHeaders().getLocation()).getPath()).startsWith("/questions");
    }

    @Test
    public void list() {
        ResponseEntity<String> response = template().getForEntity("/questions", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody().contains("runtime")).isTrue();
    }

    @Test
    public void updateForm_no_login() {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", 2L),
                String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void updateForm_login() {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity(String.format("/questions/%d/form", 1L), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody().contains("국내에서")).isTrue();
    }

    private ResponseEntity<String> update(TestRestTemplate template) {

        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("password", "test")
                .addParameter("title", "국내에는 있을까?")
                .addParameter("contents", "국내에는 없다")
                .build();

        return template.postForEntity(String.format("/questions/%d",1L), request, String.class);
    }

    @Test
    public void update_no_login() {
        ResponseEntity<String> response = update(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void update() {
        ResponseEntity<String> response = update(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(Objects.requireNonNull(response.getHeaders().getLocation()).getPath()).startsWith("/questions");
    }

    @Test
    public void delete() {
        User loginUser = findByUserId("sanjigi");

        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity(String.format("/questions/%d", 2L), request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findById(2L).isPresent()).isTrue();
        softly.assertThat(Objects.requireNonNull(response.getHeaders().getLocation()).getPath()).startsWith("/questions");
    }

    @Test
    public void delete_impossible() {
        User loginUser = defaultUser();
        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity(String.format("/questions/%d", 1L), request, String.class);

        softly.assertThat(response.getBody().contains("error")).isTrue();
    }
}
