package nextstep.web;

import nextstep.domain.User;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    @Test
    public void test_login_success() {
        String userId = "javajigi";
        HttpHeaders headers = new HttpHeaders();

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("userId", userId);
        params.add("password", "test");
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }

    @Test
    public void test_login_fail() {
        String userId = "no_user";

        HttpHeaders headers = new HttpHeaders();

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("userId", userId);
        params.add("password", "test");
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }
}
