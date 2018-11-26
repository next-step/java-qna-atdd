package nextstep.web;

import nextstep.UnAuthenticationException;
import nextstep.service.UserService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);
    
    @Autowired
    private UserService userService;

    @Test
    public void login_success() throws UnAuthenticationException {
        String userId = "javajigi";
        String password = "test";
        ResponseEntity<String> response = login(userId, password);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(userService.login(userId, password).getUserId()).isEqualTo(userId);
    }

    @Test
    public void login_failed() {
        ResponseEntity<String> response = login("javajigi", "password");
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains("아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요.");
    }
    
    private ResponseEntity<String> login(String userId, String password) {
        HtmlFormData htmlFormData = HtmlFormData.urlEncodedFormBuilder()
                .addParameter("userId", userId)
                .addParameter("password", password)
                .build();

        ResponseEntity<String> response = template().postForEntity("/users/login", htmlFormData.newHttpEntity(), String.class);
        return response;
    }
}
