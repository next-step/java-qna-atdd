package nextstep.web;

import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static nextstep.domain.UserTest.newUser;

public class ApiUserAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);

    @Test
    public void create() {
        final User testUser = newUser("tester");
        final String createdLocation = createResource("/api/users", testUser);
        final User dbUser = getResource(createdLocation, User.class, testUser);
        softly.assertThat(dbUser.equalsNameAndEmail(testUser));
    }

    @Test
    public void show_다른_사람() {
        final User testUser = newUser("tester1");
        final String createdLocation = createResource("/api/users", testUser);
        final ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).getForEntity(createdLocation, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update() {
        final User testUser = newUser("tester2");
        final String createdLocation = createResource("/api/users", testUser);
        final User updateUser = testUser
                .setEmail("update@test.com")
                .setName("update name");
        final ResponseEntity<User> responseEntity =
                basicAuthTemplate(testUser).exchange(createdLocation, HttpMethod.PUT, createHttpEntity(updateUser), User.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getBody().equalsNameAndEmail(updateUser)).isTrue();
    }

    @Test
    public void update_no_login() {
        final User testUser = newUser("tester3");
        final String createdLocation = createResource("/api/users", testUser);
        final User updateUser = testUser
                .setEmail("update@test.com")
                .setName("update name");
        final ResponseEntity<String> responseEntity =
                template().exchange(createdLocation, HttpMethod.PUT, createHttpEntity(updateUser), String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update_다른_사람() {
        final User testUser = newUser("tester4");
        final String createdLocation = createResource("/api/users", testUser);
        final User updateUser = testUser
                .setEmail("update@test.com")
                .setName("update name");
        final ResponseEntity<Void> responseEntity =
                basicAuthTemplate(defaultUser()).exchange(createdLocation, HttpMethod.PUT, createHttpEntity(updateUser), Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
