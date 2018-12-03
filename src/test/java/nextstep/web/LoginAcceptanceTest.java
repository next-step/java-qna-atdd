package nextstep.web;

import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.builder.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

public class LoginAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    private static final String LOGIN_URL = "/login";

    @Test
    public void loginForm() {
        ResponseEntity<String> response = template().getForEntity(LOGIN_URL, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.info("body : {}", response.getBody());
    }

    @Test
    public void login() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", "javajigi")
                .addParameter("password", "test").build();

        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(LOGIN_URL, request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        log.info("body : {}", response.getHeaders());
    }

    @Test
    public void loginFailed() {
        HttpEntity<MultiValueMap<String, Object>> request =  HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", "javajigi")
                .addParameter("password", "test11").build();
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(LOGIN_URL, request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.info("body : {}", response.getHeaders());
    }
}
