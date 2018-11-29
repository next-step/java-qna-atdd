package nextstep.web;

import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static nextstep.domain.UserTest.newUser;

@SuppressWarnings({"NonAsciiCharacters", "SpellCheckingInspection"})
public class ApiUserAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);

    @Test
    public void 사용자_생성() {

        final User newUser = newUser("testuser1");

        final ResponseEntity<Void> response = getResource(newUser);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        final String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(location).isNotNull();

        final User dbUser = basicAuthTemplate(findByUserId(newUser.getUserId())).getForObject(location, User.class);
        softly.assertThat(dbUser).isNotNull();
    }

    @Test
    public void 다른_사람의_정보_조회() {

        final User newUser = newUser("testuser2");

        ResponseEntity<Void> response = getResource(newUser);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        final String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(location).isNotNull();

        response = basicAuthTemplate(defaultUser()).getForEntity(location, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 로그인한_상태에서_자신의_정보_수정() {

        final User newUser = newUser("testuser3");

        final String location = createResource(newUser);

        final User original = basicAuthTemplate(newUser).getForObject(location, User.class);

        final User updateUser = new User(original.getId(), original.getUserId(), original.getPassword(), "javajigi2", "javajigi2@slipp.net");
        final ResponseEntity<User> responseEntity
                = basicAuthTemplate(newUser)
                .exchange(location, HttpMethod.PUT, createHttpEntity(updateUser), User.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateUser.equalsNameAndEmail(responseEntity.getBody())).isTrue();
    }

    @Test
    public void 로그인하지_않은_상태에서_사용자_정보_수정() {

        final User newUser = newUser("testuser4");

        final String location = createResource(newUser);

        final User original = basicAuthTemplate(newUser).getForObject(location, User.class);

        final User updateUser = new User(original.getId(), original.getUserId(), original.getPassword(), "javajigi2", "javajigi2@slipp.net");
        final ResponseEntity<String> responseEntity
                = template()
                .exchange(location, HttpMethod.PUT, createHttpEntity(updateUser), String.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        log.debug("error message : {}", responseEntity.getBody());
    }

    @Test
    public void 다른_사람_정보_수정() {

        final User newUser = newUser("testuser5");

        final String location = createResource(newUser);

        final User updateUser = new User(newUser.getUserId(), newUser.getPassword(), "name2", "javajigi@slipp.net2");
        final ResponseEntity<Void> responseEntity
                = basicAuthTemplate()
                .exchange(location, HttpMethod.PUT, createHttpEntity(updateUser), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<Void> getResource(final User newUser) {
        return template().postForEntity("/api/users", newUser, Void.class);
    }

    @SuppressWarnings("ConstantConditions")
    private String createResource(final User newUser) {
        final ResponseEntity<Void> response = getResource(newUser);
        final String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(location).isNotNull();
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return location;
    }

    private HttpEntity<Object> createHttpEntity(final Object body) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

}
