package nextstep.web;

import nextstep.domain.UserRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.WebAcceptanceTest;

import static support.util.HtmlFormBuilder.builder;

public class LoginAcceptanceTest extends WebAcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/login", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }


    @Test
    public void login() throws Exception {
        // given
        String userId = "javajigi";
        String password = "test";

        MultiValueMap<String, Object> params = builder()
                .add("userId", userId)
                .add("password", password)
                .build();

        // when
        HttpEntity request = createWebRequestEntity(params);
        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(getResponseLocationPath(response)).startsWith("/");
    }

}
