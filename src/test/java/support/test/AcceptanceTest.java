package support.test;

import nextstep.domain.User;
import nextstep.domain.UserRepository;
import nextstep.dto.ListResponse;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest extends BaseTest {
    protected static final String DEFAULT_LOGIN_USER = "javajigi";

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private UserRepository userRepository;

    public TestRestTemplate template() {
        return template;
    }

    public TestRestTemplate basicAuthTemplate() {
        return basicAuthTemplate(defaultUser());
    }

    public TestRestTemplate basicAuthTemplate(User loginUser) {
        return template.withBasicAuth(loginUser.getUserId(), loginUser.getPassword());
    }

    protected User defaultUser() {
        return findByUserId(DEFAULT_LOGIN_USER);
    }

    protected User findByUserId(String userId) {
        return userRepository.findByUserId(userId).get();
    }

    protected <T> ResponseEntity<ListResponse<T>> getListResource(String location, Class<T> responseType, User loginUser) {
        ResponseEntity<ListResponse<T>> response = basicAuthTemplate(loginUser).exchange(
            location, HttpMethod.GET,
            null, new ParameterizedTypeReference<ListResponse<T>>() {});
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        return response;
    }

    protected <T> ResponseEntity<T> getResource(String url, Class<T> responseType, User loginUser) {
        ResponseEntity<T> response = basicAuthTemplate(loginUser).getForEntity(url, responseType);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        return response;
    }

    protected <T> ResponseEntity<Void> createResource(String url, T bodyPayload, User loginUser) {
        ResponseEntity<Void> response = basicAuthTemplate(loginUser).postForEntity(url, bodyPayload, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        return response;
    }

    protected <P, T> ResponseEntity<T> updateResource(String url, P bodyPayload, Class<T> responseType, User loginUser) {
        ResponseEntity<T> response = basicAuthTemplate(loginUser).exchange(url,
            HttpMethod.PUT,
            createHttpEntity(bodyPayload),
            responseType);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        return response;
    }

    protected ResponseEntity<Void> deleteResource(String url, User loginUser) {
        ResponseEntity<Void> response = basicAuthTemplate(loginUser).exchange(url,
            HttpMethod.DELETE,
            null,
            Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        return response;
    }

    protected <P> HttpEntity createHttpEntity(P body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}
