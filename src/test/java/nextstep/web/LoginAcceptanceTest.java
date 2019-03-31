package nextstep.web;

import nextstep.web.lib.HtmlFormDataBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    @Test
    public void loginForm() {
        ResponseEntity<String> response = template().getForEntity("/login", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void login_success() throws Exception {
        String userId = "sanjigi";
        String password = "test";
        ResponseEntity<String> response = login(userId, password);

        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void login_fail() throws Exception {
        String userId = "spring";
        String password = "stay";

        ResponseEntity<String> response = login(userId, password);
        log.debug(response.getBody());

        softly.assertThat(response.getBody()).contains("아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요.");
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private ResponseEntity<String> login(String userId, String password) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .post()
                .addParameter("userId", userId)
                .addParameter("password", password)
                .build();
        return template().postForEntity("/users/login", request, String.class);
    }
}
