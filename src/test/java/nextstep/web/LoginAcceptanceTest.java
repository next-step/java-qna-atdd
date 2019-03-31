package nextstep.web;

import nextstep.domain.UserRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Autowired
    private UserRepository userRepository;

    @Test
    public void login() throws Exception {
        String userId = "javajigi";
        String password = "test";
        String name = "자바지기";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", userId)
                .addParameter("password", password)
                .build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(userRepository.findByUserId(userId).isPresent()).isTrue();
        softly.assertThat(userRepository.findByUserId(userId).map(user -> user.matchPassword(password)).isPresent()).isTrue();
        softly.assertThat(userRepository.findByUserId(userId).map(user -> user.getName().equals(name)).isPresent()).isTrue();
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }
}
