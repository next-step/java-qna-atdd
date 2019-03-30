package nextstep.web;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Test
    public void loginForm() {
        ResponseEntity<String> response = template().getForEntity("/login/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void login() {
        String userId = "javajigi";
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        builder.addParameter("userId", userId);
        builder.addParameter("password", "test");
        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }

    @Test
    public void login_fail() {
        String userId = "javajigi";
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        builder.addParameter("userId", userId);
        builder.addParameter("password", "password");
        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
