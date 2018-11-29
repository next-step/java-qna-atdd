package nextstep.web;

import nextstep.domain.User;
import nextstep.domain.UserRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HttpHelper;

public class UserAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = HttpHelper.get(template(), "/users/form");
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        String userId = "testuser";
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("userId", userId);
        params.add("password", "password");
        params.add("name", "자바지기");
        params.add("email", "javajigi@slipp.net");

        ResponseEntity<String> response = HttpHelper.post(template(), "/users", params);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(userRepository.findByUserId(userId).isPresent()).isTrue();
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }

    @Test
    public void list() throws Exception {
        ResponseEntity<String> response = HttpHelper.get(template(), "/users");
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains(defaultUser().getEmail());
    }

    @Test
    public void updateForm_no_login() throws Exception {
        ResponseEntity<String> response = HttpHelper.get(template(), String.format("/users/%d/form", defaultUser().getId()));
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void updateForm_login() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = HttpHelper.get(basicAuthTemplate(loginUser), String.format("/users/%d/form", loginUser.getId()));
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains(defaultUser().getEmail());
    }

    @Test
    public void update_no_login() throws Exception {
        ResponseEntity<String> response = update(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    private ResponseEntity<String> update(TestRestTemplate template) throws Exception {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("password", "test");
        params.add("name", "자바지기2");
        params.add("email", "javajigi@slipp.net");

        return HttpHelper.put(template, String.format("/users/%d", defaultUser().getId()), params);
    }

    @Test
    public void update() throws Exception {
        ResponseEntity<String> response = update(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }
}
