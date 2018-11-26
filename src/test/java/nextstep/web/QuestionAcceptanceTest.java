package nextstep.web;

import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.helper.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import java.util.Arrays;
import java.util.Objects;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create_login() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = getMultiValueMapCreateAndDeleteHttpEntity();

        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void create_no_login() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = getMultiValueMapCreateAndDeleteHttpEntity();

        User loginUser = new User("hyunwoo", "1234", "김현우", "hyunwoo9283@gmail.com");
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void read_one_question() throws Exception {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", defaultUser().getId()), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());

        softly.assertThat(response.getBody()).contains(defaultUser().getName());
    }

    @Test
    public void update_my_question() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = getMultiValueUpdateMapHttpEntity();
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity(String.format("/questions/%d/update", defaultQuestion().getId()), request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        log.debug("body : {}",response.getBody());
    }

    @Test
    public void update_not_my_question() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = getMultiValueUpdateMapHttpEntity();
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity(String.format("/questions/%d/update", 2), request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void delete_my_question() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = getMultiValueMapCreateAndDeleteHttpEntity();

        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .exchange(String.format("/questions/%d/", defaultUser().getId()), HttpMethod.DELETE, request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void delete_not_my_question() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = getMultiValueMapCreateAndDeleteHttpEntity();

        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .exchange(String.format("/questions/%d/", 2), HttpMethod.DELETE, request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private HttpEntity<MultiValueMap<String, Object>> getMultiValueUpdateMapHttpEntity() {
        return HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "put")
                .addParameter("title", "createTitle")
                .addParameter("contents", "this is my first ATDD test")
                .build();
    }

    private HttpEntity<MultiValueMap<String, Object>> getMultiValueMapCreateAndDeleteHttpEntity() {
        return HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "createTitle")
                .addParameter("contents", "this is my first ATDD test")
                .build();
    }
}
