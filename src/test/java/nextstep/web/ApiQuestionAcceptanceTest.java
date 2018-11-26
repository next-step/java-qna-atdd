package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static nextstep.domain.UserTest.newUser;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    @Test
    public void create_login() throws Exception {

        User defaultUser = defaultUser();
        ResponseEntity<Void> response = template().postForEntity("/api/users", defaultUser, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();
    }
}
