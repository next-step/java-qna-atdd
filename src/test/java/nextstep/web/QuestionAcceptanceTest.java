package nextstep.web;

import helper.HtmlFormDataBuilder;
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
    public void createForm_no_login() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void createForm_login() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate()
                .getForEntity("/questions/form", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    private ResponseEntity<String> makeCreateResponseEntity(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .post()
                .addParameter("title", "본문 제목 입니다.")
                .addParameter("contents", "본문 내용 입니다~~~~")
                .build();

        return template.postForEntity("/questions/", request, String.class);
    }

    @Test
    public void create_login() throws Exception {
        ResponseEntity<String> response = makeCreateResponseEntity(basicAuthTemplate());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create_no_login() throws Exception {
        ResponseEntity<String> response = makeCreateResponseEntity(template());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void show() throws Exception {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", 1), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void show_no_exists() throws Exception {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", -1), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        log.debug("body : {}", response.getBody());
    }

    private ResponseEntity<String> makeUpdateResponseEntity(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", "제목을 수정합니다!")
                .addParameter("contents", "본문을 수정합니다!!!")
                .build();

        return template.postForEntity(String.format("/questions/%d", 1), request, String.class);
    }

    @Test
    public void update_no_login() throws Exception {
        ResponseEntity<String> response = makeUpdateResponseEntity(template());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void update_login_owner() throws Exception {
        ResponseEntity<String> response = makeUpdateResponseEntity(basicAuthTemplate());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions/");
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void update_login_not_owner() throws Exception {
        User otherUser = findByUserId("sanjigi");

        ResponseEntity<String> response = makeUpdateResponseEntity(basicAuthTemplate(otherUser));

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        log.debug("body : {}", response.getBody());
    }

    private ResponseEntity<String> makeDeleteResponseEntity(TestRestTemplate template, long id) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();

        return template.postForEntity(String.format("/questions/%d", id), request, String.class);
    }

    @Test
    public void delete_no_login() throws Exception {
        ResponseEntity<String> response = makeDeleteResponseEntity(template(), 2);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void delete_login_owner() throws Exception {
        ResponseEntity<String> response = makeDeleteResponseEntity(basicAuthTemplate(), 2);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void delete_login_not_owner() throws Exception {
        User otherUser = findByUserId("sanjigi");

        ResponseEntity<String> response = makeDeleteResponseEntity(basicAuthTemplate(otherUser), 3);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        log.debug("body : {}", response.getBody());
    }
}
