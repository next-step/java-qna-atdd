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

    private static final String API_USERS = "/api/users";

    @Test
    public void create() throws Exception {
        User newUser = newUser("testuser1");
        ResponseEntity<Void> response = createResource(API_USERS, newUser, template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        User dbUser = basicAuthTemplate(findByUserId(newUser.getUserId())).getForObject(location, User.class);
        softly.assertThat(dbUser).isNotNull();
    }

    @Test
    public void show_다른_사람() throws Exception {
        User newUser = newUser("testuser2");
        ResponseEntity<Void> response = createResource(API_USERS, newUser, template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        response = basicAuthTemplate(defaultUser()).getForEntity(location, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update() throws Exception {
        User newUser = newUser("testuser3");
        ResponseEntity<Void> response = createResource(API_USERS, newUser, template());
        String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        User original = basicAuthTemplate(newUser).getForObject(location, User.class);

        User updateUser = new User
                (original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");

        ResponseEntity<User> responseEntity = updateResource(location, updateUser, basicAuthTemplate(newUser), User.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateUser.equalsNameAndEmail(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_no_login() throws Exception {
        User newUser = newUser("testuser4");
        ResponseEntity<Void> response = createResource(API_USERS, newUser, template());
        String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        User original = basicAuthTemplate(newUser).getForObject(location, User.class);

        User updateUser = new User
                (original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");

        ResponseEntity<User> responseEntity = updateResource(location, updateUser, template(), User.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }

    @Test
    public void update_다른_사람() throws Exception {
        User newUser = newUser("testuser5");
        ResponseEntity<Void> response = createResource(API_USERS, newUser, template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        User updateUser = new User(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");

        ResponseEntity<Void> responseEntity = updateResource(location, updateUser, basicAuthTemplate(defaultUser()), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
