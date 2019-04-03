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
        // given
        User newUser = newUser("testuser1");
        String resourceLocation = createResource(newUser);

        // when
        ResponseEntity<User> response = getUserResourceResponseEntity(resourceLocation, newUser);
        User dbUser = response.getBody();

        // then
        softly.assertThat(dbUser).isNotNull();
    }

    @Test
    public void show_다른_사람() throws Exception {
        // given
        User newUser = newUser("testuser2");
        String resourceLocation = createResource(newUser);
        User otherUser = defaultUser();

        // when
        ResponseEntity<User> response = getUserResourceResponseEntity(resourceLocation, otherUser);

        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update() throws Exception {
        // given
        User newUser = newUser("testuser3");
        String resourceLocation = createResource(newUser);

        User original = getUserResourceResponseEntity(resourceLocation, newUser).getBody();
        User updateUser = new User
                (original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");

        // when
        ResponseEntity<User> response =
                putUserResourceResponseEntity(resourceLocation, createHttpEntity(updateUser), newUser);

        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateUser.equalsNameAndEmail(response.getBody())).isTrue();
    }

    @Test
    public void update_no_login() throws Exception {
        User newUser = newUser("testuser4");
        String resourceLocation = createResource(newUser);

        User original = getUserResourceResponseEntity(resourceLocation, newUser).getBody();
        User updateUser = new User
                (original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");

        ResponseEntity<String> responseEntity =
                template().exchange(resourceLocation, HttpMethod.PUT, createHttpEntity(updateUser), String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }

    @Test
    public void update_다른_사람() throws Exception {
        // given
        User newUser = newUser("testuser5");
        String resourceLocation = createResource(newUser);
        User otherUser = defaultUser();

        User original = getUserResourceResponseEntity(resourceLocation, newUser).getBody();
        User updateUser = new User
                (original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");

        // when
        ResponseEntity<User> responseEntity = putUserResourceResponseEntity(resourceLocation, createHttpEntity(updateUser), otherUser);

        // then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
