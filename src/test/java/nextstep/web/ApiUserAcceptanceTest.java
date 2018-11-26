package nextstep.web;

import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;
import support.test.RestApiExecutor;
import support.test.RestApiResult;

import static nextstep.domain.UserTest.newUser;

public class ApiUserAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);

    @Test
    public void create() throws Exception {
        User newUser = newUser("testuser1");
        RestApiResult<Void> result = RestApiExecutor.ready(template(), Void.class).post()
                .request(newUser).url("/api/users").execute();
        softly.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = result.getResourceLocation();

        User dbUser = basicAuthTemplate(findByUserId(newUser.getUserId())).getForObject(location, User.class);
        softly.assertThat(dbUser).isNotNull();
    }

    @Test
    public void show_다른_사람() throws Exception {
        User newUser = newUser("testuser2");
        RestApiResult<Void> result = RestApiExecutor.ready(template(), Void.class).post()
                .request(newUser).url("/api/users").execute();
        softly.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = result.getResourceLocation();

        ResponseEntity<Void> getResponse = basicAuthTemplate(defaultUser()).getForEntity(location, Void.class);
        softly.assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update() throws Exception {
        User newUser = newUser("testuser3");
        RestApiResult<Void> result = RestApiExecutor.ready(template(), Void.class).post()
                .request(newUser).url("/api/users").execute();
        String location = result.getResourceLocation();
        softly.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        User original = basicAuthTemplate(newUser).getForObject(location, User.class);

        User updateUser = new User
                (original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");

        RestApiResult<User> putResult = RestApiExecutor.ready(basicAuthTemplate(newUser), User.class)
                .put().url(location).request(updateUser).execute();

        softly.assertThat(putResult.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateUser.equalsNameAndEmail(putResult.getBody())).isTrue();
    }

    @Test
    public void update_no_login() throws Exception {
        User newUser = newUser("testuser4");
        RestApiResult<Void> result = RestApiExecutor.ready(template(), Void.class).post()
                .request(newUser).url("/api/users").execute();
        String location = result.getResourceLocation();
        softly.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        User original = basicAuthTemplate(newUser).getForObject(location, User.class);

        User updateUser = new User
                (original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");

        RestApiResult<String> putResult = RestApiExecutor.ready(template(), String.class)
                .put().url(location).request(updateUser).execute();

        softly.assertThat(putResult.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", putResult.getBody());
    }

    @Test
    public void update_다른_사람() throws Exception {
        User newUser = newUser("testuser5");
        RestApiResult<Void> result = RestApiExecutor.ready(template(), Void.class).post()
                .request(newUser).url("/api/users").execute();
        softly.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = result.getResourceLocation();

        User updateUser = new User(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");

        RestApiResult<Void> putResult = RestApiExecutor.ready(basicAuthTemplate(defaultUser()), Void.class)
                .put().url(location).request(updateUser).execute();

        softly.assertThat(putResult.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
