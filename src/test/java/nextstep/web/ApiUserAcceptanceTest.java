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
    public void create() throws Exception {
        User newUser = newUser("testuser1");
        User dbUser = getResource(createResource("/api/users", newUser)
                , User.class, findByUserId(newUser.getUserId()));

        softly.assertThat(dbUser).isNotNull();
    }

    @Test
    public void show_다른_사람() throws Exception {
        User newUser = newUser("testuser2");
        ResponseEntity<Void> response = basicAuthTemplate(defaultUser())
                .getForEntity(createResource("/api/users", newUser), Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update() throws Exception {
        User newUser = newUser("testuser3");
        String location = createResource("/api/users", newUser);
        User original = getResource(location, User.class, newUser);
        User updateUser = new User
                (original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");

        ResponseEntity<User> responseEntity = getExchange(location, updateUser, newUser, User.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateUser.equalsNameAndEmail(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_no_login() throws Exception {
        User newUser = newUser("testuser4");
        String location = createResource("/api/users", newUser);
        User original = getResource(location, User.class, newUser);
        User updateUser = new User
                (original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");
        ResponseEntity<String> responseEntity = getExchange(template(), location, updateUser, String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }

    @Test
    public void update_다른_사람() throws Exception {
        User newUser = newUser("testuser5");
        User updateUser = new User(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");
        ResponseEntity<Void> responseEntity =
                getExchange(createResource("/api/users", newUser), updateUser, defaultUser(), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}
