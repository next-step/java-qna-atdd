package nextstep.web;

import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static nextstep.domain.UserTest.newUser;
import static org.assertj.core.api.Assertions.assertThat;

public class ApiUserAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);
    private final String API_USERS = "/api/users";

    @Test
    public void create() throws Exception {
        User newUser = newUser("testuser1");
        String location = createResourceLocation(API_USERS, newUser);
        User result = getResource(location, User.class, newUser);
        softly.assertThat(result).isNotNull();
    }

    @Test
    public void show_다른_사람() throws Exception {
        User newUser = newUser("testuser2");
        String location = createResourceLocation(API_USERS, newUser);
        User result = getResource(location, User.class, defaultUser());
        softly.assertThat(result).isNull();
    }

    @Test
    public void update() throws Exception {
        User newUser = newUser("testuser3");
        String location = createResourceLocation(API_USERS, newUser);
        User original = getResource(location, User.class, newUser);

        User updateUser = new User(original.getId(), original.getUserId(), original.getPassword(),
                "javajigi2", "javajigi2@slipp.net");

        ResponseEntity<User> responseEntity = putLoginResponseEntity(location, User.class, newUser, updateUser);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateUser.equalsNameAndEmail(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_no_login() throws Exception {
        User newUser = newUser("testuser44");
        String location = createResourceLocation(API_USERS, newUser);
        User original = getResource(location, User.class, newUser);

        User updateUser = new User(original.getId(), original.getUserId(), original.getPassword(),
                "javajigi2", "javajigi2@slipp.net");

        ResponseEntity<String> responseEntity = putResponseEntity(location, String.class, updateUser);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }

    @Test
    public void update_다른_사람() throws Exception {
        User newUser = newUser("testuser5");
        String location = createResourceLocation(API_USERS, newUser);
        User updateUser = new User(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");
        ResponseEntity<Void> responseEntity = putLoginResponseEntity(location, Void.class, defaultUser(), updateUser);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}
