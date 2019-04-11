package nextstep.web;

import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;
import support.test.RestApiCallUtils;

import static nextstep.domain.UserTest.newUser;

public class ApiUserAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);
    private static final String BASE_URL = "/api/users";

    @Test
    public void create() throws Exception {
        // Given
        User newUser = newUser("testuser1");

        // When
        ResponseEntity<Void> response = RestApiCallUtils.createResource(
                template(), BASE_URL, newUser);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Given
        String location = response.getHeaders().getLocation().getPath();
        User byUserId = findByUserId(newUser.getUserId());

        // When
        User dbUser = RestApiCallUtils.getResource(
                basicAuthTemplate(byUserId), location, User.class).getBody();

        // Then
        softly.assertThat(dbUser).isNotNull();
    }

    @Test
    public void show_다른_사람() throws Exception {
        // Given
        User newUser = newUser("testuser2");

        // When
        ResponseEntity<Void> response = RestApiCallUtils.createResource(
                template(), BASE_URL, newUser);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Given
        String location = response.getHeaders().getLocation().getPath();

        // When
        response = RestApiCallUtils.getResource(
                basicAuthTemplate(selfUser()), location, Void.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update() throws Exception {
        // Given
        User newUser = newUser("testuser3");

        // When
        ResponseEntity<Void> response = RestApiCallUtils.createResource(
                template(), BASE_URL, newUser);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Given
        String location = response.getHeaders().getLocation().getPath();
        User original = RestApiCallUtils.getResource(
                basicAuthTemplate(newUser), location, User.class).getBody();
        User updateUser = new User
                (original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");

        // When
        ResponseEntity<User> responseEntity = RestApiCallUtils.updateResource(
                basicAuthTemplate(newUser), location, updateUser, User.class);

        // Then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateUser.equalsNameAndEmail(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_no_login() throws Exception {
        // Given
        User newUser = newUser("testuser4");

        // When
        ResponseEntity<Void> response = RestApiCallUtils.createResource(
                template(), BASE_URL, newUser);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Given
        String location = response.getHeaders().getLocation().getPath();
        User original = RestApiCallUtils.getResource(
                basicAuthTemplate(newUser), location, User.class).getBody();
        User updateUser = new User
                (original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");

        // When
        ResponseEntity<String> responseEntity = RestApiCallUtils.updateResource(template(), location, updateUser, String.class);

        // Then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }

    @Test
    public void update_다른_사람() throws Exception {
        // Given
        User newUser = newUser("testuser5");

        // When
        ResponseEntity<Void> response = RestApiCallUtils.createResource(template(), BASE_URL, newUser);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Given
        String location = response.getHeaders().getLocation().getPath();
        User updateUser = new User(newUser.getUserId(), "password",
                "name2", "javajigi@slipp.net2");

        // When
        ResponseEntity<Void> responseEntity = RestApiCallUtils.updateResource(
                basicAuthTemplate(selfUser()), location, updateUser, Void.class);

        // Then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
