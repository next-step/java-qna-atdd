package nextstep.web;

import com.sun.deploy.net.HttpUtils;
import nextstep.domain.UserRepository;
import nextstep.helper.HtmlFormDataBuilder;
import nextstep.security.HttpSessionUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import java.util.Arrays;

/**
 * UserService 클래스의 login() 메소드를 구현해 UserServiceTest의 모든 테스트 메소드가 성공해야 한다.
 * 기능을 구현한 후 UserServiceTest 테스트가 모두 통과하는지 확인한다.
 * UserController 로그인 기능 구현 메소드에서 UserService의 login() 메소드를 호출해 로그인 기능 처리를 위임한다.
 * 로그인에 성공하면 "redirect:/users"로 응답을 보낸다.
 * 로그인에 실패하면 templates/user 디렉토리의 login_failed.html을 응답으로 보낸다.
 * 로그인이 성공하면 로그인 기능을 담당하는 Controller 메소드에서 세션에 User를 저장해야 한다.
 * 세션에서 사용할 이름은 HttpSessionUtils.USER_SESSION_KEY를 사용한다.
 * 세션을 User를 저장하려면 HttpSession에 접근할 수 있어야 하는데 다음과 같이 구현할 수 있다.
 */
public class LoginAcceptanceTest extends AcceptanceTest {

    @Autowired
    UserRepository userRepository;

    /**
     * 로그인 성공
     */
    @Test
    public void login_success() {
        String userId = "javajigi";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParam("userId", userId)
                .addParam("password", "test")
                .build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(userRepository.findByUserId(userId).isPresent()).isTrue();
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }

    /**
     * 로그인 성공
     */
    @Test
    public void login_failed() {
        String userId = "javajigi";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParam("userId", userId)
                .addParam("password", "test2")
                .build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(userRepository.findByUserId(userId).isPresent()).isTrue();
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/templates/user/login_failed.html");
    }



}
