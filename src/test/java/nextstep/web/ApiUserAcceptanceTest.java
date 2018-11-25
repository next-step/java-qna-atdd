package nextstep.web;

import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static nextstep.domain.UserTest.newUser;

public class ApiUserAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);

    @Test
    public void create() {
        User newUser = newUser("testuser1");
        String location = createResource("/api/users", newUser);

        User dbUser = getResource(location, User.class, findByUserId(newUser.getUserId()));
        softly.assertThat(dbUser).isNotNull();
    }

    @Test
    public void show_다른_사람() {
        User newUser = newUser("testuser2");
        String location = createResource("/api/users", newUser);

        ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).getForEntity(location, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update() {
        User newUser = newUser("testuser3");

        String location = createResource("/api/users", newUser);

        User original = getResource(location, User.class, newUser);

        User updateUser = new User
                (original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");

        ResponseEntity<User> responseEntity = getResponseByExchage(location, createHttpEntity(updateUser), User.class, newUser, HttpMethod.PUT);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateUser.equalsNameAndEmail(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_no_login() {
        User newUser = newUser("testuser4");
        String location = createResource("/api/users", newUser);

        User original = getResource(location, User.class, newUser);

        User updateUser = new User(original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");

        ResponseEntity<String> responseEntity =
                template().exchange(location, HttpMethod.PUT, createHttpEntity(updateUser), String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }

    @Test
    public void update_다른_사람() {
        User newUser = newUser("testuser5");
        String location = createResource("/api/users", newUser);

        User updateUser = new User(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");

        ResponseEntity<Void> responseEntity =
                getResponseByExchage(location, createHttpEntity(updateUser), Void.class, defaultUser(), HttpMethod.PUT);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
